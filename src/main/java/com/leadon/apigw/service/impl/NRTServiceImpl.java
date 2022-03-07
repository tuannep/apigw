package com.leadon.apigw.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.model.AchCustomerInfo;
import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;
import com.leadon.apigw.repository.TransactionRepository;
import com.leadon.apigw.service.AchCustomerInfoService;
import com.leadon.apigw.service.NRTService;
import com.leadon.apigw.util.DateUtil;
import com.leadon.apigw.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.leadon.apigw.config.AppProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

    //@Autowired
   // private KafkaProducerService producer;

    @Override
    public DataObj fundTransferNRT(String iso8583Message) {
        String transId = "", eCode = "", eDesc = "";
        Transaction trans = new Transaction();
        TransAchDetail transAchDetail = new TransAchDetail();
        DataObj dataObj = new DataObj();
        try{
            JsonNode root = JsonUtil.toJsonNode(iso8583Message);
            parseNrtOut2Obj(root, trans, transAchDetail);
            dataObj = transactionRepository.initTrans(trans, transAchDetail);

            eCode = dataObj.getEcode();
            eDesc = dataObj.getEdesc();
            logger.debug("After Init trans, errCode: " + eCode + ", errDesc: " + eDesc + ", transId: "
                    + trans.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
            if (!AppConstant.SystemResponse.SUCCESS_CODE.equalsIgnoreCase(eCode)) {
                return dataObj;
            }
            transId = trans.getTransId().toString();
            // luu log msg
            //producer.pushMsgLogReq(transId, iso8583Message, trans.getChannelId(), AppConstant.LogConfig.BANK,AppConstant.LogConfig.CATEGORY_INTERNAL);
            //logger.debug("After producer.pushMsgLogReq");
        } catch (Exception e) {

            logger.error("Exception fundTransferNRT: " + e.getMessage());
            eCode = AppConstant.SystemResponse.EXCEPRION_ERROR_CODE;
            eDesc = AppConstant.SystemResponse.EXCEPRION_ERROR_DESC;
            dataObj.setEcode(eCode);
            dataObj.setEdesc(eDesc);
        } finally {
//            if (!DataUtil.isNullObject(trans.getTransId()))
//                transactionRepository.updateTransStatus(trans.getTransId(), objRes.getEcode(), objRes.getEdesc());
        }
        return dataObj;
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
}
