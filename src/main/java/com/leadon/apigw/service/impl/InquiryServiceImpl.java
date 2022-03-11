package com.leadon.apigw.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.dto.InquiryTransactionDto;
import com.leadon.apigw.dto.NPResponse;
import com.leadon.apigw.dto.RestDataObj;
import com.leadon.apigw.model.*;
import com.leadon.apigw.repository.TransAchActivityRepository;
import com.leadon.apigw.repository.TransMessageISO8583Repository;
import com.leadon.apigw.repository.TransactionRepository;
import com.leadon.apigw.service.AchCustomerInfoService;
import com.leadon.apigw.service.InquiryService;
import com.leadon.apigw.service.KafkaProducerService;
import com.leadon.apigw.util.ACHUtil;
import com.leadon.apigw.util.DataUtil;
import com.leadon.apigw.util.JsonUtil;
import com.leadon.apigw.util.MsgBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.smartcardio.Card;
import java.text.SimpleDateFormat;
import java.util.Date;

@Service("inquiryService")
public class InquiryServiceImpl implements InquiryService {

    public static Logger logger = LoggerFactory.getLogger(InquiryServiceImpl.class);

    @Autowired
    private KafkaProducerService producer;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private TransAchActivityRepository transAchActivityRepository;

    @Autowired
    private TransMessageISO8583Repository transMessageISO8583Repository;

    @Autowired
    private AchCustomerInfoService achCustomerInfoService;

    @Override
    public String inquiryDAS(String iso8583Message) {
        logger.info("===== Begin Process InquiryDAS =====");
        String transId = "", eCode = "", eDesc = "";
        Transaction trans = new Transaction();
        TransAchDetail transAchDetail = new TransAchDetail();
        DataObj objRes = new DataObj();
        String iso8583toBank = "";
        String senderRefId = "";
        try {
            JsonNode root = JsonUtil.toJsonNode(iso8583Message);
            //// 1.init transaction
            ACHUtil.parseDasOut2Obj(root, trans, transAchDetail);
            DataObj initRes = transactionRepository.initTrans(trans, transAchDetail);
            eCode = initRes.getEcode();
//			eDesc = initRes.getEdesc();
            if (!AppConstant.SystemResponse.SUCCESS_CODE.equalsIgnoreCase(eCode)) {
                return MsgBuilder.buildISO8583DASNACK(iso8583Message, AppConstant.InquiryConfig.Das.DAS_ERROR_CODE_FORMAT);
            }
            transId = String.valueOf(trans.getTransId());
            // push log request
            producer.pushMsgLogReq(transId, iso8583Message, trans.getChannelId(), AppConstant.LogConfig.BANK,
                    AppConstant.LogConfig.CATEGORY_INTERNAL);

            String jsonRequest = MsgBuilder.buildDas(root, transId);

            String CRD_ACPT_TRM = JsonUtil.getVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM").asText();
            String ACQ_CD = JsonUtil.getVal(root, "/body/iso8583/DE032_ACQ_CD").asText();
            String year = new SimpleDateFormat("yyyy").format(new Date());
            String dateyyyyMMddHHmmss = year + JsonUtil.getVal(root, "/body/iso8583/DE013_LOC_TRN_DATE").asText()
                    + JsonUtil.getVal(root, "/body/iso8583/DE012_LOC_TRN_TIME").asText();
            String[] arrParam = {CRD_ACPT_TRM, JsonUtil.getVal(root, "/body/iso8583/DE011_TRACE_NO").asText(), dateyyyyMMddHHmmss};
            senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.PACS008, ACQ_CD,
                    AppConstant.SenderRefType.SENDER_REF_DAS, arrParam);

            // Push iso8583 message
            producer.pushIso8583Message(transId,  senderRefId, iso8583Message, iso8583Message, AppConstant.LogConfig.REQUEST, AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.NAPAS,
                    AppConstant.LogConfig.CATEGORY_INTERNAL);

            // insert log call napas va trans activity request
            producer.pushMsgLogReq(transId, jsonRequest, AppConstant.LogConfig.BANK, AppConstant.LogConfig.NAPAS,
                    AppConstant.LogConfig.CATEGORY_NAPAS);
            //

            // Push activity request of send das to napas
            transAchActivityRepository.pushActivity(Long.parseLong(transId), senderRefId, AppConstant.MsgIdr.DAS,
                    "Send DAS to Napas", AppConstant.LogConfig.REQUEST, jsonRequest, new Date(),
                    AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
                    AppConstant.SystemResponse.SUCCESS_DESC);
            //TOTO call goi sang doi tac
//            RestDataObj restData = NapasCaller.send2Napas(jsonRequest, AppConstant.MsgIdr.DAS, senderRefId,
//                    AppConstant.ACHService.DAS);
            RestDataObj restData = new RestDataObj();
            restData.setHttpStatus("200");
            restData.setResponse("{\"msgType\":\"0210\",\"processingCode\":\"432020\",\"transAmount\":\"000000000000\",\"transmissionDateTime\":\"0308172153\",\"systemTraceAuditNum\":\"000002\",\"localTime\":\"002153\",\"localDate\":\"0309\",\"settlementDate\":\"0309\",\"sendingMember\":\"970457\",\"retRefNumber\":\"206828323747\",\"authIdResponse\":\"000002\",\"responseCode\":\"00\",\"cardAcceptorTerminalId\":\"WOORIVN2\",\"additionalDataPrivate\":\"LE NGOC DO\\r365D\\/7, KP7, PHUONG TAN TIEN, TP. BIEN HOA, DONG NAI\",\"transCurrencyCode\":\"704\",\"usrDefinedField\":\"05\",\"serviceCode\":\"IF_INQ\",\"transRefNumber\":\"eBakxhqPKbgdwpL8\",\"receivingMember\":\"970415\",\"senderAcc\":\"100200074449\",\"receiverAcc\":\"103871380992\",\"contentTransfers\":\"LE NGOC DO\",\"accountHolderName\":\"TRAN THI XUAN\",\"PAN\":\"100200074449\"}");
            logger.debug("++++++++++restData NAPAS:::::" + restData.toString());

            objRes = handleDasOutNPResp(restData);
            logger.debug("++++++++++handleDasOutNPResp:::::" + objRes.toString());

            // insert log call napas va tran activity response
            producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(restData), AppConstant.LogConfig.BANK,
                    AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);

