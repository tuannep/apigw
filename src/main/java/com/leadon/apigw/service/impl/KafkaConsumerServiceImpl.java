package com.leadon.apigw.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadon.apigw.config.AppProperties;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.kafka.CustomKafkaMessage;
import com.leadon.apigw.model.AchCustomerInfo;
import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransMessageIso8583;
import com.leadon.apigw.model.TransMessageLog;
import com.leadon.apigw.repository.TransMessageISO8583Repository;
import com.leadon.apigw.repository.TransMessageLogRepository;
import com.leadon.apigw.repository.TransactionRepository;
import com.leadon.apigw.service.AchCustomerInfoService;
import com.leadon.apigw.service.KafkaConsumerService;
import com.leadon.apigw.service.KafkaProducerService;
import com.leadon.apigw.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.text.SimpleDateFormat;
import java.util.Date;

@Service("kafkaConsumerService")
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    public static Logger logger = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);

    @Autowired
    private KafkaProducerService producer;

    @Autowired
    private TransactionRepository transactionRepository;

//    @Autowired
//    private TransAchActivityRepository transAchActivityRepository;

    @Autowired
    private TransMessageLogRepository transMessageLogRepository;

//    @Autowired
//    private ReportRepository reportRepository;
//
//    @Autowired
//    private CopyPaymentService copyPaymentService;

    @Autowired
    private TransMessageISO8583Repository transMessageISO8583Repository;

