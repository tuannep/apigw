package com.leadon.apigw.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.dto.NPResponse;
import com.leadon.apigw.dto.RestDataObj;
import com.leadon.apigw.model.*;
import com.leadon.apigw.repository.TransAchActivityRepository;
import com.leadon.apigw.repository.TransactionRepository;
import com.leadon.apigw.service.AchCustomerInfoService;
import com.leadon.apigw.service.KafkaProducerService;
import com.leadon.apigw.service.NRTService;
import com.leadon.apigw.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.leadon.apigw.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

@Service("nrtService")
public class NRTServiceImpl implements NRTService {

    public static Logger logger = LoggerFactory.getLogger(NRTServiceImpl.class);

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AchCustomerInfoService achCustomerInfoService;

    @Autowired
    private KafkaProducerService producer;

    @Autowired
    private TransAchActivityRepository transAchActivityRepository;

    @Override
    public DataObj fundTransferNRT(String iso8583Message) {
        String transId = "", eCode = "", eDesc = "";
        Transaction trans = new Transaction();
        TransAchDetail transAchDetail = new TransAchDetail();
        DataObj objRes = new DataObj();
        try{
            JsonNode root = JsonUtil.toJsonNode(iso8583Message);
            parseNrtOut2Obj(root, trans, transAchDetail);
            objRes = transactionRepository.initTrans(trans, transAchDetail);

            eCode = objRes.getEcode();
            eDesc = objRes.getEdesc();
            logger.debug("After Init trans, errCode: " + eCode + ", errDesc: " + eDesc + ", transId: "
                    + trans.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
            if (!AppConstant.SystemResponse.SUCCESS_CODE.equalsIgnoreCase(eCode)) {
                return objRes;
            }
            transId = trans.getTransId().toString();
            // luu log msg
            producer.pushMsgLogReq(transId, iso8583Message, trans.getChannelId(), AppConstant.LogConfig.BANK,AppConstant.LogConfig.CATEGORY_INTERNAL);
            logger.debug("After producer.pushMsgLogReq");

            String year = new SimpleDateFormat("yyyy").format(new Date());
            String[] arrParam = {year + JsonUtil.getVal(root, "/body/iso8583/DE013_LOC_TRN_DATE").asText()
                    + JsonUtil.getVal(root, "/body/iso8583/DE012_LOC_TRN_TIME").asText()};
            String senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.PACS008,
                    transAchDetail.getDbtrMemId(), AppConstant.SenderRefType.SENDER_REF_NORMAL, arrParam);
            transAchDetail.setSenderRefId(senderRefId);
            // String jsonRequest = this.buildJsonFTReq(ftMsg, transId);
            // Push iso8583 message
            producer.pushIso8583Message(transId, senderRefId, iso8583Message, iso8583Message, AppConstant.LogConfig.REQUEST , AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.BANK,
                    AppConstant.LogConfig.CATEGORY_INTERNAL);
            String jsonRequest = MsgBuilder.buildPacs008(root, trans, transAchDetail);
            /// luu log callNapas va activity request
            /// insert log call napas va trans activity request
            producer.pushMsgLogReq(transId, jsonRequest, AppConstant.LogConfig.BANK, AppConstant.LogConfig.NAPAS,
                    AppConstant.LogConfig.CATEGORY_NAPAS);
            // Push activity request of bank iso8583 ach
            transAchActivityRepository.pushActivity(trans.getTransId(), senderRefId, AppConstant.MsgIdr.ISO8583,
                    "Core send message NRT iso8583 to ACH", AppConstant.LogConfig.REQUEST, iso8583Message, new Date(),
                    AppConstant.TransStep.ACT_STEP_SEND_NRT, AppConstant.SystemResponse.SUCCESS_CODE,
                    AppConstant.SystemResponse.SUCCESS_DESC);

            // Push activity request of send pacs008 to napas
            transAchActivityRepository.pushActivity(trans.getTransId(), senderRefId, AppConstant.MsgIdr.PACS008,
                    "Send pacs008 to Napas", AppConstant.LogConfig.REQUEST, jsonRequest, new Date(),
                    AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
                    AppConstant.SystemResponse.SUCCESS_DESC);
//            RestDataObj restData = NapasCaller.send2Napas(jsonRequest, AppConstant.MsgIdr.PACS008, senderRefId,
//                    AppConstant.ACHService.DIRECT_CREDIT);
            RestDataObj restData = new RestDataObj();
            restData = new RestDataObj();
            restData.setHttpStatus("200");
            restData.setResponse("{\"type\":\"success\",\"message\":\"Message successfully processed\",\"duplicated\":false}");

            objRes = handleFundTransferNrtNPResp(restData);
            Long activitiId= transAchActivityRepository.checkExsitIso8583ToBank(Long.parseLong(transId), AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
            logger.info("+++checkExsitIso8583ToBank:" + activitiId);
            if (!StringUtils.isEmpty(activitiId)) {
                return objRes;
            }
            else if(activitiId == null && objRes != null && !AppConstant.SystemResponse.NAPAS_RESPONSE_CODE_SUCCESS.equals(objRes.getEcode())){
                // get err code
//				DataObj dataObjNP = null;
//				dataObjNP = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
//						objRes.getEcode());
                String bankEcode = StringUtils.isEmpty(objRes.getEcode()) ? "30" : objRes.getEcode();
                String bankEdesc = StringUtils.isEmpty(objRes.getEdesc()) ? AppConstant.ResponseMsg.RESP_INVALID_MESSAGE : objRes.getEdesc();

                logger.debug("++++Starting push to queue send to bank!, error code:" + bankEcode + ", error desc:" + bankEdesc);
                // Push msg to queue
//                CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//                kkMsg.setSenderRefId(senderRefId);
//                kkMsg.setErrCode(bankEcode);
//                kkMsg.setErrDesc(bankEdesc);
//                kkMsg.setMessage(iso8583Message);
//                kkMsg.setMsgIdr(AppConstant.MsgIdr.ISO8583);
//                kkMsg.setActStep(AppConstant.TransStep.ACT_STEP_SEND_PACS008);
//                kkMsg.setActDesc("Send Pacs008 message ISO8583 to Bank");
//                kkMsg.setTransId(transId);
//                producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_NACK_IN_ISO8583);
            }
            logger.debug("After call napas, errCode: " + objRes.getEcode() + ", errDesc: " + objRes.getEdesc()
                    + ", transId: " + trans.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
            /// insert log call napas va tran activity response
            producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(restData), AppConstant.LogConfig.BANK,
                    AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);

            TransAchActivity transAchActivity = new TransAchActivity();
            transAchActivity.setTransId(Long.parseLong(transId));
            transAchActivity.setActivityDesc("Receiver from Napas");
            transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
            transAchActivity.setMsgContent(ACHUtil.parseObjectToString(restData));
            transAchActivity.setMsgIdentifier(AppConstant.MsgIdr.PACS008);
            transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
            transAchActivity.setSenderRefId(senderRefId);
            transAchActivity.setCreatedOn(new Date());
            transAchActivity.setMsgDt(new Date());
            transAchActivity.setErrCode(objRes.getEcode());
            transAchActivity.setErrDesc(objRes.getEdesc());

            transAchDetail.setMsgIdentifier(AppConstant.MsgIdr.PACS008);
            transAchDetail.setInstrId(transAchDetail.getInstrId());
            transAchDetail.setEndtoendId(transAchDetail.getEndtoendId());
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setTxId(senderRefId);
            transAchDetail.setTransStep(AppConstant.TransStep.ACT_STEP_PUTMX);
            transAchDetail.setOrgSenderRefId(senderRefId);
            transAchDetail.setSettleDt(transAchDetail.getSettleDt());
            transAchDetail.setChargeBr(transAchDetail.getChargeBr());
            transAchDetail.setErrDesc(objRes.getEdesc());
            transAchDetail.setErrCode(objRes.getEcode());

            DataObj dataObjLogHandle = transactionRepository.handleAchDetailActivity(transAchDetail, transAchActivity);
            String outEcode = dataObjLogHandle.getEcode();
            String outEdesc = dataObjLogHandle.getEdesc();

            logger.debug("After call handleAchDetailAct, errCode: " + outEcode + ", errDesc: " + outEdesc
                    + ", transId: " + trans.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
        } catch (Exception e) {

            logger.error("Exception fundTransferNRT: " + e.getMessage());
            eCode = AppConstant.SystemResponse.EXCEPRION_ERROR_CODE;
            eDesc = AppConstant.SystemResponse.EXCEPRION_ERROR_DESC;
            objRes.setEcode(eCode);
            objRes.setEdesc(eDesc);
        } finally {
            if (!DataUtil.isNullObject(trans.getTransId()))
                transactionRepository.updateTransStatus(trans.getTransId(), objRes.getEcode(), objRes.getEdesc());
        }
        return objRes;
    }


    private void parseNrtOut2Obj(JsonNode root, Transaction transaction, TransAchDetail transAchDetail) {
        try {
            // Transaction
            String xrefId = JsonUtil.getVal(root, "/body/iso8583/DE037_REL_REF_NO").asText() + UUID.randomUUID().toString().replace("-", "").substring(0,23).toUpperCase();
            String clientId = JsonUtil.getVal(root, "/body/iso8583/DE060_CNL_TP").asText();
            String channelId = JsonUtil.getVal(root, "/body/iso8583/DE060_CNL_TP").asText();
            String serviceGroupId = AppConstant.SrvGroup.ACH_NRT;
            String serviceId = AppConstant.ServiceId.SERVICE_NRT_OUT;
            String transCate = JsonUtil.getVal(root, "/body/iso8583/DE003_PROC_CD").asText();
            String transInOut = AppConstant.TransDirection.TRANS_OUT;
            String transDesc = JsonUtil.getVal(root, "/body/iso8583/DE104_TRN_CONT").asText();

            long amount = Long.parseLong(JsonUtil.getVal(root, "/body/iso8583/DE004_TRN_AMT").asText().substring(0,10));
//			String ccy = JsonUtil.getVal(root, "/body/iso8583/DE050_STL_CCY").asText();
            String ccy = appProperties.getProperty(JsonUtil.getVal(root, "/body/iso8583/DE049_TRN_CCY").asText());
            String transStat = "";
            String transStatDesc = "";
            String transDt = JsonUtil.getVal(root, "/body/iso8583/DE007_TRN_DT").asText();

            // Trans_ach_detail
            Long orgTransId = null;
            String senderRefId = "";
            String orgSenderRefId = "";
            String refSenderRefId = "";
            String msgIdentifier = AppConstant.MsgIdr.PACS008;
            long feeAmount = 0;
            long vatAmount = 0;
            Date settleDt = DateUtil.FORMAT_DATE_MMdd
                    .parse(JsonUtil.getVal(root, "/body/iso8583/DE015_STL_DATE").asText());
            //20201218 anhtn12 add stmt date format yyyy-MM-dd
            //old
            //String settleMtd = "";
            //new
            String settleMtd = DateUtil.formatDateYMD(settleDt);
            //end
            String instrId = "";
            String endtoendId = "";
            String txId = "";
            String chargeBr = AppConstant.PacsCommonConfig.PAYLOAD_CHRGBR;

            String dbtrBrn = "";
            String DE048_ADD_PRV_INF = JsonUtil.getVal(root, "/body/iso8583/DE048_ADD_PRV_INF").asText();
            String[] ADD_PRV_INF = null;
            String dbtrName = "";
            String dbtrAddress = "";
            if(DE048_ADD_PRV_INF.contains("\n"))
                ADD_PRV_INF = DE048_ADD_PRV_INF.split("\n");
            else if (DE048_ADD_PRV_INF.contains("\r"))
                ADD_PRV_INF = DE048_ADD_PRV_INF.split("\r");
            else {
                dbtrName = DE048_ADD_PRV_INF;
            }

            if (ADD_PRV_INF != null) {
                dbtrName = ADD_PRV_INF[0];
                if(1 < ADD_PRV_INF.length)
                    dbtrAddress = ADD_PRV_INF[1];
            }
            String dbtrAcctType = "";
            if (AppConstant.Common.ACCT.equals(transCate.substring(2, 4))) {
                dbtrAcctType = AppConstant.MethodType.ACCT_TYPE;
            } else {
                dbtrAcctType = AppConstant.MethodType.CARD_TYPE;
            }
            // begin anhtn12 20211103 change get dbtrAcct from DE002_PAN
            String dbtrAcctNo = JsonUtil.getVal(root, "/body/iso8583/DE002_PAN").asText();
            //String dbtrAcctNo = JsonUtil.getVal(root, "/body/iso8583/DE102_SND_ACC_INF").asText();
            // end anhtn12 20211103
            String dbtrMemId = JsonUtil.getVal(root, "/body/iso8583/DE032_ACQ_CD").asText();
            String dbtrMemCode = AppConstant.Common.SENDER_CODE;

            String cdtrAcctNo = JsonUtil.getVal(root, "/body/iso8583/DE103_RCV_ACC_INF").asText();
            String cdtrMemId = JsonUtil.getVal(root, "/body/iso8583/DE100_BEN_CD").asText();

            String cdtrBrn = "";
            String cdtrAddress = "";
            String cdtrAcctType = "";
            AchCustomerInfo achCustomerInfo = new AchCustomerInfo();
            String cdtrName;
            if (AppConstant.Common.ACCT.equals(transCate.substring(4, 6))) {
                cdtrAcctType = AppConstant.MethodType.ACCT_TYPE;

               achCustomerInfo = achCustomerInfoService.getAchCustomerById(cdtrAcctNo, cdtrMemId);
            } else {
                cdtrAcctType = AppConstant.MethodType.CARD_TYPE;

                achCustomerInfo = achCustomerInfoService.getAchCustomerByCardNo(cdtrAcctNo);
            }

            cdtrName = achCustomerInfo.getCdtrName();
            cdtrAddress = achCustomerInfo.getCdtrAddress();

            String cdtrMemCode = appProperties.getProperty(cdtrMemId);
            String coreRef = JsonUtil.getVal(root, "/body/iso8583/DE037_REL_REF_NO").asText();
            String trnRefNo = JsonUtil.getVal(root, "/body/iso8583/DE063_TRN_REF_NO").asText();
            String transDetail = transDesc;
            String transStep = "";
            String transStepStat = "";
            String errCode = "";
            String errDesc = "";
            String sessionNo = "";
            String groupStatus = "";
            String isCopy = "";
            String transType = AppConstant.Common.TRANS_TYPE_NRT;
            int numberOfTxs = 1;
            String maker = "";
            String modifier = "";
            String checker = "";
            String createdBy = "SYSTEM";

            transaction.setXrefId(xrefId);
            transaction.setClientId(clientId);
            transaction.setChannelId(channelId);
            transaction.setServiceGroupId(serviceGroupId);
            transaction.setServiceId(serviceId);
            transaction.setTransCate(transCate);
            transaction.setTransInout(transInOut);
            transaction.setTransDesc(transDesc);
            transaction.setAmount(BigDecimal.valueOf(amount));
            transaction.setCcy(ccy);
            transaction.setTransDt(transDt);
            transaction.setTransStat(transStat);
            transaction.setTransStatDesc(transStatDesc);

            transAchDetail.setOrgTransId(orgTransId);
            transAchDetail.setSenderRefId(senderRefId);
            transAchDetail.setOrgSenderRefId(orgSenderRefId);
            transAchDetail.setRefSenderRefId(refSenderRefId);
            transAchDetail.setMsgIdentifier(msgIdentifier);
            transAchDetail.setAmount(BigDecimal.valueOf(amount));
            transAchDetail.setCcy(ccy);
            transAchDetail.setFeeAmount(BigDecimal.valueOf(feeAmount));
            transAchDetail.setVatAmount(BigDecimal.valueOf(vatAmount));
            transAchDetail.setSettleDt(settleDt);
            transAchDetail.setSettleMtd(settleMtd);
            transAchDetail.setInstrId(instrId);
            transAchDetail.setEndtoendId(endtoendId);
            transAchDetail.setTxId(txId);
            transAchDetail.setChargeBr(chargeBr);
            transAchDetail.setDbtrBrn(dbtrBrn);
            transAchDetail.setDbtrName(dbtrName);
            transAchDetail.setDbtrAddress(dbtrAddress);
            transAchDetail.setDbtrAcctType(dbtrAcctType);
            transAchDetail.setDbtrAcctNo(dbtrAcctNo);
            transAchDetail.setDbtrMemId(dbtrMemId);
            transAchDetail.setDbtrMemCode(dbtrMemCode);
            transAchDetail.setCdtrBrn(cdtrBrn);
            transAchDetail.setCdtrName(cdtrName);
            transAchDetail.setCdtrAddress(cdtrAddress);
            transAchDetail.setCdtrAcctType(cdtrAcctType);
            transAchDetail.setCdtrAcctNo(cdtrAcctNo);
            transAchDetail.setCdtrMemId(cdtrMemId);
            transAchDetail.setCdtrMemCode(cdtrMemCode);
            transAchDetail.setCoreRef(coreRef);
            transAchDetail.setTrnRefNo(trnRefNo);
            transAchDetail.setTransDetail(transDetail);
            transAchDetail.setTransStep(transStep);
            transAchDetail.setTransStepStat(transStepStat);
            transAchDetail.setErrCode(errCode);
            transAchDetail.setErrDesc(errDesc);
            transAchDetail.setSessionNo(sessionNo);
            transAchDetail.setGroupStatus(groupStatus);
            transAchDetail.setIsCopy(isCopy);
            transAchDetail.setTransType(transType);
            transAchDetail.setNumberOfTxs(Integer.valueOf(numberOfTxs));
            transAchDetail.setMaker(maker);
            transAchDetail.setModifier(modifier);
            transAchDetail.setChecker(checker);
            transAchDetail.setCreatedBy(createdBy);
        } catch (Exception e) {
            logger.error("Exception when handle parseNrtOut2Obj:" + e.getMessage());
        }
    }

    private DataObj handleFundTransferNrtNPResp(RestDataObj restDataObj) {
        DataObj dataObj = new DataObj();
        try {
            String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
            if (restDataObj != null && restDataObj.getHttpStatus() != null && restDataObj.getResponse() != null) {
                if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
                    NPResponse npResponse = JsonUtil.parseJson2NPResponse(restDataObj.getResponse());
                    partnerCode = restDataObj.getHttpStatus().toUpperCase() + "_" + (StringUtils.isEmpty(npResponse.getType()) ? "NULL" : npResponse.getType().toUpperCase())
                            + "_" + (StringUtils.isEmpty(npResponse.getDuplicated()) ? "NULL" : npResponse.getDuplicated().toUpperCase());
                } else if ("".equals(restDataObj.getHttpStatus())) {
                    partnerCode = AppConstant.HTTPConfig.HTTP_STATUS_5XX;
                } else {
                    partnerCode = restDataObj.getHttpStatus();
                }
            } else {
                partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
            }
            logger.info("+++partnerCode handleFundTransferNrtNPResp:" + partnerCode);
            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
                    partnerCode);
        } catch (Exception e) {
            logger.error("Exception when handle handleFundTransferNrtNPResp:" + e.getMessage());
            dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
            dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
        }
        return dataObj;
    }
}