            // Push activity response of receive das from napas
            transAchActivityRepository.pushActivity(Long.parseLong(transId), senderRefId, AppConstant.MsgIdr.DAS,
                    "Receive DAS from Napas", AppConstant.LogConfig.RESPONSE, ACHUtil.parseObjectToString(restData),
                    new Date(), AppConstant.TransStep.ACT_STEP_PUTMX, objRes.getEcode(), objRes.getEdesc());

            // get mandatory pan to check reponse das from NP
            String responseNP = restData.getResponse();

            if (restData.getHttpStatus().equals(AppConstant.HTTPConfig.HTTP_STATUS_200) && ACHUtil.isNotEmtyDasResponseNP(responseNP)) {
                String globalId = JsonUtil.getVal(root, "/header/GLB_ID").asText();
                iso8583toBank = MsgBuilder.buildISO8583Das(restData.getResponse(), globalId);

                JsonNode iso8583toBankNode = JsonUtil.toJsonNode(iso8583toBank);

                // Push AchCustomerInfo
                if ("00".equals(JsonUtil.getVal(iso8583toBankNode, "/body/iso8583/DE039_RES_CD").asText()))
                    producer.pushAchCustomerInfo(iso8583toBank);

            } else {
                iso8583toBank = MsgBuilder.buildISO8583DASNACK(iso8583Message, AppConstant.InquiryConfig.Das.DAS_ERROR_CODE_SYSTEM);
            }


        } catch (Exception e) {
            logger.error("Exception: " + e.getMessage());
            eCode = AppConstant.InquiryConfig.Das.DAS_ERROR_CODE_EXCEPTION;
            eDesc = AppConstant.SystemResponse.EXCEPRION_ERROR_DESC;
            objRes.setEcode(eCode);
            objRes.setEdesc(eDesc);
            iso8583toBank = MsgBuilder.buildISO8583DASNACK(iso8583Message, AppConstant.InquiryConfig.Das.DAS_ERROR_CODE_EXCEPTION);
        } finally {
            if (!DataUtil.isNullOrEmpty(trans.getTransId().toString())) {
                transactionRepository.updateTransStatus(trans.getTransId(), objRes.getEcode(), objRes.getEdesc());
                transactionRepository.updateTransAchDetailStatus(trans.getTransId(), objRes.getEcode(), objRes.getEdesc());
            }
        }
        // push log response
        producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(objRes), trans.getChannelId(),
                AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);

        // Push iso8583 message
        producer.pushIso8583Message(transId,  senderRefId, iso8583toBank, iso8583toBank, AppConstant.LogConfig.RESPONSE, AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
                AppConstant.LogConfig.CATEGORY_INTERNAL);

        logger.info("+++ Response to Bank:" + iso8583toBank);

        return iso8583toBank;
    }

    @Override
    public InquiryTransactionDto inquiryTransactionNRT(String msgContent) {
        InquiryTransactionDto inquiryTransactionDto = new InquiryTransactionDto();
        Transaction transaction = new Transaction();
        TransAchDetail transAchDetail = new TransAchDetail();
        String errCode = "", errDesc = "";
        String transId = "";
        String orgTransId = null;
        JsonNode root = null;
        try {
            root = JsonUtil.toJsonNode(msgContent);
            DataObj dataObj = null;
            ACHUtil.initTransInquiryTransNRT(root, transaction, transAchDetail);

            dataObj = new DataObj();
            dataObj = transactionRepository.initTrans(transaction, transAchDetail);
            errCode = dataObj.getEcode();
            errDesc = dataObj.getEdesc();
            logger.debug("After Init trans, errCode: " + errCode + ", errDesc: " + errDesc + ", transId: "
                    + transaction.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
            if (!errCode.equals(AppConstant.SystemResponse.SUCCESS_CODE)) {
                inquiryTransactionDto.setErrorCode(errCode);
                inquiryTransactionDto.setErrorDesc(errDesc);
                return inquiryTransactionDto;
            }
            transId = String.valueOf(transaction.getTransId());
            inquiryTransactionDto.setTransId(transId);
            // push log request
            producer.pushMsgLogReq(transId, msgContent, JsonUtil.getVal(root, "/channelId").asText(),
                    AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);
            boolean validationMsg = ACHUtil.validationInquiryTransNRT(root, errCode, errDesc);
            if (!validationMsg) {
                inquiryTransactionDto.setErrorCode(errCode);
                inquiryTransactionDto.setErrorDesc(errDesc);
                return inquiryTransactionDto;
            }
            String orgXrefId = JsonUtil.getVal(root, "/orgXrefId").asText();
            dataObj = new DataObj();
            dataObj = transactionRepository.checkInvestgtnInq(orgXrefId, AppConstant.InquiryConfig.Invest.CHECK_BANK,
                    AppConstant.InquiryConfig.Invest.NUM_INQ_BANK, AppConstant.InquiryConfig.Invest.INQ_TIME_BANK,
                    AppConstant.InquiryConfig.Invest.NUM_INQ_NAPAS, AppConstant.InquiryConfig.Invest.NUM_DAY_INQ,
                    AppConstant.InquiryConfig.Invest.INQ_TIME_2_NAPAS,
                    AppConstant.InquiryConfig.Invest.INQ_TIME_3_NAPAS);
            String orgSenderRefId = dataObj.getDataVal("orgSenderRefId");
            errCode = dataObj.getEcode();
            errDesc = dataObj.getEdesc();

            logger.debug("After call checkInvestgtnInq, check: " + AppConstant.InquiryConfig.Invest.CHECK_BANK
                    + ",errCode: " + errCode + ", errDesc: " + errDesc + ", transId: " + transaction.getTransId()
                    + ", senderRef: " + transAchDetail.getSenderRefId());

            if (!errCode.equals(AppConstant.AchEcode.ECODE_SUCCESS)) {
                inquiryTransactionDto.setErrorCode(errCode);
                inquiryTransactionDto.setErrorDesc(errDesc);
                return inquiryTransactionDto;
            }
            orgTransId = dataObj.getDataVal("orgTransId");
            dataObj = new DataObj();
            dataObj = transactionRepository.getTransById(Long.parseLong(orgTransId));
            errCode = dataObj.getEcode();
            errDesc = dataObj.getEdesc();
            logger.debug("After call getTransById, errCode: " + errCode + ", errDesc: " + errDesc + ", transId: "
                    + transaction.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
            String isFinalState = dataObj.getDataVal("isFinalState");
            String orgTransDate = dataObj.getDataVal("createdOn");
            if (!errCode.equals(AppConstant.AchEcode.ECODE_SUCCESS)) {
                inquiryTransactionDto.setErrorCode(errCode);
                inquiryTransactionDto.setErrorDesc(errDesc);
                return inquiryTransactionDto;
            }

            if (isFinalState.equals("1")
                    || AppConstant.FunctionCode.GET_INQ_INVES.equals(JsonUtil.getVal(root, "/functionCode").asText())) {
                inquiryTransactionDto.setOrgXrefId(JsonUtil.getVal(root, "/orgXrefId").asText());
                inquiryTransactionDto.setDebitAccountNo(dataObj.getDataVal("dbtrAcctNo"));
                inquiryTransactionDto.setCreditAccountNo(dataObj.getDataVal("cdtrAcctNo"));
                inquiryTransactionDto.setAmount(dataObj.getDataVal("amount"));
                inquiryTransactionDto.setTransCode(dataObj.getDataVal("transCode"));
                inquiryTransactionDto.setTransDesc(dataObj.getDataVal("transDesc"));
                inquiryTransactionDto.setTransCodeDetail(dataObj.getDataVal("transCodeDetail"));
                inquiryTransactionDto.setTransDescDetail(dataObj.getDataVal("transDescDetail"));
                inquiryTransactionDto.setTransDate(dataObj.getDataVal("createdOn"));
                inquiryTransactionDto.setCdtrMemId(dataObj.getDataVal("cdtrMemId"));
                inquiryTransactionDto.setErrorCode(AppConstant.AchEcode.ECODE_SUCCESS);
                inquiryTransactionDto.setErrorDesc(AppConstant.AchEcode.EDESC_SUCCESS);
                return inquiryTransactionDto;
            } else {
                if (AppConstant.FunctionCode.SEND_INQ_INVES.equals(JsonUtil.getVal(root, "/functionCode").asText())) {
                    dataObj = new DataObj();
                    dataObj = transactionRepository.checkInvestgtnInq(orgXrefId,
                            AppConstant.InquiryConfig.Invest.CHECK_NAPAS, AppConstant.InquiryConfig.Invest.NUM_INQ_BANK,
                            AppConstant.InquiryConfig.Invest.INQ_TIME_BANK,
                            AppConstant.InquiryConfig.Invest.NUM_INQ_NAPAS,
                            AppConstant.InquiryConfig.Invest.NUM_DAY_INQ,
                            AppConstant.InquiryConfig.Invest.INQ_TIME_2_NAPAS,
                            AppConstant.InquiryConfig.Invest.INQ_TIME_3_NAPAS);

                    String isCallNapas = dataObj.getDataVal("isCallNapas");
                    errCode = dataObj.getEcode();
                    errDesc = dataObj.getEdesc();
                    logger.debug("After call checkInvestgtnInq, check: " + AppConstant.InquiryConfig.Invest.CHECK_NAPAS
                            + ", errCode: " + errCode + ", errDesc: " + errDesc + ", transId: "
                            + transaction.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
                    if (!errCode.equals(AppConstant.AchEcode.ECODE_SUCCESS)) {
                        if (isCallNapas == null) {
                            inquiryTransactionDto.setErrorCode(errCode);
                            inquiryTransactionDto.setErrorDesc(errDesc);
                            return inquiryTransactionDto;
                        } else if (isCallNapas.equals("0")) {
                            inquiryTransactionDto.setOrgXrefId(JsonUtil.getVal(root, "/orgXrefId").asText());
                            inquiryTransactionDto.setDebitAccountNo(dataObj.getDataVal("dbtrAcctNo"));
                            inquiryTransactionDto.setCreditAccountNo(dataObj.getDataVal("cdtrAcctNo"));
                            inquiryTransactionDto.setAmount(dataObj.getDataVal("amount"));
                            inquiryTransactionDto.setTransCode(dataObj.getDataVal("transCode"));
                            inquiryTransactionDto.setTransDesc(dataObj.getDataVal("transDesc"));
                            inquiryTransactionDto.setTransCodeDetail(dataObj.getDataVal("transCodeDetail"));
                            inquiryTransactionDto.setTransDescDetail(dataObj.getDataVal("transDescDetail"));
                            inquiryTransactionDto.setTransDate(dataObj.getDataVal("createdOn"));
                            inquiryTransactionDto.setCdtrMemId(dataObj.getDataVal("cdtrMemId"));
                            inquiryTransactionDto.setErrorCode(AppConstant.AchEcode.ECODE_SUCCESS);
                            inquiryTransactionDto.setErrorDesc(AppConstant.AchEcode.EDESC_SUCCESS);
                            return inquiryTransactionDto;
                        }
                    }
                } else {
                    inquiryTransactionDto.setOrgXrefId(JsonUtil.getVal(root, "/orgXrefId").asText());
                    inquiryTransactionDto.setDebitAccountNo(dataObj.getDataVal("dbtrAcctNo"));
                    inquiryTransactionDto.setCreditAccountNo(dataObj.getDataVal("cdtrAcctNo"));
                    inquiryTransactionDto.setAmount(dataObj.getDataVal("amount"));
                    inquiryTransactionDto.setTransCode(dataObj.getDataVal("transCode"));
                    inquiryTransactionDto.setTransDesc(dataObj.getDataVal("transDesc"));
                    inquiryTransactionDto.setTransCodeDetail(dataObj.getDataVal("transCodeDetail"));
                    inquiryTransactionDto.setTransDescDetail(dataObj.getDataVal("transDescDetail"));
                    inquiryTransactionDto.setTransDate(dataObj.getDataVal("createdOn"));
                    inquiryTransactionDto.setCdtrMemId(dataObj.getDataVal("cdtrMemId"));
                    inquiryTransactionDto.setErrorCode(AppConstant.AchEcode.ECODE_SUCCESS);
                    inquiryTransactionDto.setErrorDesc(AppConstant.AchEcode.EDESC_SUCCESS);
                    return inquiryTransactionDto;
                }
            }
            String senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.PACS028,
                    AppConstant.PacsCommonConfig.SENDER_ID, AppConstant.SenderRefType.SENDER_REF_NORMAL, null);
            // call to napas
            // set lai gia tri sender code
            String requestBody = MsgBuilder.buildPacs028(senderRefId, orgTransId,
                    orgSenderRefId, orgTransDate);
            if ("".equals(requestBody)) {
                inquiryTransactionDto.setErrorCode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
                inquiryTransactionDto.setErrorDesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
            }

            // Push activity request of send pacs028 to napas
            transAchActivityRepository.pushActivity(Long.parseLong(orgTransId), senderRefId, AppConstant.MsgIdr.PACS028,
                    "Send pacs028 to Napas", AppConstant.LogConfig.REQUEST, requestBody, new Date(),
                    AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
                    AppConstant.SystemResponse.SUCCESS_DESC);
            // TODO: callto napas
//            RestDataObj restData = NapasCaller.send2Napas(requestBody, AppConstant.MsgIdr.PACS028, senderRefId,
//                    AppConstant.ACHService.INVESTIGATION_TRANS);
            // TODO: restData
            RestDataObj restData = new RestDataObj();
            DataObj objRes = handleInqTransNrtNPResp(restData);

            logger.debug("After call napas, errCode: " + objRes.getEcode() + ", errDesc: " + objRes.getEdesc()
                    + ", transId: " + transaction.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());

            // Call PR_MAP_ERROR_CODE
            inquiryTransactionDto.setErrorCode(objRes.getEcode());
            inquiryTransactionDto.setErrorDesc(objRes.getEdesc());

            // Push activity response
            transAchActivityRepository.pushActivity(Long.parseLong(orgTransId), senderRefId, AppConstant.MsgIdr.PACS028,
                    "Receive pacs028 to Napas", AppConstant.LogConfig.RESPONSE, ACHUtil.parseObjectToString(restData),
                    new Date(), AppConstant.TransStep.ACT_STEP_PUTMX, inquiryTransactionDto.getErrorCode(),
                    inquiryTransactionDto.getErrorDesc());

            // update transaciton va trans_detail
           /* if (!DataUtil.isNullOrEmpty(transaction.getTransId().toString())) {
                transAchDetail = ACHUtil.hadleUpdateTranStatusAndTransDetail(transaction.getTransId(), orgTransId, inquiryTransactionDto.getErrorCode(), inquiryTransactionDto.getErrorDesc());
                transactionRepository.updateTransAchDetail(transAchDetail);
                transactionRepository.updateTransStatusUpdated(transAchDetail.getTransId(), inquiryTransactionDto.getErrorCode(), inquiryTransactionDto.getErrorDesc());
//                transactionRepository.updateTransAchDetailStatus(transaction.getTransId(), inquiryTransactionDto.getErrorCode(), inquiryTransactionDto.getErrorDesc());
            }
            if (!DataUtil.isNullOrEmpty(orgTransId)) {
                // sua lai phai update ca org_trans_id
                transAchDetail = ACHUtil.hadleUpdateTranStatusAndTransDetail(Long.parseLong(orgTransId), null, inquiryTransactionDto.getErrorCode(), inquiryTransactionDto.getErrorDesc());
                transactionRepository.updateTransAchDetail(transAchDetail);
                transactionRepository.updateTransStatusUpdated(Long.parseLong(orgTransId), inquiryTransactionDto.getErrorCode(), inquiryTransactionDto.getErrorDesc());
//                transactionRepository.updateTransAchDetailStatus(Long.parseLong(orgTransId), inquiryTransactionDto.getErrorCode(), inquiryTransactionDto.getErrorDesc());
            } */
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception when handle inquiryTransactionNRT:" + e.getMessage());
            inquiryTransactionDto.setErrorCode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
            inquiryTransactionDto.setErrorDesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
        }
        return inquiryTransactionDto;
    }

//    @Override
//    public DataObj inquiryTransactionNRTCore(String msgContent) {
//        logger.info("=====Starting do inquiryTransactionNRTCore======= ");
//        DataObj dataObj1 = new DataObj();
//        Transaction transaction = new Transaction();
//        TransAchDetail transAchDetail = new TransAchDetail();
//        String errCode = "", errDesc = "";
//        String transId = "";
//        JsonNode root = null;
//        try {
//            root = JsonUtil.toJsonNode(msgContent);
//
//            DataObj dataObj2 = new DataObj();
//            String de090 = JsonUtil.getVal(root, "/body/iso8583/DE090_ORG_TRN_KEY").asText();
//            dataObj2 = transMessageISO8583Repository.getPacs008FromIso8583(de090.substring(0, 4), de090.substring(4, 10), de090.substring(10, 20), de090.substring(25, 31));
//            String orgXrefId = dataObj2.getDataVal("orgXrefId");
//            String channelId = dataObj2.getDataVal("channelId");
//            String transDt = dataObj2.getDataVal("transDt");
//            String transType = dataObj2.getDataVal("transType");
//            String orgMessageIso8583 = dataObj2.getDataVal("message");
//            String orgSenderRefId = dataObj2.getDataVal("orgSenderRefId");
//            String orgTransId = dataObj2.getDataVal("orgTransId");
//            String errCodeGetNrtMx = dataObj2.getEcode();
//            String errDescGetNrtMx = dataObj2.getEdesc();
//
//            logger.info("+++ orgXrefId:" + orgXrefId + ", orgSenderRefId:" + orgSenderRefId + ", channelId" + channelId + ", transDt" + transDt + ", transType" + transType
//                    + ", errCodeGetNrtMx" + errCodeGetNrtMx + ", errDescGetNrtMx" + errDescGetNrtMx);
//
//            // Push iso8583 message
//            producer.pushIso8583Message(StringUtils.isEmpty(orgTransId) ? "0" : orgTransId, orgSenderRefId, msgContent, msgContent, AppConstant.LogConfig.REQUEST, AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.BANK,
//                    AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//            DataObj dataObj = null;
//            String xrefId = JsonUtil.getVal(root, "/body/iso8583/DE037_REL_REF_NO").asText() + UUID.randomUUID().toString().replace("-", "").substring(0, 23).toUpperCase();
//            String newTransDt = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
//            ACHUtil.initTransInquiryTransNRTCore(root, transaction, transAchDetail, xrefId, channelId, newTransDt, transType);
//
//            dataObj = new DataObj();
//            dataObj = transactionRepository.initTrans(transaction, transAchDetail);
//            errCode = dataObj.getEcode();
//            errDesc = dataObj.getEdesc();
//            logger.debug("After Init trans, errCode: " + errCode + ", errDesc: " + errDesc + ", transId: "
//                    + transaction.getTransId() + ", senderRef: " + transAchDetail.getSenderRefId());
//            dataObj1.setEcode(errCode);
//            dataObj1.setEdesc(errDesc);
//
//            if ("A3".equals(errCodeGetNrtMx) || "EX".equalsIgnoreCase(errCodeGetNrtMx)) {
//                // Push msg to queue
//                CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//                if ("A3".equals(errCodeGetNrtMx))
//                    kkMsg.setErrCode("12");
//                else
//                    kkMsg.setErrException(AppConstant.SystemResponse.EXCEPRION_ERROR_CODE);
//                kkMsg.setErrDesc(errDescGetNrtMx);
//                kkMsg.setMessage(msgContent);
//
//                producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_INQUIRY_PAYMENT_OUT);
//
//                return new DataObj(errCodeGetNrtMx, errDescGetNrtMx, "");
//            } else if (AppConstant.AchEcode.ECODE_SUCCESS.equals(errCode)) {
//                // Push msg to queue
//                CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//                kkMsg.setOrgXrefId(orgXrefId);
//                kkMsg.setTransId(String.valueOf(transaction.getTransId()));
//                kkMsg.setErrCode(errCode);
//                kkMsg.setErrDesc(errDesc);
//                kkMsg.setMessage(msgContent);
//                kkMsg.setOrgMessage(orgMessageIso8583);
//                kkMsg.setOrgSenderRefId(orgSenderRefId);
//                kkMsg.setSenderRefId(transAchDetail.getSenderRefId());
//                kkMsg.setFunctionCode(AppConstant.FunctionCode.SEND_INQ_INVES);
//                kkMsg.setChanelId(channelId);
//                producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_INQUIRY_PAYMENT_OUT);
//            } else {
//                dataObj1.setEcode(errCode);
//                dataObj1.setEdesc(errDesc);
//                return dataObj1;
//            }
//            transId = String.valueOf(transaction.getTransId());
//            // push log request
//            producer.pushMsgLogReq(StringUtils.isEmpty(transId) ? "0" : transId, msgContent, channelId,
//                    AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Exception when handle inquiryTransactionNRTCore:" + e.getMessage());
//            dataObj1.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_EXCEPTION_CODE);
//            dataObj1.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
//
//			// Push msg to queue
//            if (ACHUtil.isJSON8583Valid(msgContent)) {
//                CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//                kkMsg.setErrException(AppConstant.SystemResponse.SYSTEM_ERROR_EXCEPTION_CODE);
//                kkMsg.setErrDesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
//                kkMsg.setMessage(msgContent);
//
//                producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_INQUIRY_PAYMENT_OUT);
//            } else {
//                logger.error("Invalid json format iso 8583" + e.getMessage());
//            }
//        }
//        return dataObj1;
//    }
//
//    public DataObj handlePacs028(JsonNode root, String message) {
//        String transId = "";
//        Map<String, String> map = new HashMap<>();
//        try {
//            String errorCode = "", errorDesc = "";
//            Transaction transaction = new Transaction();
//            TransAchDetail transAchDetail = new TransAchDetail();
//            TransAchActivity transAchActivity = new TransAchActivity();
//            // build init trans
//            ACHUtil.initTransPacs028(root, transaction, transAchDetail, transAchActivity);
//            // CALL PR_GET_INVESTGTN_IN
//            DataObj dataObj2 = null;
//            dataObj2 = transactionRepository.getInvestgtnIn(transAchDetail.getOrgSenderRefId());
//            String rootTransId = dataObj2.getDataVal("rootTransId");
//            String msgPacs008 = dataObj2.getDataVal("msgContent");
//            String counTimeInvest = dataObj2.getDataVal("counTimeInvest");
//            errorCode = StringUtils.isEmpty(dataObj2.getEcode()) ? errorCode : dataObj2.getEcode();
//            errorDesc = StringUtils.isEmpty(dataObj2.getEdesc()) ? errorDesc : dataObj2.getEdesc();
//            logger.info("+++++++++ errcode:" + errorCode + "+++++++++errDes:" + errorDesc + "++++data:" + dataObj2.getData()
//                        + ", counTimeInvest: " + counTimeInvest);
//            if (Integer.parseInt(counTimeInvest) < 2) {
//                // Init trans
//                DataObj dataObj = transactionRepository.initTrans(transaction, transAchDetail, transAchActivity);
//                logger.trace("After Init trans, errCode: " + dataObj.getEcode() + ", errDesc: " + dataObj.getEdesc() + ", transId: "
//                        + transaction.getTransId());
//                transId = String.valueOf(transaction.getTransId());
//            }
//            // push log request
//            producer.pushMsgLogReq(transId, message, AppConstant.ChannelId.ACH, AppConstant.LogConfig.BANK,
//                    AppConstant.LogConfig.CATEGORY_NAPAS);
//
//            if ("0".equals(rootTransId) || "A3".equals(dataObj2.getEcode())) { // Don't have Investgtn mesage in DB
//                String orgMsgDefine = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlMsgNmId").asText();
//                String orgTxId = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsReq/TxInf/OrgnlTxId").asText();
//                String orgSettleDate = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsReq/TxInf/OrgnlTxRef/IntrBkSttlmDt").asText();
//                String orgSenderCode = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsReq/TxInf/InstgAgt/FinInstnId/ClrSysMmbId/MmbId").asText();
//                String orgSenderRefId = transAchDetail.getOrgSenderRefId();
//                String senderRefId = JsonUtil.getVal(root, "/Header/SenderReference").asText();
//
//                //Puhs to kafka
//                CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//                kkMsg.setMsgIdr(orgMsgDefine);
//                kkMsg.setOrgTxId(orgTxId);
//                kkMsg.setOrgSettleDate(orgSettleDate);
//                kkMsg.setOrgSenderCode(orgSenderCode);
//                kkMsg.setOrgSenderRefId(orgSenderRefId);
//                kkMsg.setSenderRefId(senderRefId);
//                producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_INQUIRY_IN);
//            } else if (!StringUtils.isEmpty(msgPacs008) && !AppConstant.SystemResponse.TIMEOUT_ERROR_CODE.equals(errorCode)) {
//                JsonNode rootPacs008 = JsonUtil.toJsonNode(msgPacs008);
//                String msgIdr =  JsonUtil.getVal(rootPacs008, "/Header/MessageIdentifier").asText();
//                // Push msg to queue
//                CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//                kkMsg.setTransId(String.valueOf(transaction.getTransId()));
//                kkMsg.setErrCode(errorCode);
//                kkMsg.setErrDesc(errorDesc);
//                kkMsg.setMessage(msgPacs008);
//                kkMsg.setSenderRefId(transAchDetail.getOrgSenderRefId());
//                if (AppConstant.MsgIdr.CAMT034.equals(msgIdr)) {
//                    kkMsg.setMessage(ACHUtil.parseObjectToString(JsonUtil.getVal(rootPacs008, "/Payload/Document/Dplct/Dplct/Data/Any")));
//                    kkMsg.setMsgIdr(AppConstant.MsgIdr.CAMT034);
//                }
//                producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_ACH_IN_SEND);
//            } else if (errorCode.equals(AppConstant.SystemResponse.DUPLICATE_XREF_ID_CODE)) {
//                map.put("duplicated", AppConstant.ResponseDupl.RESP_DUPLICATED);
//                return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//                        AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, map);
//            } else {
//                map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//                return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//                        AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, map);
//            }
//        } catch (Exception e) {
//            logger.error("Exception when handle handlePacs028:" + e.getMessage());
//            map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//            return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE, AppConstant.ResponseMsg.RESP_FAIL_MESSAGE,
//                    map);
//        }
//        map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//        return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
//                map);
//    }
//
    @Override
    public DAS inquiryDASInComing(String senderId, String senderRefId, String msgContent) {
        logger.info(
                "Received msg from Napas, senderId: " + senderId + ", senderRefId:" + senderRefId);
        logger.debug("message: " + msgContent);

        DAS das = new DAS();
        JsonNode root = null;
        Card card = null;
//		Account account = null;
        String accountHolderName = "";
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String errorCode = "", errorDesc = "", isCardFlag = "", authIdResponse = "";
            root = JsonUtil.toJsonNode(msgContent);
            Transaction transaction = new Transaction();
            TransAchDetail transAchDetail = new TransAchDetail();
            TransAchActivity transAchActivity = new TransAchActivity();
            DataObj dataObj = null;
            ACHUtil.initTransDasIn(root, transaction, transAchDetail, transAchActivity);

            dataObj = new DataObj();
            dataObj = transactionRepository.initTrans(transaction, transAchDetail, transAchActivity);
            errorCode = dataObj.getEcode();
            errorDesc = dataObj.getEdesc();
            if (!AppConstant.AchEcode.ECODE_SUCCESS.equals(errorCode)) {
                // push log res
                producer.pushMsgLogRes(String.valueOf(transaction.getTransId()), objectMapper.writeValueAsString(das),
                        AppConstant.ChannelId.ACH, AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_NAPAS);
                das = ACHUtil.buildResponseDasIn(root, errorCode, accountHolderName, "000000");
                return das;
            }
            // push log request
            producer.pushMsgLogReq(String.valueOf(transaction.getTransId()), msgContent, AppConstant.ChannelId.ACH,
                    AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_NAPAS);

            if (JsonUtil.getVal(root, "/processingCode").asText().substring(4).equals(AppConstant.Common.CARD)) {
                isCardFlag = AppConstant.Common.IS_CARD_FLAG;
//                if (JsonUtil.getVal(root, "/receiverAcc").asText().length() != Integer
//                        .parseInt(AppConstant.Common.CARD_LENGTH)) {
//                    errorCode = "44";
//                    errorDesc = "Card invalid";
//                }
//                if (!JsonUtil.getVal(root, "/receiverAcc").asText().startsWith(AppConstant.Common.SENDER_ID)) {
//                    errorCode = "44";
//                    errorDesc = "Card invalid";
//                }
            } else if (JsonUtil.getVal(root, "/processingCode").asText().substring(4).equals(AppConstant.Common.ACCT)) {
                isCardFlag = AppConstant.Common.IS_ACCT_FLAG;
                if (!ACHUtil.validateAccount(JsonUtil.getVal(root, "/receiverAcc").asText())) {
                    errorCode = "38";
                    errorDesc = "Account invalid";
                }
            }

            if (!AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
                // push log res
                producer.pushMsgLogRes(String.valueOf(transaction.getTransId()), objectMapper.writeValueAsString(das),
                        AppConstant.ChannelId.ACH, AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_NAPAS);
                das = ACHUtil.buildResponseDasIn(root, errorCode, accountHolderName, "000000");
                return das;
            }

            String iso8583Message = MsgBuilder.buildISO8583Das(msgContent, "");
            logger.info("+++bulding Iso583Message: " + iso8583Message);

            // Push iso8583 message
            producer.pushIso8583Message(String.valueOf(transaction.getTransId()),  senderRefId, iso8583Message, iso8583Message, AppConstant.LogConfig.REQUEST, AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.BANK,
                    AppConstant.LogConfig.CATEGORY_EXTERNAL);

            // call to bank
//            if (AppConstant.Common.IS_CARD_FLAG.equals(isCardFlag)) {
//                card = new Card();
//                card = BankCaller.inquiryCardDetail(root, errorCode, errorDesc);
//                accountHolderName = card.getAcctName();
//            } else if (AppConstant.Common.IS_ACCT_FLAG.equals(isCardFlag)) {

            //TODO RestDataObj accountDas = BankCaller.send2Bank(iso8583Message, AppConstant.MsgIdr.DAS);
            //Simulation response from bank start
            RestDataObj accountDas = new RestDataObj();
            accountDas.setHttpStatus("200");
            accountDas.setResponse("{\"body\":{\"iso8583\":{\"MTI\":\"0210\",\"DE002_PAN\":\"0000000000000\",\"DE003_PROC_CD\":\"432020\",\"DE004_TRN_AMT\":\"000000000000\",\"DE007_TRN_DT\":\"0308172359\",\"DE011_TRACE_NO\":\"452888\",\"DE012_LOC_TRN_TIME\":\"002359\",\"DE013_LOC_TRN_DATE\":\"0309\",\"DE015_STL_DATE\":\"0309\",\"DE032_ACQ_CD\":\"686868\",\"DE037_REL_REF_NO\":\"035536002359\",\"DE038_AUTH_ID_RES\":\"346810\",\"DE039_RES_CD\":\"00\",\"DE041_CRD_ACPT_TRM\":\"20191111\",\"DE048_ADD_PRV_INF\":\"VCB HO\\r198 Tran Quang Khai, Ha Noi\",\"DE049_TRN_CCY\":\"704\",\"DE062_NAP_SVC_CD\":\"IF_INQ\",\"DE063_TRN_REF_NO\":\"4037689546490790\",\"DE100_BEN_CD\":\"970457\",\"DE102_SND_ACC_INF\":\"0000000000000\",\"DE103_RCV_ACC_INF\":\"902007616497\",\"DE104_TRN_CONT\":\"Chuyen tien NH - INQ c2c\",\"DE120_BEN_INF\":\"SOHAGAME 0922065032\",\"DE128_MAC_DAT\":\"4D7A62F27BB3F772\"}}}");
            //Simulation response from bank end
                if (accountDas !=  null && !StringUtils.isEmpty(accountDas.getResponse())) {
                    String msgIso8583 = accountDas.getResponse();
                    // Push iso8583 message
                    producer.pushIso8583Message(String.valueOf(transaction.getTransId()),  senderRefId, msgIso8583, msgIso8583, AppConstant.LogConfig.RESPONSE, AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.NAPAS,
                            AppConstant.LogConfig.CATEGORY_INTERNAL);

                    JsonNode rootDas = objectMapper.readTree(msgIso8583);
                    errorCode = JsonUtil.getVal(rootDas, "/body/iso8583/DE039_RES_CD").asText();
                    authIdResponse = JsonUtil.getVal(rootDas, "/body/iso8583/DE038_AUTH_ID_RES").asText();
                    accountHolderName = JsonUtil.getVal(rootDas, "/body/iso8583/DE120_BEN_INF").asText();
                } else {
                    errorCode = AppConstant.SystemResponse.TIMEOUT_ERROR_CODE;
                }
//            }

            if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
                das = ACHUtil.buildResponseDasIn(root, errorCode, accountHolderName, authIdResponse);
            } else {
                if (!AppConstant.SystemResponse.SYSTEM_ERROR_CODE.equals(errorCode)
                        && !AppConstant.SystemResponse.TIMEOUT_ERROR_CODE.equals(errorCode)) {
                    if (AppConstant.Common.IS_CARD_FLAG.equals(isCardFlag)) {
                        errorCode = AppConstant.InquiryConfig.Das.DAS_ERROR_CODE_CARD;
                    } else if (AppConstant.Common.IS_ACCT_FLAG.equals(isCardFlag)) {
                        errorCode = AppConstant.InquiryConfig.Das.DAS_ERROR_CODE_ACCT;
                    }
                }
                das = ACHUtil.buildResponseDasIn(root, errorCode, accountHolderName, "000000");
            }

            transactionRepository.updateTransStatus(transaction.getTransId(), errorCode, errorDesc);
            transactionRepository.updateTransAchDetailStatus(transaction.getTransId(), errorCode, errorDesc);

            // push log res
            producer.pushMsgLogRes(String.valueOf(transaction.getTransId()), objectMapper.writeValueAsString(das),
                    AppConstant.ChannelId.ACH, AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_NAPAS);
        } catch (Exception e) {
            logger.error("Exception when handle inquiryDASInComing:" + e.getMessage());
            das = ACHUtil.buildResponseDasIn(root, AppConstant.AchEcode.ECODE_SYSTEM_ERROR, accountHolderName,
                    "000000");
        }
        logger.info("-------------- sending to napas:: " + das.toString());
        return das;
    }

    private DataObj handleDasOutNPResp(RestDataObj restDataObj) {
        DataObj dataObj = new DataObj();
        try {
            String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
            if (restDataObj != null && restDataObj.getHttpStatus() != null && restDataObj.getResponse() != null) {
                JsonNode jsonReps = JsonUtil.toJsonNode(restDataObj.getResponse());
                if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
                    partnerCode = JsonUtil.getVal(jsonReps, "/errorCode").asText();
                    if ("".equals(partnerCode)) {
                        partnerCode = AppConstant.AchEcode.ECODE_SUCCESS;
                    }
                } else {
                    partnerCode = JsonUtil.getVal(jsonReps, "/errorCode").asText();
                    if ("".equals(partnerCode)) {
                        partnerCode = restDataObj.getHttpStatus();
                    }
                }
            } else {
                partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
            }
            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
                    partnerCode);
            if (AppConstant.SystemResponse.SUCCESS_CODE.equals(dataObj.getEcode())) {
                String iso8583Message = MsgBuilder.buildISO8583Das(restDataObj.getResponse(), "");
                dataObj.setData(iso8583Message);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("Exception when handle handleDasOutNPResp:" + e.getMessage());
            dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
            dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
        }
        return dataObj;
    }

    private DataObj handleInqTransNrtNPResp(RestDataObj restDataObj) {
        DataObj dataObj = new DataObj();
        try {
            String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
            if (restDataObj != null && restDataObj.getHttpStatus() != null && restDataObj.getResponse() != null) {
                if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
                    NPResponse npResponse = JsonUtil.parseJson2NPResponse(restDataObj.getResponse());
                    partnerCode = restDataObj.getHttpStatus().toUpperCase() + "_" + (StringUtils.isEmpty(npResponse.getType()) ? "NULL" : npResponse.getType().toUpperCase())
                            + "_" + (StringUtils.isEmpty(npResponse.getDuplicated()) ? "NULL" : npResponse.getDuplicated().toUpperCase());
                } else if (AppConstant.HTTPConfig.HTTP_STATUS_404.equals(restDataObj.getHttpStatus())) {
                    JsonNode jsonReps = JsonUtil.toJsonNode(restDataObj.getResponse());
                    partnerCode = JsonUtil.getVal(jsonReps, "/errorCode").asText();
                } else {
                    partnerCode = restDataObj.getHttpStatus();
                }
            } else {
                partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
            }
            logger.info("+++partnerCode inquiry +++" + partnerCode);
            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
                    partnerCode);
        } catch (Exception e) {
            logger.error("Exception when handle handleInqTransNrtNPResp:" + e.getMessage());
            dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
            dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
        }
        return dataObj;
    }

}