//    @Autowired
//    private ReturnPaymentService returnPaymentService;
//
//    @Autowired
//    private AchReconResultService achReconResultService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private AchCustomerInfoService achCustomerInfoService;

    @KafkaListener(topics = "ACH.NRT.IN.PACS008", groupId = "group01")
    public void consumeNrtPacs008(CustomKafkaMessage kafkaMessage) {
//        logger.info("+++++staring run queue ACH.NRT.IN.PACS008 ");
//        try {
//            logger.debug("consumeNrtPacs008 Consumed Message: "+ kafkaMessage.getTransId());
//            String transId = kafkaMessage.getTransId();
//            String message = kafkaMessage.getMessage();
//            String msgIdr = kafkaMessage.getMsgIdr();
//            String orgSenderRefId = kafkaMessage.getSenderRefId();
//            String actStep = kafkaMessage.getActStep();
//            logger.debug("Receive " + msgIdr + ", message content: " + message + ", transId: "
//                    + transId + ", senderRef: " + orgSenderRefId);
//            // luu log msg pacs008 req
////            producer.pushMsgLogReq(transId, message, AppConstant.LogConfig.NAPAS,
////                    AppConstant.LogConfig.BANK, AppConstant.LogConfig.NAPAS);
//            String iso8583Message;
//            ObjectMapper objectMapper1 = new ObjectMapper();
//            JsonNode rootPacs = objectMapper1.readTree(message);
//            String dbtrMemCode;
//
//            if (AppConstant.MsgIdr.CAMT034.equals(msgIdr)) {
//                dbtrMemCode = JsonUtil
//                        .getVal(rootPacs,
//                                "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
//                        .asText();
//                iso8583Message = MsgBuilder.buildISO8583Pacs008FromCamt034(message, appProperties.getProperty(dbtrMemCode));
//            }
//            else if(AppConstant.MsgIdr.PACS004.equals(msgIdr)) {
//                dbtrMemCode =  JsonUtil.getVal(rootPacs,
//                        "/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId").asText();
//                iso8583Message = MsgBuilder.buildISO8583Pacs004FromCam043(message, kafkaMessage.getOrgMessage(), kafkaMessage.getCreDt(), appProperties.getProperty(dbtrMemCode));
//            }
//            else {
//                dbtrMemCode = JsonUtil
//                        .getVal(rootPacs,
//                                "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
//                        .asText();
//                iso8583Message = MsgBuilder.buildISO8583NRT(message, appProperties.getProperty(dbtrMemCode));
//            }
//
//            if (StringUtils.isEmpty(iso8583Message))
//                return;
//            // luu log msg hach toan req
//            producer.pushMsgLogReq(transId, iso8583Message, AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                    AppConstant.LogConfig.CATEGORY_INTERNAL);
//            // Push iso8583 message
//            producer.pushIso8583Message(transId, orgSenderRefId, iso8583Message, message, AppConstant.LogConfig.REQUEST , AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.BANK,
//                    AppConstant.LogConfig.CATEGORY_EXTERNAL);
//            // Hach toan
//            RestDataObj restData = BankCaller.send2Bank(iso8583Message, msgIdr);
//            DataObj dataObjBank = handlePutMxBankResp(restData);
//
//            String transEdesc;
//            String ftResponse = null;
//
//            if (StringUtils.hasText(restData.getErrorMsg())) {   // check if have error from igate
//                transEdesc = restData.getErrorMsg();
//            } else {
//                ftResponse = restData.getResponse();
//                transEdesc = dataObjBank.getEdesc();
//            }
//            // Push iso8583 message
//            if (!StringUtils.isEmpty(ftResponse)) {
//                producer.pushIso8583Message(transId, orgSenderRefId, ftResponse, ftResponse, AppConstant.LogConfig.RESPONSE, AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.BANK,
//                        AppConstant.LogConfig.CATEGORY_INTERNAL);
//            }
//
//            String transEcode = dataObjBank.getEcode();
//
//            logger.debug("After call corebank, errCode: " + transEcode + ", errDesc: " + transEdesc + ", transId: "
//                    + transId + ", senderRef: " + orgSenderRefId);
//            //Push activity request payment IN to bank
//            transAchActivityRepository.pushActivity(Long.parseLong(transId), orgSenderRefId, AppConstant.MsgIdr.ISO8583,
//                    "Send and received response code of " + msgIdr + " message ISO8583 to Bank", AppConstant.LogConfig.REQUEST, iso8583Message, new Date(),
//                    StringUtils.isEmpty(actStep) ? AppConstant.TransStep.ACT_STEP_SEND_PACS008 : actStep, transEcode, transEdesc);
//
//            if (!AppConstant.SystemResponse.SUCCESS_CODE.equals(transEcode)) {
//                transactionRepository.updateTransAchDetailStatus(Long.valueOf(transId), transEcode, transEdesc);
//            }
//
//            // call update transaction
//            transactionRepository.updateTransStatus(Long.valueOf(transId), transEcode, transEdesc);
//
//            // map code NP
//            DataObj dataObj = null;
//            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_BANK, AppConstant.ChannelId.ACH_IN,
//                    transEcode);
//            String napasEcode = dataObj.getEcode();
//            String napasEdesc = dataObj.getEdesc();
//            // luu log msg hach toan res
//            producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(restData), AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                    AppConstant.LogConfig.CATEGORY_INTERNAL);
//            //handle if timeout or error , dont send pacs002 to napas
//            //Don't buil pacs002 in case is pacs004-camt034
//            if (!transEcode.equalsIgnoreCase(AppConstant.SystemResponse.TIMEOUT_ERROR_CODE) && !AppConstant.MsgIdr.PACS004.equals(msgIdr)) {
//                String[] arrParam = null;
//                if (ftResponse != null && !"".equals(ftResponse)) {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    JsonNode root = objectMapper.readTree(ftResponse);
//                    String year = new SimpleDateFormat("yyyy").format(new Date());
//                    arrParam = new String[]{year + JsonUtil.getVal(root, "/body/iso8583/DE013_LOC_TRN_DATE").asText()
//                            + JsonUtil.getVal(root, "/body/iso8583/DE012_LOC_TRN_TIME").asText()};
//                }
//                String senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.PACS002,
//                        AppConstant.PacsCommonConfig.SENDER_ID, AppConstant.SenderRefType.SENDER_REF_NORMAL, arrParam);
//                String jsonReqPacs002;
//                if (AppConstant.MsgIdr.CAMT034.equals(msgIdr)) {
//                    jsonReqPacs002 = MsgBuilder.buildPacs002ForPacs008InCamt034(message, transId, senderRefId, transEcode, transEdesc,
//                            napasEcode, napasEdesc);
//                }
//                //Don't buil pacs002 in case is pacs004-camt034
////                else if (AppConstant.MsgIdr.PACS004.equals(msgIdr)){
////                    jsonReqPacs002 = MsgBuilder.buildPacs002ForPacs004InCamt034(message, transId, senderRefId, transEcode, transEdesc,
////                            napasEcode, napasEdesc);
////                }
//                else {
//                    jsonReqPacs002 = MsgBuilder.buildPacs002(message, transId, senderRefId, transEcode, transEdesc,
//                            napasEcode, napasEdesc);
//                }
//                if (!"".equals(jsonReqPacs002)) {
//                    /// luu log callNapas va activity request
//
//                    // luu log msg call napas req
//                    producer.pushMsgLogReq(transId, jsonReqPacs002, AppConstant.LogConfig.BANK,
//                            AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);
//                    //
//                    //Push activity request of send to napas
//                    transAchActivityRepository.pushActivity(Long.parseLong(transId), senderRefId, AppConstant.MsgIdr.PACS002,
//                            "Send " + AppConstant.MsgIdr.PACS002 + " to NAPAS", AppConstant.LogConfig.REQUEST, jsonReqPacs002, new Date(),
//                            AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
//                            AppConstant.SystemResponse.SUCCESS_DESC);
//
//                    //Call napas
//                    RestDataObj restDataNP = NapasCaller.send2Napas(jsonReqPacs002, AppConstant.MsgIdr.PACS002, senderRefId, AppConstant.ACHService.DIRECT_CREDIT);
//                    DataObj dataObjNP = handlePutMxNPResp(restDataNP);
//
//                    //hard code ecode
////					dataObj2.setEcode("01");
////					dataObj2.setEdesc("Gui lenh sang Napas thanh cong");
//                    //end hard code
//
//                    logger.debug("After call napas send pacs002, errCode: " + dataObjNP.getEcode() + ", errDesc: " + dataObjNP.getEdesc() + ", transId: "
//                            + transId + ", senderRef: " + senderRefId);
//                    // luu log msg call napas res
//                    producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(restData), AppConstant.LogConfig.BANK,
//                            AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);
//                    //
//                    // call handle
//                    TransAchDetail transAchDetail = new TransAchDetail();
//                    transAchDetail.setTransId(Long.parseLong(transId));
//                    transAchDetail.setSenderRefId(senderRefId);
//                    transAchDetail.setMsgIdentifier(AppConstant.MsgIdr.PACS002);
//                    transAchDetail.setTransStep(AppConstant.TransStep.ACT_STEP_PUTMX);
//                    transAchDetail.setErrCode(dataObjNP.getEcode());
//                    transAchDetail.setErrDesc(dataObjNP.getEdesc());
//
//                    TransAchActivity transAchActivity = new TransAchActivity();
//                    transAchActivity.setActivityDesc("System receive Response NAPAS for Msg " + AppConstant.MsgIdr.PACS002);
//                    transAchActivity.setMsgContent(ACHUtil.parseObjectToString(restData));
//                    transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
//                    transAchActivity.setErrCode(dataObjNP.getEcode());
//                    transAchActivity.setErrDesc(dataObjNP.getEdesc());
//                    transAchActivity.setMsgDt(new Date());
//
//                    DataObj dataObj3 = transactionRepository.handleAchDetailActivity(transAchDetail, transAchActivity);
//                    logger.debug("After call handleAchDetailAct, errCode: " + dataObj3.getEcode() + ", errDesc: " + dataObj3.getEdesc() + ", transId: "
//                            + transId + ", senderRef: " + senderRefId);
//                }
//            }
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeNrtPacs008:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = "ACH.MSG.LOG", groupId = "group01")
    public void consumeMsgLog(CustomKafkaMessage kafkaMessage) {
        try {
            logger.debug("consumeMsgLog Consumed Message: "+ kafkaMessage.getTransId());
            TransMessageLog msg = new TransMessageLog();
            msg.setTransId(Long.parseLong(kafkaMessage.getTransId()));
            msg.setSender(kafkaMessage.getFromSys());
            msg.setReceiver(kafkaMessage.getToSys());
            msg.setCategory(kafkaMessage.getCategory());
            msg.setMsgContent(kafkaMessage.getMessage());
            msg.setMsgType(kafkaMessage.getMsgType());
            msg.setCreatedOn(new Date());
            transMessageLogRepository.save(msg);
        } catch (Exception e) {
            logger.error("Exception when handle consumeMsgLog:" + e.getMessage());
        }
    }

    @KafkaListener(topics = "ACH.IN.SEND", groupId = "group01")
    public void consumeNrtPacs002(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeNrtPacs002 Consumed Message: "+ kafkaMessage.getTransId());
//            String errorCode = kafkaMessage.getErrCode();
//            String errorDesc = kafkaMessage.getErrDesc();
//            String transId = kafkaMessage.getTransId();
//            String messagePacs008 = kafkaMessage.getMessage();
//            String orgSenderRefId = kafkaMessage.getSenderRefId();
//            String msgIdr = kafkaMessage.getMsgIdr();
//            logger.debug("Receive  message content: " + kafkaMessage + ", transId: "
//                    + transId + ", senderRef: " + orgSenderRefId);
//            // process send napas
//            DataObj dataObj = null;
//            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_BANK, AppConstant.ChannelId.ACH_IN,
//                    errorCode);
//            String napasEcode = dataObj.getEcode();
//            String napasEdesc = dataObj.getEdesc();
//
//
//            String senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.PACS002,
//                    AppConstant.PacsCommonConfig.SENDER_ID, AppConstant.SenderRefType.SENDER_REF_NORMAL, null);
//            String jsonReqPacs002;
//            if (AppConstant.MsgIdr.CAMT034.equals(msgIdr)) {
//                jsonReqPacs002 = MsgBuilder.buildPacs002ForPacs008InCamt034(messagePacs008, transId, senderRefId, errorCode, errorDesc,
//                        napasEcode, napasEdesc);
//            } else {
//                jsonReqPacs002 = MsgBuilder.buildPacs002(messagePacs008, transId, senderRefId, errorCode, errorDesc,
//                    napasEcode, napasEdesc);
//            }
//            if (!"".equals(jsonReqPacs002)) {
//                /// luu log callNapas va activity request
//
//                // luu log msg call napas req
//                producer.pushMsgLogReq(transId, jsonReqPacs002, AppConstant.LogConfig.BANK,
//                        AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);
//                //
//                //Push activity request of send to napas
//                transAchActivityRepository.pushActivity(Long.parseLong(transId), senderRefId, AppConstant.MsgIdr.PACS002,
//                        "Send pacs002 to Napas", AppConstant.LogConfig.REQUEST, jsonReqPacs002, new Date(),
//                        AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
//                        AppConstant.SystemResponse.SUCCESS_DESC);
//
//                //Call napas
//                RestDataObj restData = NapasCaller.send2Napas(jsonReqPacs002, AppConstant.MsgIdr.PACS002, senderRefId, AppConstant.ACHService.DIRECT_CREDIT);
//                //TODO: restData
//                DataObj dataObj2 = handlePutMxNPResp(restData);
//
//                //hard code ecode
////				dataObj2.setEcode("01");
////				dataObj2.setEdesc("Gui lenh sang Napas thanh cong");
//                //end hard code
//                logger.debug("After call napas send pacs002, errCode: " + dataObj2.getEcode() + ", errDesc: " + dataObj2.getEdesc() + ", transId: "
//                        + transId + ", senderRef: " + orgSenderRefId);
//
//                // luu log msg call napas res
//                producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(restData), AppConstant.LogConfig.BANK,
//                        AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);
//                //
//                // call handle
//                TransAchDetail transAchDetail = new TransAchDetail();
//                transAchDetail.setTransId(Long.parseLong(transId));
//                transAchDetail.setSenderRefId(senderRefId);
//                transAchDetail.setMsgIdentifier(AppConstant.MsgIdr.PACS002);
//                transAchDetail.setTransStep(AppConstant.TransStep.ACT_STEP_PUTMX);
//                transAchDetail.setErrCode(dataObj2.getEcode());
//                transAchDetail.setErrDesc(dataObj2.getEdesc());
//
//                TransAchActivity transAchActivity = new TransAchActivity();
//                transAchActivity.setActivityDesc("System receive Response NAPAS for Msg " + AppConstant.MsgIdr.PACS002);
//                transAchActivity.setMsgContent(ACHUtil.parseObjectToString(restData));
//                transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
//                transAchActivity.setErrCode(dataObj2.getEcode());
//                transAchActivity.setErrDesc(dataObj2.getEdesc());
//                transAchActivity.setMsgDt(new Date());
//
//                DataObj dataObj3 = transactionRepository.handleAchDetailActivity(transAchDetail, transAchActivity);
//                logger.debug("After call handleAchDetailAct, errCode: " + dataObj3.getEcode() + ", errDesc: " + dataObj3.getEdesc() + ", transId: "
//                        + transId + ", senderRef: " + orgSenderRefId);
//            }
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeNrtPacs002:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = "ACH.IN.REPORT", groupId = "group01")
    public void consumeReport(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeReport Consumed Message: "+ kafkaMessage.getTransId());
//            String msg = kafkaMessage.getMessage();
//            if (msg != null && !"".equals(msg)) {
//                JsonNode jsonRoot = JsonUtil.toJsonNode(msg);
//
//                String sessionId, msgId, msgName, pageNumber, endPage, dtRequest, totalSubElements, xmlContent;
//                String msgIdr = JsonUtil.getVal(jsonRoot, "/Header/MessageIdentifier").asText();
//
//                if (AppConstant.MsgIdr.CAMT052.equals(msgIdr)) {
//
//                    String msgSessionIdStr = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrAcctRpt/Rpt/AddtlRptInf").asText();
//                    sessionId = msgSessionIdStr.substring(11, msgSessionIdStr.indexOf("\n"));
//
//                    msgId = JsonUtil.getVal(jsonRoot, "/Payload/AppHdr/BizMsgIdr").asText();
//                    msgName = JsonUtil.getVal(jsonRoot, "/Payload/AppHdr/MsgDefIdr").asText();
//                    pageNumber = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrAcctRpt/Rpt/StmtPgntn/PgNb").asText();
//                    endPage = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrAcctRpt/Rpt/StmtPgntn/LastPgInd").asText();
//                    dtRequest = JsonUtil.getVal(jsonRoot, "/Header/Timestamp").asText();
//
//                    JsonNode items = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrAcctRpt/Rpt/Bal");
//                    totalSubElements = "0";
//                    if (items != null && items.isArray()) {
//                        totalSubElements = String.valueOf(items.size());
//                    }
//                    xmlContent = XmlUtil.buildXmlClearingReport(jsonRoot);
//
//                    logger.info("consumeReport CAMT052, sessionId: " + sessionId + ", msgId: " + msgId + ", msgName: "
//                            + msgName + ", pageNumber: " + pageNumber + ", endPage: " + endPage + ", dtRequest: "
//                            + dtRequest + ", totalSubElements: " + totalSubElements);
//
//                    DataObj dataObj = reportRepository.handleReport(sessionId, msgId, msgName, pageNumber, endPage, dtRequest, totalSubElements, msg, xmlContent);
//
//                    logger.info("consumeReport CAMT052 handleReport, ecode: " + dataObj.getEcode() + ", edesc: " + dataObj.getEdesc());
//
//                } else if (AppConstant.MsgIdr.CAMT053.equals(msgIdr)) {
//
//                    sessionId = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrStmt/Stmt/Ntry/0/AcctSvcrRef").asText();
//                    msgId = JsonUtil.getVal(jsonRoot, "/Payload/AppHdr/BizMsgIdr").asText();
//                    msgName = JsonUtil.getVal(jsonRoot, "/Payload/AppHdr/MsgDefIdr").asText();
//                    pageNumber = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrStmt/Stmt/StmtPgntn/PgNb").asText();
//                    endPage = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrStmt/Stmt/StmtPgntn/LastPgInd").asText();
//                    dtRequest = JsonUtil.getVal(jsonRoot, "/Header/Timestamp").asText();
//
//                    JsonNode items = JsonUtil.getVal(jsonRoot, "/Payload/Document/BkToCstmrStmt/Stmt/Ntry");
//                    totalSubElements = "0";
//                    if (items != null && items.isArray()) {
//                        totalSubElements = String.valueOf(items.size());
//                    }
//
//                    xmlContent = XmlUtil.buildXmlStatementReport(jsonRoot);
//
//                    logger.info("consumeReport CAMT053, sessionId: " + sessionId + ", msgId: " + msgId + ", msgName: "
//                            + msgName + ", pageNumber: " + pageNumber + ", endPage: " + endPage + ", dtRequest: "
//                            + dtRequest + ", totalSubElements: " + totalSubElements);
//
//                    DataObj dataObj = reportRepository.handleReport(sessionId, msgId, msgName, pageNumber, endPage, dtRequest, totalSubElements, msg, xmlContent);
//
//                    logger.info("consumeReport CAMT053 handleReport, ecode: " + dataObj.getEcode() + ", edesc: " + dataObj.getEdesc());
//
//                } else if (AppConstant.MsgIdr.CAMT998.equals(msgIdr)) {
//
//                    sessionId = JsonUtil.getVal(jsonRoot, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/RcnclRep/0/ElctrncSeqNb").asText();
//                    msgId = JsonUtil.getVal(jsonRoot, "/Payload/AppHdr/BizMsgIdr").asText();
//                    msgName = JsonUtil.getVal(jsonRoot, "/Payload/AppHdr/MsgDefIdr").asText();
//                    pageNumber = JsonUtil.getVal(jsonRoot, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/RcnclRep/0/StmtPgntn/PgNb").asText();
//                    endPage = JsonUtil.getVal(jsonRoot, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/RcnclRep/0/StmtPgntn/LastPgInd").asText();
//                    dtRequest = JsonUtil.getVal(jsonRoot, "/Header/Timestamp").asText();
//
//                    JsonNode items = JsonUtil.getVal(jsonRoot, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/RcnclRep/0/Ntry");
//                    totalSubElements = "0";
//                    if (items != null && items.isArray()) {
//                        totalSubElements = String.valueOf(items.size());
//                    }
//
//                    xmlContent = XmlUtil.buildXmlReconReport(jsonRoot);
//
//                    logger.info("consumeReport CAMT998, sessionId: " + sessionId + ", msgId: " + msgId + ", msgName: "
//                            + msgName + ", pageNumber: " + pageNumber + ", endPage: " + endPage + ", dtRequest: "
//                            + dtRequest + ", totalSubElements: " + totalSubElements);
//
//                    DataObj dataObj = reportRepository.handleReport(sessionId, msgId, msgName, pageNumber, endPage, dtRequest, totalSubElements, msg, xmlContent);
//
//                    logger.info("consumeReport CAMT998 handleReport, ecode: " + dataObj.getEcode() + ", edesc: " + dataObj.getEdesc());
//
//                } else {
//                    logger.warn("consumeReport MessageIdentifier is invalid. " + msgIdr);
//                }
//
//            } else {
//                logger.warn("consumeReport msg is null or empty.");
//            }
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeReport:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = "ACH.NACK.IN.ISO8583", groupId = "group01")
    public void consumeNackIso8583(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeNackIso8583 Consumed Message: "+ kafkaMessage.getOrgSenderRefId());
//            String message = kafkaMessage.getMessage();
//            String senderRefId = kafkaMessage.getSenderRefId();
//            String err_code = kafkaMessage.getErrCode();
//            String err_desc = kafkaMessage.getErrDesc();
//            String actStep = kafkaMessage.getActStep();
//            String actDes = kafkaMessage.getActDesc();
//            String msgIdr = kafkaMessage.getMsgIdr();
//            String authIdRes = kafkaMessage.getAuthIdRes();
//            String transId = kafkaMessage.getTransId();
//            String orgMessage = kafkaMessage.getOrgMessage();
//            String countCheck = kafkaMessage.getCheckInvest();
//            String errException = kafkaMessage.getErrException();
//
//            logger.debug("++++ Starting build msg iso8583 in queue ACH.NACK.IN.ISO8583" + ", transId: "
//                    + transId + ", senderRef: " + senderRefId + ", error_code:" + err_code + ", err_desc" + err_desc);
//            String iso8583Message;
//            // 17/12/2020 comment send to bank from TuanLa - open 25/12/2020
//            if (AppConstant.MsgIdr.PACS004.equals(msgIdr)) {
//                ObjectMapper objectMapper1 = new ObjectMapper();
//                JsonNode rootPacs = objectMapper1.readTree(message);
//                String dbtrMemCode = JsonUtil
//                        .getVal(rootPacs,
//                                "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId")
//                        .asText();
//
//                iso8583Message = MsgBuilder.buildISO8583PACS004(message, orgMessage, appProperties.getProperty(dbtrMemCode));
//            }
//            else if (countCheck != null && 1 <= Integer.parseInt(countCheck))
//                iso8583Message = MsgBuilder.buildISO8583Investigation(message, orgMessage, err_code, authIdRes, errException);
//            else
//                iso8583Message = MsgBuilder.buildISO8583NRTNAK(message, err_code, authIdRes);
//            // luu log msg hach toan req
//            producer.pushMsgLogReq(String.valueOf(transId), iso8583Message, AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                    AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//            // Push iso8583 message
//            producer.pushIso8583Message(transId, senderRefId, iso8583Message, iso8583Message, AppConstant.LogConfig.RESPONSE , AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.BANK,
//                    AppConstant.LogConfig.CATEGORY_EXTERNAL);
//
//            //Call igate bank
//            RestDataObj restData = BankCaller.send2Bank(iso8583Message, msgIdr);
//            DataObj dataObj2 = handlePutMxBankResp(restData);
//            String transEcode = dataObj2.getEcode();
//            String transEdesc = dataObj2.getEdesc();
//
//            // Push iso8583 message
//            if (!StringUtils.isEmpty(restData.getResponse())) {
//                producer.pushIso8583Message(transId, kafkaMessage.getOrgSenderRefId(), restData.getResponse(), restData.getResponse(), AppConstant.LogConfig.RESPONSE, AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.BANK,
//                        AppConstant.LogConfig.CATEGORY_INTERNAL);
//            }
//
//            logger.debug("After call igate bank send iso8583, errCode: " + transEcode + ", errDesc: " + transEdesc + ", transId: "
//                    + transId + ", senderRef: " + senderRefId);
//
//            // only for return in payment ,update status transaction , push activity ,log via pacs004 transId
//            if (AppConstant.MsgIdr.PACS004.equals(msgIdr)) {
//                String pacs004TransId = kafkaMessage.getNewTransId();
//                transactionRepository.updateTransactionStatusJpa(Long.valueOf(pacs004TransId), transEcode, transEdesc);
//
//                producer.pushMsgLogReq(pacs004TransId, iso8583Message, AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                        AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//                producer.pushMsgLogRes(pacs004TransId, ACHUtil.parseObjectToString(restData), AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                        AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//
//                // push activity send to core
//                transAchActivityRepository.pushActivity(Long.parseLong(pacs004TransId), senderRefId,
//                        AppConstant.MsgIdr.ISO8583,
//                        "Send Pacs004 message ISO8583 to Bank| Original Pacs008 transId : " + transId,
//                        AppConstant.LogConfig.REQUEST,
//                        iso8583Message,
//                        new Date(),
//                        AppConstant.TransStep.ACT_STEP_SEND_PACS004, transEcode, transEdesc); // original transId of pacs008
//
//
//            }
//            // Push activity request of bank iso8583 ach
//            transAchActivityRepository.pushActivity(Long.parseLong(transId), senderRefId, AppConstant.MsgIdr.ISO8583,
//                    actDes, AppConstant.LogConfig.RESPONSE, iso8583Message, new Date(),
//                    actStep, transEcode, transEdesc); // update errCode from pacs002
//
//
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeNackIso8583:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = "ACH.INQUIRY.IN", groupId = "group01")
    public void consumeInquiryNotExist(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeInquiryNotExist Consumed Message: "+ kafkaMessage.getOrgSenderRefId());
//            String orgMsgDefine = kafkaMessage.getMsgIdr();
//            String orgTxId = kafkaMessage.getOrgTxId();
//            String orgSettleDate = kafkaMessage.getOrgSettleDate();
//            String orgSenderCode = kafkaMessage.getOrgSenderCode();
//            String orgSenderRefId = kafkaMessage.getOrgSenderRefId();
//            String senderRefId = kafkaMessage.getSenderRefId();
//
//            CopyPaymentDto copyPaymentDto = new CopyPaymentDto();
//            copyPaymentDto.setOrgSenderRefId(orgSenderRefId);
//            copyPaymentDto.setOrgMsgDefine(orgMsgDefine);
//            copyPaymentDto.setOrgTxId(orgTxId);
//            copyPaymentDto.setOrgSettleDate(orgSettleDate);
//            copyPaymentDto.setOrgSenderCode(orgSenderCode);
//            copyPaymentDto.setChannelId(AppConstant.ChannelId.ACH_OUT);
//            copyPaymentDto.setTraceNumber(orgSenderRefId);
//            logger.info("++++++creatte  copyPayment from queue ACH.INQUIRY.IN:" + copyPaymentDto.getOrgSenderRefId() + ", msg:" + copyPaymentDto.getOrgMsgDefine());
//            DataObj dataObj1 = copyPaymentService.copyPayment(copyPaymentDto);
//            logger.info("++++++response copyPayment from queue ACH.INQUIRY.IN:" + dataObj1.getEcode() + ", des:" + dataObj1.getEdesc());
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeInquiryNotExist:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = AppConstant.QueueConfig.TOPIC_COPY_MISSTRANS_IN, groupId = "group01")
    public void consumeCopyMissTransIn(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeCopyMissTransIn Consumed Message: "+ kafkaMessage.getOrgSenderRefId());
//            String orgMsgDefine = kafkaMessage.getMsgIdr();
//            String orgTxId = kafkaMessage.getOrgTxId();
//            String orgSettleDate = kafkaMessage.getOrgSettleDate();
//            String orgSenderCode = kafkaMessage.getOrgSenderCode();
//            String orgSenderRefId = kafkaMessage.getOrgSenderRefId();
//            String senderRefId = kafkaMessage.getSenderRefId();
//
//            CopyPaymentDto copyPaymentDto = new CopyPaymentDto();
//            copyPaymentDto.setOrgSenderRefId(orgSenderRefId);
//            copyPaymentDto.setOrgMsgDefine(orgMsgDefine);
//            copyPaymentDto.setOrgTxId(orgTxId);
//            copyPaymentDto.setOrgSettleDate(orgSettleDate);
//            copyPaymentDto.setOrgSenderCode(orgSenderCode);
//            copyPaymentDto.setChannelId(AppConstant.ChannelId.ACH_OUT);
//            copyPaymentDto.setTraceNumber(orgSenderRefId);
//            logger.info("++++++creatte copyPayment from queue TOPIC.COPY.MISSTRANS.IN:" + copyPaymentDto.getOrgSenderRefId() + ", msg:" + copyPaymentDto.getOrgMsgDefine());
//            DataObj dataObj1 = achReconResultService.copyTrans(copyPaymentDto);
//            logger.info("++++++response copyPayment from queue TOPIC.COPY.MISSTRANS.IN:" + dataObj1.getEcode() + ", des:" + dataObj1.getEdesc());
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeCopyMissTransIn:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = AppConstant.QueueConfig.TOPIC_INVEST_NRT_TO_CORE, groupId = "group01")
    public void consumeInvestNrtToCore(CustomKafkaMessage kafkaMessage) throws JsonProcessingException {
//        Long transId = Long.valueOf(kafkaMessage.getTransId());
//
//        // Push iso8583 message
//        producer.pushMsgLogReq(String.valueOf(transId) , kafkaMessage.getMessage() , AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//        RestDataObj restData = BankCaller.send2Bank(kafkaMessage.getMessage(), AppConstant.MsgIdr.ISO8583);
//        DataObj dataObjBank = handlePutMxBankResp(restData);
//
//        producer.pushMsgLogRes(String.valueOf(transId) , ACHUtil.parseObjectToString(restData) , AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                AppConstant.LogConfig.CATEGORY_INTERNAL);
//        int isInvestSuccess = 0;
//        String newTransStat = "";
//        String transEcode = dataObjBank.getEcode();
//        String transDesc;
//
//        if (StringUtils.hasText(restData.getErrorMsg())) {   // if send to Igate error
//
//            logger.error("Send invest message to Igate error with transId : {} and error : {}" , kafkaMessage.getTransId() , restData.getErrorMsg());
//            transDesc = restData.getErrorMsg();
//
//        } else {     // handle code at de039
//
//            if (transEcode.equals(AppConstant.AchEcode.ECODE_SUCCESS)) {
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode jsonNodeIso8583 = objectMapper.readTree(restData.getResponse());
//                String de48 = JsonUtil.getVal(jsonNodeIso8583, "/body/iso8583/DE048_ADD_PRV_INF").asText();
//                newTransStat = de48.substring(12);
//                if (AppConstant.AchEcode.ECODE_SUCCESS.equals(newTransStat)) {
//                    transDesc="Get response successfully from bank";
//                } else {
//                    DataObj dataObj = transactionRepository.mapErrorCode("BANK" , "ACH_IN" , newTransStat);
//                    transDesc = dataObj.getEdesc();
//                }
//
//                isInvestSuccess = 1;
//            } else {
//                logger.error("Core bank response invest error de39 : {} with transId {})" , transEcode , transId);
//                transDesc = "Core bank response invest error de39 " + transEcode;
//            }
//
//        }
//
//        if (!StringUtils.hasText(newTransStat)) {
//            newTransStat = "68";
//        }
//        transactionRepository.updateTransactionAfterInvestToCore(transId, isInvestSuccess , newTransStat , transDesc);

    }

    @KafkaListener(topics = AppConstant.QueueConfig.TOPIC_INVEST_IN_RECON, groupId = "group01")
    public void consumeInvestInReconToCore(CustomKafkaMessage kafkaMessage) throws JsonProcessingException {
//        Long transId = Long.valueOf(kafkaMessage.getTransId());
//
//        // Push iso8583 message
//        producer.pushMsgLogReq(String.valueOf(transId) , kafkaMessage.getMessage() , AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//        RestDataObj restData = BankCaller.send2Bank(kafkaMessage.getMessage(), AppConstant.MsgIdr.ISO8583);
//        DataObj dataObjBank = handlePutMxBankResp(restData);
//
//        producer.pushMsgLogRes(String.valueOf(transId) , ACHUtil.parseObjectToString(restData) , AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                AppConstant.LogConfig.CATEGORY_INTERNAL);
//        int isInvestSuccess = 0;
//        String newTransStat = "";
//        String transEcode = dataObjBank.getEcode();
//        String transDesc;
//
//
//        if (StringUtils.hasText(restData.getErrorMsg())) {   // if send to Igate error
//
//            logger.error("Send invest message to Igate error with transId : {} and error : {}" , kafkaMessage.getTransId() , restData.getErrorMsg());
//            transDesc = restData.getErrorMsg();
//
//        } else {     // handle code at de039
//
//            if (transEcode.equals(AppConstant.AchEcode.ECODE_SUCCESS)) {
//
//                ObjectMapper objectMapper = new ObjectMapper();
//                JsonNode jsonNodeIso8583 = objectMapper.readTree(restData.getResponse());
//                String de48 = JsonUtil.getVal(jsonNodeIso8583, "/body/iso8583/DE048_ADD_PRV_INF").asText();
//                newTransStat = de48.substring(12);
//                if (AppConstant.AchEcode.ECODE_SUCCESS.equals(newTransStat)) {
//                    transDesc="Get response successfully from bank";
//                } else {
//                    DataObj dataObj = transactionRepository.mapErrorCode("BANK" , "ACH_IN" , newTransStat);
//                    transDesc = dataObj.getEdesc();
//                }
//                achReconResultService.updateTransBtachById(kafkaMessage.getAchReconResultId() , newTransStat);
//                isInvestSuccess = 1;
//            } else {
//                logger.error("Core bank response invest error de39 : {} with transId {})" , transEcode , transId);
//                transDesc = "Core bank response invest error de39 " + transEcode;
//            }
//
//        }
//
//        if (!StringUtils.hasText(newTransStat)) {
//            newTransStat = "68";
//        }
//        transactionRepository.updateTransactionAfterInvestToCore(transId, isInvestSuccess , newTransStat , transDesc);

    }

    @KafkaListener(topics = "ACH.RETURN.ISO8583.OUT", groupId = "group01")
    public void consumeReturnIso8583(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeReturnIso8583 Consumed Message: " + kafkaMessage.getOrgSenderRefId() + ", orgTransId:" + kafkaMessage.getTransId());
//
//            String message = MsgBuilder.buildISO8583NRTNAK(kafkaMessage.getMessage(), kafkaMessage.getErrCode(), ACHUtil.subStringbyIndex(kafkaMessage.getOrgSenderRefId(), 6));
//            //Call igate bank
//            RestDataObj restData = BankCaller.send2Bank(message, AppConstant.MsgIdr.ISO8583);
//            DataObj dataObj2 = handlePutMxBankResp(restData);
//
//            logger.debug("After call igate bank send iso8583, errCode: " + dataObj2.getEcode() + ", errDesc: " + dataObj2.getEdesc());
//            String transEcode = dataObj2.getEcode();
//            String transEdesc = dataObj2.getEdesc();
//
//            // Push iso8583 message
//            producer.pushIso8583Message(kafkaMessage.getTransId(), kafkaMessage.getOrgSenderRefId(), message, message, AppConstant.LogConfig.RESPONSE , AppConstant.LogConfig.BANK, AppConstant.LogConfig.COREBANKING,
//                    AppConstant.LogConfig.CATEGORY_INTERNAL);
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeReturnIso8583:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = "ACH.RETURN.PAYMENT.OUT", groupId = "group01")
    public void consumeReturnPayment(CustomKafkaMessage kafkaMessage) {
//        try {
//            logger.debug("consumeReturnPayment Consumed Message: "+ kafkaMessage.getOrgSenderRefId());
//
//            ReturnPaymentDto returnPaymentDto = new ReturnPaymentDto();
//
//            returnPaymentDto.setOrgSenderRefId(kafkaMessage.getOrgSenderRefId());
//            returnPaymentDto.setOrgTransId(kafkaMessage.getTransId());
//            returnPaymentDto.setTransDt(kafkaMessage.getTransDt());
//            returnPaymentDto.setAmount(kafkaMessage.getAmount());
//            returnPaymentDto.setCurrency(kafkaMessage.getCcy());
//            returnPaymentDto.setDescription("Hoan Tien Chu Dong NRT");
//            returnPaymentDto.setTraceNumber(kafkaMessage.getTraceNumber());
//            returnPaymentDto.setChannelId(AppConstant.ChannelId.ACH_OUT);
//            returnPaymentDto.setErrCode(kafkaMessage.getErrCode());
//            returnPaymentDto.setMessage(kafkaMessage.getMessage());
//            returnPaymentDto.setMsgIdr(AppConstant.MsgIdr.ISO8583);
//            returnPaymentDto.setCaseId(kafkaMessage.getCaseId());
//            logger.info("++++++creatte  returnPayment from queue ACH.RETURN.PAYMENT.OUT:" + returnPaymentDto.getOrgSenderRefId() + ", transId:" + returnPaymentDto.getOrgTransId()
//                        + ", transDt:" + returnPaymentDto.getTransDt());
//            DataObj dataObj1 = returnPaymentService.returnPayment(returnPaymentDto);
//            logger.info("++++++response returnPayment from queue ACH.RETURN.PAYMENT.OUT:" + dataObj1.getEcode() + ", des:" + dataObj1.getEdesc());
//        } catch (Exception e) {
//            logger.error("Exception when handle consumeReturnPayment:" + e.getMessage());
//        }
    }

    @KafkaListener(topics = "ACH.INQUIRY.PAYMENT.OUT", groupId = "group01")
    public void consumeInquiryPaymentOut(CustomKafkaMessage kafkaMessage) {
//        logger.info("======Stating in consumeInquiryPaymentOut=========");
//        String errCode = "", errDesc= "", transId = "", orgSenderRefId = "", errException;
//        String orgTransId = "";
//        try {
//            logger.debug("consumeInquiryPaymentOut Consumed Message: " + kafkaMessage.getOrgXrefId() + ", orgSenderRefId:" + kafkaMessage.getOrgSenderRefId());
//            String message = kafkaMessage.getMessage();
//            String orgMessageIso8583 = kafkaMessage.getOrgMessage();
//            String orgXrefId = kafkaMessage.getOrgXrefId();
//            String functionCode = kafkaMessage.getFunctionCode();
//            errCode = kafkaMessage.getErrCode();
//            transId = kafkaMessage.getTransId();
//            orgSenderRefId = kafkaMessage.getOrgSenderRefId();
//            String channelId = kafkaMessage.getChanelId();
//            errException = kafkaMessage.getErrException();
//
//            DataObj dataObj = new DataObj();
//            JsonNode root = JsonUtil.toJsonNode(message);
//            String de090 = JsonUtil.getVal(root, "/body/iso8583/DE090_ORG_TRN_KEY").asText();
//            dataObj = transMessageISO8583Repository.getPacs008FromIso8583("0210", de090.substring(4, 10), de090.substring(10, 20) , de090.substring(25, 31));
//            String de039 = dataObj.getDataVal("de039");
//            orgTransId = dataObj.getDataVal("orgTransId");
//            String transStatus = dataObj.getEcode();
//
//            logger.info("+++getPacs008FromIso8583 orgSenderRef:" + dataObj.getDataVal("orgSenderRefId") + ", orgtransId" + orgTransId
//                        + ", DE039:" + de039 +  ", transStatus: " + transStatus);
//
//            if ("12".equals(errCode) || AppConstant.SystemResponse.SYSTEM_ERROR_EXCEPTION_CODE.equals(errException)
//                || "EX".equalsIgnoreCase(transStatus)) {
//                errCode = AppConstant.SystemResponse.EXCEPRION_ERROR_CODE;
//
//                DataObj dataObj2 = handleSend2Bank(message, null, errCode, "000000", errException);
//                errCode = dataObj2.getEcode();
//                errDesc = dataObj2.getEdesc();
//                logger.debug("After call igate: " + AppConstant.InquiryConfig.Invest.CHECK_BANK
//                        + ",errCode: " + errCode + ", errDesc: " + errDesc + ", transId: " + transId);
//            } else if (!StringUtils.isEmpty(de039) && !StringUtils.isEmpty(transStatus) && !transStatus.equalsIgnoreCase(AppConstant.SystemResponse.TIMEOUT_ERROR_CODE)) {
//                DataObj dataObj2;
//                if (!StringUtils.isEmpty(transStatus))
//                    dataObj2 = handleSend2Bank(message, orgSenderRefId, transStatus, orgTransId, errException);
//                else
//                    dataObj2 = handleSend2Bank(message, orgSenderRefId, de039, orgTransId, errException);
//                errCode = dataObj2.getEcode();
//                errDesc = dataObj2.getEdesc();
//                logger.debug("After call igate: " + AppConstant.InquiryConfig.Invest.CHECK_BANK
//                        + ",errCode: " + errCode + ", errDesc: " + errDesc + ", transId: " + transId);
//            } else {
//                dataObj = transactionRepository.checkInvestgtnInq(orgXrefId, AppConstant.InquiryConfig.Invest.CHECK_BANK,
//                        AppConstant.InquiryConfig.Invest.NUM_INQ_BANK, AppConstant.InquiryConfig.Invest.INQ_TIME_BANK,
//                        AppConstant.InquiryConfig.Invest.NUM_INQ_NAPAS, AppConstant.InquiryConfig.Invest.NUM_DAY_INQ,
//                        AppConstant.InquiryConfig.Invest.INQ_TIME_2_NAPAS,
//                        AppConstant.InquiryConfig.Invest.INQ_TIME_3_NAPAS);
//                orgSenderRefId = dataObj.getDataVal("orgSenderRefId");
//                errCode = dataObj.getEcode();
//                errDesc = dataObj.getEdesc();
//
//                logger.debug("After call checkInvestgtnInq , check 1: " + AppConstant.InquiryConfig.Invest.CHECK_BANK
//                        + ",errCode: " + errCode + ", errDesc: " + errDesc + ", transId: " + transId
//                        + ", orgsenderRef: " + orgSenderRefId);
//
//                orgTransId = dataObj.getDataVal("orgTransId");
//                dataObj = new DataObj();
//                dataObj = transactionRepository.getTransById(Long.parseLong(orgTransId));
//                errCode = dataObj.getEcode();
//                errDesc = dataObj.getEdesc();
//
//                String isFinalState = dataObj.getDataVal("isFinalState");
//                String orgTransDate = dataObj.getDataVal("createdOn");
//
//                logger.debug("After call getTransById, errCode: " + errCode + ", errDesc: " + errDesc + ", orgTransId: "
//                        + orgTransId + ", orgSenderRef: " + orgSenderRefId + ", isFinalState:" + isFinalState);
//
//                if (!isFinalState.equals("1") && !AppConstant.FunctionCode.GET_INQ_INVES.equals(functionCode)
//                        && AppConstant.FunctionCode.SEND_INQ_INVES.equals(functionCode)) {
//                    dataObj = new DataObj();
//                    dataObj = transactionRepository.checkInvestgtnInq(orgXrefId,
//                            AppConstant.InquiryConfig.Invest.CHECK_NAPAS, AppConstant.InquiryConfig.Invest.NUM_INQ_BANK,
//                            AppConstant.InquiryConfig.Invest.INQ_TIME_BANK,
//                            AppConstant.InquiryConfig.Invest.NUM_INQ_NAPAS,
//                            AppConstant.InquiryConfig.Invest.NUM_DAY_INQ,
//                            AppConstant.InquiryConfig.Invest.INQ_TIME_2_NAPAS,
//                            AppConstant.InquiryConfig.Invest.INQ_TIME_3_NAPAS);
//
//                    String isCallNapas = dataObj.getDataVal("isCallNapas");
//                    errCode = dataObj.getEcode();
//                    errDesc = dataObj.getEdesc();
//                    logger.debug("After call checkInvestgtnInq, check 2: " + AppConstant.FunctionCode.SEND_INQ_INVES
//                            + ", errCode: " + errCode + ", errDesc: " + errDesc + ", orgTransId: "
//                            + orgTransId + ", senderRef: " + orgSenderRefId + ", isCallNapas:" + isCallNapas);
//                    if (errCode.equals(AppConstant.AchEcode.ECODE_SUCCESS) && !StringUtils.isEmpty(isCallNapas) && !isCallNapas.equals("0")) {
//                        /*
//                         **response to bank
//                         */
//                        logger.info("Case create pacs028 ");
//                        /*
//                         **Create pacs028 request to NP
//                         */
//                        logger.info("Create pacs028 ");
//                        String senderRefId = ACHUtil.generateSenderRefId(orgTransId, AppConstant.MsgIdr.PACS028,
//                                AppConstant.PacsCommonConfig.SENDER_ID, AppConstant.SenderRefType.SENDER_REF_NORMAL, null);
//                        // call to napas
//                        // set lai gia tri sender code
//                        String requestBody = MsgBuilder.buildPacs028(senderRefId, orgTransId,
//                                orgSenderRefId, orgTransDate);
//
//                        // Push activity request of send pacs028 to napas
//                        transAchActivityRepository.pushActivity(Long.parseLong(orgTransId), senderRefId, AppConstant.MsgIdr.PACS028,
//                                "Send pacs028 to Napas", AppConstant.LogConfig.REQUEST, requestBody, new Date(),
//                                AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
//                                AppConstant.SystemResponse.SUCCESS_DESC);
//
//                        RestDataObj restDataNp = NapasCaller.send2Napas(requestBody, AppConstant.MsgIdr.PACS028, senderRefId,
//                                AppConstant.ACHService.INVESTIGATION_TRANS);
//                        DataObj objRes = handleInqTransNrtNPResp(restDataNp);
//                        errCode = objRes.getEcode();
//                        errDesc = objRes.getEdesc();
//
//                        logger.debug("After call napas, errCode: " + objRes.getEcode() + ", errDesc: " + objRes.getEdesc()
//                                + ", transId: " + orgTransId + ", senderRef: " + orgSenderRefId);
//
//                        // Push activity response
//                        transAchActivityRepository.pushActivity(Long.parseLong(orgTransId), senderRefId, AppConstant.MsgIdr.PACS028,
//                                "Response send pacs028 to Napas", AppConstant.LogConfig.RESPONSE, ACHUtil.parseObjectToString(restDataNp),
//                                new Date(), AppConstant.TransStep.ACT_STEP_PUTMX, errCode, errDesc);
//
//                        // update transaciton va trans_detail
//                       /* TransAchDetail transAchDetail;
//                        if (!DataUtil.isNullOrEmpty(transId)) {
//                            transAchDetail = ACHUtil.hadleUpdateTranStatusAndTransDetail(Long.parseLong(transId), orgTransId, errCode, errDesc);
//                            transactionRepository.updateTransAchDetail(transAchDetail);
//                            transactionRepository.updateTransStatusUpdated(Long.parseLong(transId), errCode, errDesc);
//                        }
//                        if (!DataUtil.isNullOrEmpty(orgTransId)) {
//                            transAchDetail = ACHUtil.hadleUpdateTranStatusAndTransDetail(Long.parseLong(orgTransId), null, errCode, errDesc);
//                            transactionRepository.updateTransAchDetail(transAchDetail);
//                            transactionRepository.updateTransStatusUpdated(Long.parseLong(orgTransId), errCode, errDesc);
//                        } */
//
//                    }
//                } else if(isFinalState.equals("1")) {
//                    String de039Final = transAchActivityRepository.selectErrCode(Long.parseLong(orgTransId), AppConstant.MsgIdr.PACS002);
//                    dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
//                            de039Final);
//                    logger.info("+++isFinalState: " + isFinalState + ",  errCode mapping: " + dataObj.getEcode());
//                    DataObj dataObj2 = handleSend2Bank(message, orgSenderRefId, dataObj.getEcode(), orgTransId, errException);
//                    errCode = dataObj2.getEcode();
//                    errDesc = dataObj2.getEdesc();
//                    logger.debug("After call igate: " + AppConstant.InquiryConfig.Invest.CHECK_BANK
//                            + ",errCode: " + errCode + ", errDesc: " + errDesc + ", transId: " + transId);
//                }
//           }
//        } catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Exception when handle consumeInquiryPaymentOut:" + e.getMessage());
//            errCode = AppConstant.SystemResponse.SYSTEM_ERROR_EXCEPTION_CODE;
//            errDesc = AppConstant.SystemResponse.SYSTEM_ERROR_DESC;
//            errException = AppConstant.SystemResponse.SYSTEM_ERROR_EXCEPTION_CODE;
//
//            DataObj dataObj2 = handleSend2Bank(kafkaMessage.getMessage(), null, errCode, "000000", errException);
//            errCode = dataObj2.getEcode();
//            errDesc = dataObj2.getEdesc();
//            logger.debug("After call igate: " + AppConstant.InquiryConfig.Invest.CHECK_BANK
//                    + ",errCode: " + errCode + ", errDesc: " + errDesc + ", transId: " + transId);
//        }
    }

//    private DataObj handleSend2Bank(String message, String orgSenderRefId, String errCode, String orgTransId, String errException) {
//        logger.info("+++handleSend2Bank, orgSenderRefId:" + orgSenderRefId + ", errCode, " + errCode + ", orgTransId" + orgTransId + ", errException" + errException);
//        String returnMesaageIso8583;
//        DataObj selectMsgIso8583 = new DataObj();
//        String messageIso8583;
//
//        //Get original NRT message
//        if (!StringUtils.isEmpty(orgSenderRefId)) {
//            //Check invest for return
//            JsonNode rootReturnInvest = JsonUtil.toJsonNode(message);
//            String defineInvestOfReturnByDe104 = JsonUtil.getVal(rootReturnInvest, "/body/iso8583/DE104_TRN_CONT").asText();
//            if (defineInvestOfReturnByDe104.startsWith(AppConstant.DisputeConfig.DisputeType.DISP_RTN_TYPE)) {
//                selectMsgIso8583 = transMessageISO8583Repository.selectMesageReturn8583ByOrgSenderRefId(orgSenderRefId);
//                messageIso8583 = selectMsgIso8583.getDataVal("msContent");
//            }
//            else {
//                selectMsgIso8583 = transAchActivityRepository.selectMsgIso8583(orgSenderRefId, AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
//                messageIso8583 = selectMsgIso8583.getDataVal("msgContent");
//            }
//
//            returnMesaageIso8583 = MsgBuilder.buildISO8583Investigation(message, messageIso8583, errCode, orgTransId, errException);
//        } else {
//            returnMesaageIso8583 = MsgBuilder.buildISO8583Investigation(message, message, errCode, orgTransId, errException);
//        }
//        //Call igate bank
//        RestDataObj restData = BankCaller.send2Bank(returnMesaageIso8583, AppConstant.MsgIdr.ISO8583);
//
//        // Push iso8583 message
//        producer.pushIso8583Message(StringUtils.isEmpty(orgTransId) ? "0": orgTransId, orgSenderRefId, returnMesaageIso8583, returnMesaageIso8583, AppConstant.LogConfig.REQUEST , AppConstant.LogConfig.COREBANKING, AppConstant.LogConfig.BANK,
//                AppConstant.LogConfig.CATEGORY_INTERNAL);
//
//        return handlePutMxBankResp(restData);
//    }

    @KafkaListener(topics = "ACH.IN.NRT.RETURN", groupId = "group01")
    public void consumeMsgIso8583(CustomKafkaMessage kafkaMessage) {
        try {
            logger.debug("consumeMsgIso8583 Consumed Message: "+ kafkaMessage.getTransId());
            TransMessageIso8583 msg = new TransMessageIso8583();

            String orgMsgIso8583 = kafkaMessage.getMessage();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootIso8583 = objectMapper.readTree(orgMsgIso8583);

            msg.setTransId(Long.parseLong(kafkaMessage.getTransId()));
            msg.setSender(kafkaMessage.getFromSys());
            msg.setReceiver(kafkaMessage.getToSys());
            msg.setCategory(kafkaMessage.getCategory());
            msg.setMsgContent(kafkaMessage.getMsgPacs008());
            msg.setMsgType(kafkaMessage.getMsgType());
            msg.setCreatedOn(new Date());
            msg.setGlobalId(JsonUtil.getVal(rootIso8583, "/header/GLB_ID").asText());
            msg.setOrgSenderRefId(kafkaMessage.getOrgSenderRefId());
            msg.setMti(JsonUtil.getVal(rootIso8583, "/body/iso8583/MTI").asText());
            msg.setDe002Pan(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE002_PAN").asText());
            msg.setDe003ProcCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE003_PROC_CD").asText());
            msg.setDe004TrnAmt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE004_TRN_AMT").asText());
            msg.setDe005StlAmt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE005_STL_AMT").asText());
            msg.setDe006BilAmt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE006_BIL_AMT").asText());
            msg.setDe007TrnDt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE007_TRN_DT").asText());
            msg.setDe009StlConvRt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE009_STL_CONV_RT").asText());
            msg.setDe010BilConvRt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE010_BIL_CONV_RT").asText());
            msg.setDe011TraceNo(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE011_TRACE_NO").asText());
            msg.setDe012LocTrnTime(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE012_LOC_TRN_TIME").asText());
            msg.setDe013locTrnDate(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE013_LOC_TRN_DATE").asText());
            msg.setDe015StlDate(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE015_STL_DATE").asText());
            msg.setDe018MerCatCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE018_MER_CAT_CD").asText());
            msg.setDe019AcqCtryCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE019_ACQ_CTRY_CD").asText());
            msg.setDe022PosMode(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE022_POS_MODE").asText());
            msg.setDe023CrdSeqNo(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE023_CRD_SEQ_NO").asText());
            msg.setDe025PosCondCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE025_POS_COND_CD").asText());
            msg.setDe026PinCapCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE026_PIN_CAP_CD").asText());
            msg.setDe032AcqCd(JsonUtil.getVal(rootIso8583,"/body/iso8583/DE032_ACQ_CD").asText());
            msg.setDe035Trk2Dat(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE035_TRK2_DAT").asText());
            msg.setDe036Trk3Dat(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE036_TRK3_DAT").asText());
            msg.setDe037RelRefNo(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE037_REL_REF_NO").asText());
            msg.setDe038AuthIdRes(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE038_AUTH_ID_RES").asText());
            msg.setDe039ResCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE039_RES_CD").asText());
            msg.setDe041CrdAcptTrm(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE041_CRD_ACPT_TRM").asText());
            msg.setDe042CrdAcptId(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE042_CRD_ACPT_ID").asText());
            msg.setDe043CrdAcptLoc(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE043_CRD_ACPT_LOC").asText());
            msg.setDe045Trk1Dat(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE045_TRK1_DAT").asText());
            msg.setDe048AddPrvInf(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE048_ADD_PRV_INF").asText());
            msg.setDe049TrnCcy(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE049_TRN_CCY").asText());
            msg.setDe050StlCcy(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE050_STL_CCY").asText());
            msg.setDe051BilCcy(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE051_BIL_CCY").asText());
            msg.setDe052Pin(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE052_PIN").asText());
            msg.setDe054AddAmt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE054_ADD_AMT").asText());
            msg.setDe055EmvDat(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE055_EMV_DAT").asText());
            msg.setDe060CnlTp(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE060_CNL_TP").asText());
            msg.setDe062NapSvcCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE062_NAP_SVC_CD").asText());
            msg.setDe063TrnRefNo(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE063_TRN_REF_NO").asText());
            msg.setDe070NetMgtCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE070_NET_MGT_CD").asText());
            msg.setDe090OrgTrnKey(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE090_ORG_TRN_KEY").asText());
            msg.setDe100BenCd(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE100_BEN_CD").asText());
            msg.setDe102SndAccInf(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE102_SND_ACC_INF").asText());
            msg.setDe103RcvAccInf(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE103_RCV_ACC_INF").asText());
            msg.setDe104TrnCont(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE104_TRN_CONT").asText());
            msg.setDe105NewPin(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE105_NEW_PIN").asText());
            msg.setDe120BenInf(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE120_BEN_INF").asText());
            msg.setDe128MacDat(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE128_MAC_DAT").asText());
            transMessageISO8583Repository.save(msg);
        } catch (Exception e) {
            logger.error("Exception when handle consumeMsgIso8583:" + e.getMessage());
        }
    }

    @KafkaListener(topics = "TOPIC.INQUIRY.SAVE.INFO", groupId = "group01")
    public void consumeAchCustomerInfo(CustomKafkaMessage kafkaMessage) {
        try {
            logger.debug("consumeAchCustomerInfo Consumed Message: "+ kafkaMessage.getTransId());
            AchCustomerInfo achCustomerInfo = new AchCustomerInfo();
            String typeTrans = "";

            String orgMsgIso8583 = kafkaMessage.getMessage();
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootIso8583 = objectMapper.readTree(orgMsgIso8583);

            String acctNo = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE103_RCV_ACC_INF").asText();
            achCustomerInfo.setCdtrAcctNo(acctNo);
            achCustomerInfo.setCdtrName(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE120_BEN_INF").asText());
            achCustomerInfo.setCdtrAddress(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE048_ADD_PRV_INF").asText());
            achCustomerInfo.setCdtrAcctType(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE003_PROC_CD").asText());

            String transCate = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE003_PROC_CD").asText();
            typeTrans = transCate.substring(4, 6);
            if (AppConstant.Common.ACCT.equals(typeTrans)) {
                achCustomerInfo.setCdtrMemId(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE100_BEN_CD").asText());
            } else if (AppConstant.Common.CARD.equals(typeTrans)) {
                achCustomerInfo.setCdtrMemId(acctNo.substring(0, 6));
            }

            achCustomerInfo.setCcy(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE049_TRN_CCY").asText());
            achCustomerInfo.setDbtrAcctNo(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE102_SND_ACC_INF").asText());
            achCustomerInfo.setDbtrMemId(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE032_ACQ_CD").asText());
            achCustomerInfo.setModifiedOn(new Date());

            achCustomerInfoService.saveAchCustomerInfo(achCustomerInfo);
        } catch (Exception e) {
            logger.error("Exception when handle consumeAchCustomerInfo:" + e.getMessage());
        }
    }

//    private DataObj handlePutMxNPResp(RestDataObj restDataObj) {
//        DataObj dataObj = new DataObj();
//        try {
//            String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
//            if (restDataObj != null && restDataObj.getHttpStatus() != null && restDataObj.getResponse() != null) {
//                if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
//                    NPResponse npResponse = JsonUtil.parseJson2NPResponse(restDataObj.getResponse());
//                    partnerCode = restDataObj.getHttpStatus().toUpperCase() + "_" + (StringUtils.isEmpty(npResponse.getType()) ? "NULL" : npResponse.getType().toUpperCase())
//                            + "_" + (StringUtils.isEmpty(npResponse.getDuplicated()) ? "NULL" : npResponse.getDuplicated().toUpperCase());
//                } else if ("".equals(restDataObj.getHttpStatus())) {
//                    partnerCode = AppConstant.HTTPConfig.HTTP_STATUS_5XX;
//                } else {
//                    partnerCode = restDataObj.getHttpStatus();
//                }
//            } else {
//                partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
//            }
//            logger.info("~~~~~~~partnerCode handlePutMxNPResp~~~~~~" + partnerCode);
//            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH, partnerCode);
//        } catch (Exception e) {
//            logger.error("Exception when handle handlePutMxNPResp:" + e.getMessage());
//            dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
//            dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
//        }
//        return dataObj;
//    }

//    private DataObj handlePutMxBankResp(RestDataObj restDataObj) {
//        DataObj dataObj = new DataObj();
//        try {
//            String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
//            if (restDataObj != null && restDataObj.getHttpStatus() != null) {
//                if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
//                    partnerCode = restDataObj.getHttpStatus().toUpperCase();
//                    if (!StringUtils.isEmpty(restDataObj.getResponse())) {
//                        ObjectMapper objectMapper = new ObjectMapper();
//                        JsonNode jsonNodeIso8583 = objectMapper.readTree(restDataObj.getResponse());
//
//                        partnerCode = JsonUtil.getVal(jsonNodeIso8583, "/body/iso8583/DE039_RES_CD").asText();
//
//                    }
//                } else if ("".equals(restDataObj.getHttpStatus())) {
//                    partnerCode = AppConstant.HTTPConfig.HTTP_STATUS_5XX;
//                } else {
//                    partnerCode = restDataObj.getHttpStatus();
//                }
//            } else {
//                if (restDataObj != null && restDataObj.isReadTimedOut()) {
//                    partnerCode = AppConstant.AchEcode.ECODE_UNKONW; // time out at iso8583 code
//                    dataObj.setEdesc("Send success to Igate but no response after 15s");
//                } else {
//                    partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
//                }
//
//            }
//            logger.info("~~~~~~~partnerCode handlePutMxBankResp~~~~~~" + partnerCode);
//            dataObj.setEcode(partnerCode);
//            if ("00".equals(partnerCode))
//                dataObj.setEdesc("Get response successfully from bank");
////            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_BANK, AppConstant.ChannelId.ACH_IN, partnerCode);
//        } catch (Exception e) {
//            logger.error("Exception when handle handlePutMxBankResp:" + e.getMessage());
//            dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
//            dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
//        }
//        return dataObj;
//    }
//    private DataObj handleInqTransNrtNPResp(RestDataObj restDataObj) {
//        DataObj dataObj = new DataObj();
//        try {
//            String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
//            if (restDataObj != null && restDataObj.getHttpStatus() != null && restDataObj.getResponse() != null) {
//                if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
//                    NPResponse npResponse = JsonUtil.parseJson2NPResponse(restDataObj.getResponse());
//                    partnerCode = restDataObj.getHttpStatus().toUpperCase() + "_" + (StringUtils.isEmpty(npResponse.getType()) ? "NULL" : npResponse.getType().toUpperCase())
//                            + "_" + (StringUtils.isEmpty(npResponse.getDuplicated()) ? "NULL" : npResponse.getDuplicated().toUpperCase());
//                } else if (AppConstant.HTTPConfig.HTTP_STATUS_404.equals(restDataObj.getHttpStatus())) {
//                    JsonNode jsonReps = JsonUtil.toJsonNode(restDataObj.getResponse());
//                    partnerCode = JsonUtil.getVal(jsonReps, "/errorCode").asText();
//                } else {
//                    partnerCode = restDataObj.getHttpStatus();
//                }
//            } else {
//                partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
//            }
//            dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
//                    partnerCode);
//        } catch (Exception e) {
//            logger.error("Exception when handle handleInqTransNrtNPResp:" + e.getMessage());
//            dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
//            dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
//        }
//        return dataObj;
//    }

}
