package com.leadon.apigw.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.kafka.CustomKafkaMessage;
import com.leadon.apigw.model.DataObj;
import com.leadon.apigw.model.TransAchActivity;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.repository.TransAchActivityRepository;
import com.leadon.apigw.repository.TransMessageISO8583Repository;
import com.leadon.apigw.repository.TransactionRepository;
import com.leadon.apigw.service.KafkaProducerService;
import com.leadon.apigw.util.ACHUtil;
import com.leadon.apigw.util.JsonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import com.leadon.apigw.dto.*;
import com.leadon.apigw.service.TransactionService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("transactionService")
public class TransactionServiceImpl implements TransactionService {

	public static Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

	@Autowired
	private KafkaProducerService producer;

	@Autowired
	private TransactionRepository transactionRepository;

	@Autowired
	private TransAchActivityRepository transAchActivityRepository;

	@Autowired
	private TransMessageISO8583Repository transMessageISO8583Repository;

	public DataObj handleAckNack(JsonNode root, String message) {
		Map<String, String> map = new HashMap<>();
		try {
			String orgSenderRefId = JsonUtil.getVal(root, "/Header/SenderReference").asText();
			String description = AppConstant.MsgDesc.DESC_ACKNACK;
			String transStep = JsonUtil.getVal(root, "/Payload/ack_nak/type").asText();
			String errorCode = "", errorDesc = "", groupStatus = "";
			if (AppConstant.TransStep.ACT_STEP_ACK.equals(transStep)) {
				errorCode = AppConstant.AchEcode.ECODE_ACK;
				errorDesc = AppConstant.AchEcode.EDESC_ACK;
				groupStatus = AppConstant.AchEcode.ECODE_ACK;
			} else if (AppConstant.TransStep.ACT_STEP_NACK.equals(transStep)) {
				JsonNode Code = JsonUtil.getVal(root, "/Payload/ack_nak/Data/Code");
				JsonNode Description = JsonUtil.getVal(root, "/Payload/ack_nak/Data/Description");
				errorCode = (Code == null) ? AppConstant.AchEcode.ECODE_NACK : Code.asText();
				errorDesc = (Description == null) ? AppConstant.AchEcode.EDESC_NACK : Description.asText();
				groupStatus = AppConstant.AchEcode.ECODE_NACK;
			}
			TransactionDto transactionDto = checkExistAndUpdateTrans(root, message, orgSenderRefId, description,
					errorCode, errorDesc, transStep, groupStatus, null, null);
			if (transactionDto == null) {
				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
						AppConstant.ResponseMsg.RESP_FAIL_MESSAGE, map);
			}
			// get msg iso8583 send to bank
			DataObj selectMsgIso8583 = transAchActivityRepository.selectMsgIso8583(orgSenderRefId, AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
			String messageIso8583 = selectMsgIso8583.getDataVal("msgContent");
			Long activitiId= transAchActivityRepository.checkExsitIso8583ToBank(transactionDto.getTransId(), AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
			logger.info("+++checkExsitIso8583ToBank: " + activitiId);
			if (!StringUtils.isEmpty(activitiId)) {
				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
						map);
			}
			else if (activitiId == null &&AppConstant.TransStep.ACT_STEP_NACK.equals(transStep) && !StringUtils.isEmpty(messageIso8583)) {
				logger.debug("++++Get successfully original Message iso8583 NAK:");

//				String iso8583Message = MsgBuilder.buildISO8583NRTNAK(messageIso8583);

				// get err code
				DataObj dataObj = null;
				dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
						errorCode);
				String bankEcode = StringUtils.isEmpty(dataObj.getEcode()) ? "30" : dataObj.getEcode();
				String bankEdesc = StringUtils.isEmpty(dataObj.getEdesc()) ? errorDesc : dataObj.getEdesc();

				// Push msg to queue
				CustomKafkaMessage kkMsg = new CustomKafkaMessage();
				kkMsg.setSenderRefId(orgSenderRefId);
				kkMsg.setErrCode(bankEcode);
				kkMsg.setErrDesc(bankEdesc);
				kkMsg.setMessage(messageIso8583);
				kkMsg.setMsgIdr(AppConstant.MsgIdr.ACKNACK);
				kkMsg.setActStep(AppConstant.TransStep.ACT_STEP_SEND_NACK);
				kkMsg.setActDesc("Send NACK message ISO8583 to Bank");
				kkMsg.setTransId(String.valueOf(transactionDto.getTransId()));
				producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_NACK_IN_ISO8583);
			}
			map.put("transId", String.valueOf(transactionDto.getTransId()));
		} catch (Exception e) {
			logger.error("Exception when handle handleAckNack:" + e.getMessage());
			map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
			return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE, AppConstant.ResponseMsg.RESP_FAIL_MESSAGE,
					map);
		}
		map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
		return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
				map);
	}

//	public DataObj handleCamt025(JsonNode root, String message) {
//		Map<String, String> map = new HashMap<>();
//		try {
//			TransactionDto transactionDto = new TransactionDto();
//			TransAchDetail transAchDetail = new TransAchDetail();
//			TransAchActivity transAchActivity = new TransAchActivity();
//			String timeStamp = JsonUtil.getVal(root, "/Header/Timestamp").asText();
//			String orgSenderRefId = JsonUtil.getVal(root, "/Payload/Document/Rct/RctDtls/OrgnlMsgId/MsgId").asText();
//			String senderRefId = JsonUtil.getVal(root, "/Header/SenderReference").asText();
//			String messageIdentifier = JsonUtil.getVal(root, "/Header/MessageIdentifier").asText();
//
//			String transStep = "", errorCode = "", errorDesc = "", groupStatus = "";
//			transactionDto = checkTransExist(orgSenderRefId, message);
//			if (transactionDto == null) {
//				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//				logger.debug("camt 025 transactionDto : duplicated");
//				return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, map);
//			}
//			map.put("transId", String.valueOf(transactionDto.getTransId()));
//			// CHECK camt025 payment
//			if (AppConstant.TransCate.BANK_PAYMENT.contains(transactionDto.getTransCate())
//					&& AppConstant.TransDirection.TRANS_OUT.equals(transactionDto.getTransInOut())) {
//				transStep = AppConstant.TransStep.ACT_STEP_REJECT;
//				JsonNode camt025Desc = JsonUtil.getVal(root, "/Payload/Document/Rct/RctDtls/ReqHdlg/0/Desc");
//				if (camt025Desc != null) {
//					String[] splitCamt025Desc = null;
//					String desc = camt025Desc.asText();
//					if(desc.contains("\n"))
//						splitCamt025Desc = desc.split("\n");
//					else if(desc.contains(","))
//						splitCamt025Desc = desc.split(",");
//					if(!StringUtils.isEmpty(splitCamt025Desc)) {
//						errorCode = splitCamt025Desc[0];
//						errorDesc = splitCamt025Desc[1];
//					}
//				} else {
//					errorCode = AppConstant.AchEcode.ECODE_CAMT025_RJCT;
//					errorDesc = AppConstant.AchEcode.EDESC_CAMT025_RJCT;
//				}
//				groupStatus = AppConstant.AchEcode.ECODE_CAMT025_RJCT;
//			} else {
//				transStep = AppConstant.TransStep.ACT_STEP_RECEIPT;
//				JsonNode StsCd = JsonUtil.getVal(root, "/Payload/Document/Rct/RctDtls/ReqHdlg/0/StsCd");
//				JsonNode Desc = JsonUtil.getVal(root, "/Payload/Document/Rct/RctDtls/ReqHdlg/0/Desc");
//				errorCode = (StsCd != null) ? StsCd.asText() : "";
//				errorDesc = (Desc != null) ? Desc.asText() : "";
//				groupStatus = (StsCd != null) ? StsCd.asText() : "OK";
//			}
//
//			// transactionDto.setSenderRefId(senderRefId);
//			transactionDto.setMessageIdentifier(messageIdentifier);
//			transactionDto.setTransDesc(AppConstant.MsgDesc.DESC_CAMT025);
//			transactionDto.setMsgType(AppConstant.LogConfig.RESPONSE);
//			transactionDto.setMsgContent(message);
//			transactionDto.setMsgDt(timeStamp);
//			transactionDto.setErrorCode(errorCode);
//			transactionDto.setErrorDesc(errorDesc);
//			transactionDto.setTransStep(transStep);
//			transactionDto.setTransStepStat(AppConstant.TransStepState.TRANS_STEP_STATE_SUCCESS);
//			transactionDto.setGroupStatus(groupStatus);
//
//			// camt025 In payment call insert transAchActivity
//			if (AppConstant.TransDirection.TRANS_IN.equals(transactionDto.getTransInOut())
//					&& AppConstant.TransCate.BANK_PAYMENT.contains(transactionDto.getTransCate())) {
//				// Push activity request of receive camt025 from napas
//				transAchActivityRepository.pushActivity(transactionDto.getTransId(), senderRefId, messageIdentifier,
//						AppConstant.MsgDesc.DESC_CAMT025, AppConstant.LogConfig.RESPONSE, message,
//						DateUtil.parseTimestampXXX2Date(timeStamp), transStep, errorCode, errorDesc);
//
////				if (activityId == 0) {
////					map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
////					return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
////							AppConstant.ResponseMsg.RESP_FAIL_MESSAGE, map);
////				}
//			} else {
//				// CAll PR_HANDLE_ACH_DETAIL_ACTIVITY
//				ACHUtil.buildReqHandleAchMsg(transAchDetail, transAchActivity, transactionDto);
//				DataObj dataObj2 = null;
//				dataObj2 = transactionRepository.handleAchDetailActivity(transAchDetail, transAchActivity);
//
//				if (!AppConstant.SystemResponse.SUCCESS_CODE.equals(dataObj2.getEcode())) {
//					map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//					logger.debug("camt 025 SystemResponse : not duplicated");
//					return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//							AppConstant.ResponseMsg.RESP_FAIL_MESSAGE, map);
//				}
//
//				if (AppConstant.TransDirection.TRANS_OUT.equals(transactionDto.getTransInOut())) {
//					boolean checkUpdateTrans = handleUpdateTrans(transactionDto.getTransId(), errorCode, errorDesc);
//					if (!checkUpdateTrans) {
//						map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//						return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//								AppConstant.ResponseMsg.RESP_FAIL_MESSAGE, map);
//					}
//				}
//			}
//			// call to client
//			// get msg iso8583 send to bank
//			DataObj selectMsgIso8583 = transAchActivityRepository.selectMsgIso8583(orgSenderRefId, AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
//			String messageIso8583 = selectMsgIso8583.getDataVal("msgContent");
//			Long activitiId= transAchActivityRepository.checkExsitIso8583ToBank(transactionDto.getTransId(), AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
//			logger.info("+++checkExsitIso8583ToBank: " + activitiId);
//			if (!StringUtils.isEmpty(activitiId)) {
//				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//				return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
//						map);
//			}
//			else if (activitiId == null && "ERRC".equals(errorCode) && !StringUtils.isEmpty(messageIso8583)) {
//				logger.debug("++++Get successfully original Message iso8583 CAMT025:");
//
////				String iso8583Message = MsgBuilder.buildISO8583NRTNAK(messageIso8583);
//
//				// get err code
//				DataObj dataObj = null;
//				dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
//						errorCode);
//				String bankEcode = StringUtils.isEmpty(dataObj.getEcode()) ? "30" : dataObj.getEcode();
//				String bankEdesc = StringUtils.isEmpty(dataObj.getEdesc()) ? errorDesc : dataObj.getEdesc();
//
//				// Push msg to queue
//				CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//				kkMsg.setSenderRefId(senderRefId);
//				kkMsg.setOrgSenderRefId(orgSenderRefId);
//				kkMsg.setErrCode(bankEcode);
//				kkMsg.setErrDesc(bankEdesc);
//				kkMsg.setMessage(messageIso8583);
//				kkMsg.setMsgIdr(AppConstant.MsgIdr.CAMT025);
//				kkMsg.setActStep(AppConstant.TransStep.ACT_STEP_SEND_CAMT025);
//				kkMsg.setActDesc("Send CAMT025 message ISO8583 to Bank");
//				kkMsg.setTransId(String.valueOf(transactionDto.getTransId()));
//				producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_NACK_IN_ISO8583);
//			}
//		} catch (Exception e) {
//			logger.error("Exception when handle handleCamt025:" + e.getMessage());
//			map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//			return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE, AppConstant.ResponseMsg.RESP_FAIL_MESSAGE,
//					map);
//		}
//		map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//		return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
//				map);
//
//	}
//
//	public DataObj handleAdmi002(JsonNode root, String message) {
//		Map<String, String> map = new HashMap<>();
//		try {
//			String errorCode = "", errorDesc = "", groupStatus = "";
//			String orgSenderRefId = JsonUtil.getVal(root, "/Payload/Document/admi.002.001.01/RltdRef/Ref").asText();
//			String senderRefId = JsonUtil.getVal(root, "/Header/SenderReference").asText();
//			String description = AppConstant.MsgDesc.DESC_ADMI;
//			String transStep = AppConstant.TransStep.ACT_STEP_REJECT;
//			JsonNode RjctgPtyRsn = JsonUtil.getVal(root, "/Payload/Document/admi.002.001.01/Rsn/RjctgPtyRsn");
//			JsonNode RsnDesc = JsonUtil.getVal(root, "/Payload/Document/admi.002.001.01/Rsn/RsnDesc");
//			errorCode = (RjctgPtyRsn != null) ? RjctgPtyRsn.asText() : AppConstant.AchEcode.ECODE_ADMI;
//			errorDesc = (RsnDesc != null) ? RsnDesc.asText() : AppConstant.AchEcode.EDESC_ADMI;
//			groupStatus = AppConstant.AchEcode.ECODE_ADMI;
//
//			TransactionDto transactionDto = checkExistAndUpdateTrans(root, message, orgSenderRefId, description,
//					errorCode, errorDesc, transStep, groupStatus, null, null);
//
//			if (transactionDto == null) {
//				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//				return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
//						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, map);
//			}
//			map.put("transId", String.valueOf(transactionDto.getTransId()));
//			// call back to client
//			DataObj selectMsgIso8583 = transAchActivityRepository.selectMsgIso8583(orgSenderRefId, AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
//			String messageIso8583 = selectMsgIso8583.getDataVal("msgContent");
//			Long activitiId= transAchActivityRepository.checkExsitIso8583ToBank(transactionDto.getTransId(), AppConstant.MsgIdr.ISO8583, AppConstant.TransStep.ACT_STEP_SEND_NRT);
//			logger.info("+++checkExsitIso8583ToBank: " + activitiId);
//			if (!StringUtils.isEmpty(activitiId)) {
//				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//				return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
//						map);
//			}
//			else if (activitiId == null && !StringUtils.isEmpty(messageIso8583)) {
//				logger.debug("+++++Get successfully original Message iso8583 ADMI:");
//
//				DataObj dataObj = null;
//				dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
//						errorCode);
//				String bankEcode = StringUtils.isEmpty(dataObj.getEcode()) ? "30" : dataObj.getEcode();
//				String bankEdesc = StringUtils.isEmpty(dataObj.getEdesc()) ? errorDesc : dataObj.getEdesc();
//
////				String iso8583Message = MsgBuilder.buildISO8583NRTNAK(messageIso8583);
//				// Push msg to queue
//				CustomKafkaMessage kkMsg = new CustomKafkaMessage();
//				kkMsg.setSenderRefId(senderRefId);
//				kkMsg.setOrgSenderRefId(orgSenderRefId);
//				kkMsg.setErrCode(bankEcode);
//				kkMsg.setErrDesc(bankEdesc);
//				kkMsg.setMessage(messageIso8583);
//				kkMsg.setMsgIdr(AppConstant.MsgIdr.ADMI002);
//				kkMsg.setActStep(AppConstant.TransStep.ACT_STEP_SEND_ADMI);
//				kkMsg.setActDesc("Send ADMI message ISO8583 to Bank");
//				kkMsg.setTransId(String.valueOf(transactionDto.getTransId()));
//				producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_NACK_IN_ISO8583);
//			}
//		} catch (Exception e) {
//			logger.error("Exception when handle handleAdmi002:" + e.getMessage());
//			map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//			return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE, AppConstant.ResponseMsg.RESP_FAIL_MESSAGE,
//					map);
//		}
//		map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
//		return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
//				map);
//	}

	public DataObj handlePacs002(JsonNode root, String message) {
		Map<String, String> map = new HashMap<>();
		try {
			TransactionDto transactionDto = new TransactionDto();
			Long trasId;
			String activityStep = AppConstant.TransStep.ACT_STEP_SEND_NRT;
            String messageIso8583;
            String authIdResDe038 = "";
			String senderRefId = JsonUtil.getVal(root, "/Header/SenderReference").asText();
			String orgSenderRefId = JsonUtil
					.getVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgId").asText();
			logger.info("+++ orgSenderRefId: " + orgSenderRefId);
			String description = AppConstant.MsgDesc.DESC_PACS002;
			String transStep = "", errorCode = "", errorDesc = "", groupStatus = "";
			String sessionNo = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/AcctSvcrRef")
					.asText();

			JsonNode GrpSts = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/GrpSts");
			JsonNode TxSts = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/TxSts");
			JsonNode StsId = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsId");
			String OrgnlMsgNmId = JsonUtil.getVal(root,
					"/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId").asText();

			JsonNode AddtlInfItemForDe038 = JsonUtil.getVal(root,
					"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf");
			if (AddtlInfItemForDe038 != null) {
				for (int i = 0; i < AddtlInfItemForDe038.size(); i++) {
					String identifycationCode = AddtlInfItemForDe038.get(i).asText().substring(0, 5);
					if (identifycationCode.equals(AppConstant.PacsStatus.ACH_PREFIX_CODE)) {
						if (!StringUtils.isEmpty(AddtlInfItemForDe038.get(i).asText()))
							authIdResDe038 = ACHUtil.subStringbyIndex(AddtlInfItemForDe038.get(i).asText(), 6);
					}
				}
			}
			if (AppConstant.GroupStatus.GRPSTS_ACSP.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_ACSP.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_NOAN.equals(StsId.asText())
					&& AppConstant.MsgIdr.PACS004.equals(OrgnlMsgNmId)) {
				transStep = AppConstant.TransStep.ACT_STEP_POSTED;
				errorCode = AppConstant.AchEcode.ECODE_ACSP_NOAN_ACSP_P4;
				errorDesc = AppConstant.AchEcode.EDESC_ACSP_NOAN_ACSP_P4;
				groupStatus = AppConstant.AchEcode.ECODE_ACSP_NOAN_ACSP_P4;
			} else if (AppConstant.GroupStatus.GRPSTS_RJCT.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_RJCT.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_NOAN.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_REJECT;
				JsonNode Prtry2 = JsonUtil.getVal(root,
						"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/Rsn/Prtry");
				JsonNode AddtlInf = JsonUtil.getVal(root,
						"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0");
				errorCode = (Prtry2 != null) ? Prtry2.asText() : AppConstant.AchEcode.ECODE_RJCT_NOAN_RJCT;
				errorDesc = (AddtlInf != null) ? AddtlInf.asText() : AppConstant.AchEcode.EDESC_RJCT_NOAN_RJCT;
				groupStatus = AppConstant.AchEcode.ECODE_RJCT_NOAN_RJCT;
			} else if (AppConstant.GroupStatus.GRPSTS_ACSP.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_ACSP.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_NOAN.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_POSTED;
				errorCode = AppConstant.AchEcode.ECODE_ACSP_NOAN_ACSP;
				errorDesc = AppConstant.AchEcode.EDESC_ACSP_NOAN_ACSP;
				groupStatus = AppConstant.AchEcode.ECODE_ACSP_NOAN_ACSP;
			} else if (AppConstant.GroupStatus.GRPSTS_ACSP.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_ACSP.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_NAUT.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_UPD_NAUT_TIMEOUT;
//				JsonNode AddtlInfItem = JsonUtil.getVal(root,
//						"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf");
//				for (int i = 0; i < AddtlInfItem.size(); i++) {
//					String identifycationCode = AddtlInfItem.get(i).asText().substring(0, 5);
//					if (identifycationCode.equals(AppConstant.AchEcode.INDENTIFICATION_ERROR_CODE)) {
//						errorCode = AddtlInfItem.get(i).asText().substring(5);
//					} else if (identifycationCode.equals(AppConstant.AchEcode.INDENTIFICATION_ERROR_DESC)) {
//						errorDesc = AddtlInfItem.get(i).asText().substring(5);
//					}
//				}
				if (errorCode.equals("") || errorCode == null) {
					errorCode = AppConstant.AchEcode.ECODE_ACSP_NAUT_ACSP;
					errorDesc = AppConstant.AchEcode.EDESC_ACSP_NAUT_ACSP;
				}
				groupStatus = AppConstant.AchEcode.ECODE_ACSP_NAUT_ACSP;
			} else if (AppConstant.GroupStatus.GRPSTS_RJCT.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_RJCT.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_NAUT.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_UPD_NAUT;
				JsonNode AddtlInfItem = JsonUtil.getVal(root,
						"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf");
				for (int i = 0; i < AddtlInfItem.size(); i++) {
					String identifycationCode = AddtlInfItem.get(i).asText().substring(0, 5);
					if (identifycationCode.equals(AppConstant.AchEcode.INDENTIFICATION_ERROR_CODE)) {
						errorCode = AddtlInfItem.get(i).asText().substring(5);
					} else if (identifycationCode.equals(AppConstant.AchEcode.INDENTIFICATION_ERROR_DESC)) {
						errorDesc = AddtlInfItem.get(i).asText().substring(5);
					}
				}
				if (errorCode.equals("") || errorCode == null) {
					errorCode = AppConstant.AchEcode.ECODE_RJCT_NAUT_RJCT;
					errorDesc = AppConstant.AchEcode.EDESC_RJCT_NAUT_RJCT;
				}
				groupStatus = AppConstant.AchEcode.ECODE_RJCT_NAUT_RJCT;
			} else if (AppConstant.GroupStatus.GRPSTS_ACSP.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_ACSP.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_AUTH.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_UPD_AUTH;
				errorCode = AppConstant.AchEcode.ECODE_ACSP_AUTH_ACSP;
				errorDesc = AppConstant.AchEcode.EDESC_ACSP_AUTH_ACSP;
				groupStatus = AppConstant.AchEcode.ECODE_ACSP_AUTH_ACSP;
			} else if (AppConstant.GroupStatus.GRPSTS_ACSC.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_ACSC.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_AUTH.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_UPD_AUTH;
				errorCode = AppConstant.AchEcode.ECODE_ACSC_AUTH_ACSC;
				errorDesc = AppConstant.AchEcode.EDESC_ACSC_AUTH_ACSC;
				groupStatus = AppConstant.AchEcode.ECODE_ACSC_AUTH_ACSC;
			} else if (AppConstant.GroupStatus.GRPSTS_ACSC.equals(GrpSts.asText())
					&& AppConstant.TxStatus.TXSTS_ACSC.equals(TxSts.asText())
					&& AppConstant.StatusId.STSID_NAUT.equals(StsId.asText())) {
				transStep = AppConstant.TransStep.ACT_STEP_UPD_NAUT;
				errorCode = AppConstant.AchEcode.ECODE_ACSC_NAUT_ACSC;
				errorDesc = AppConstant.AchEcode.EDESC_ACSC_NAUT_ACSC;
				groupStatus = AppConstant.AchEcode.ECODE_ACSC_NAUT_ACSC;
			} else {
				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, map);
			}

			logger.info("+++errorCode" + errorCode + ", errorDesc" + errorDesc + ", transStep" + transStep + ", groupStatus" + groupStatus);

			transactionDto = checkExistAndUpdateTrans(root, message, orgSenderRefId, description,
					errorCode, errorDesc, transStep, groupStatus, sessionNo, AppConstant.MsgIdr.PACS002);
			if (transactionDto == null) {
				map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
				return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE,
						AppConstant.ResponseMsg.RESP_INVALID_MESSAGE, map);
			}
			trasId = transactionDto.getTransId();
			map.put("transId", String.valueOf(trasId));

			// select msg return pacs004 request to NP
			// get org_sender_ref_id for NRT msg
			if (AppConstant.MsgIdr.PACS004.equals(OrgnlMsgNmId)) {
				DataObj dataObj;
//				activityStep = AppConstant.TransStep.ACT_STEP_SEND_PACS008;
				dataObj = transAchActivityRepository.selectMsgReturnBySenderRefId(orgSenderRefId, AppConstant.MsgIdr.PACS004, AppConstant.LogConfig.REQUEST);
				JsonNode pacs004JsonNode = JsonUtil.toJsonNode(dataObj.getDataVal("msgContent"));
				orgSenderRefId = JsonUtil.getVal(pacs004JsonNode, "/Payload/Document/PmtRtr/OrgnlGrpInf/OrgnlMsgId").asText();
				trasId = Long.parseLong(dataObj.getDataVal("transId"));

				DataObj dataObj2 = new DataObj();
				dataObj2 = transMessageISO8583Repository.selectMesageReturn8583ByOrgSenderRefId(orgSenderRefId);
                messageIso8583 = dataObj2.getDataVal("msContent");

				logger.info("+++ case return pacs004, orgSenderRefId: " + orgSenderRefId + ", trasId: " + trasId);
			} else {
                DataObj selectMsgIso8583 = transAchActivityRepository.selectMsgIso8583(orgSenderRefId, AppConstant.MsgIdr.ISO8583, activityStep);
                messageIso8583 = selectMsgIso8583.getDataVal("msgContent");
            }

			// call back to client
			Long checkExsitIso8583ToBank = transAchActivityRepository.checkExsitIso8583ToBank(trasId, AppConstant.MsgIdr.ISO8583, activityStep);
			logger.info("+++checkExsitIso8583ToBank: " + checkExsitIso8583ToBank);

			// Check send invest
            DataObj dataObjCheckInvest = transAchActivityRepository.checkExsitPacs028SendToNP(String.valueOf(trasId));
            String countCheck = dataObjCheckInvest.getDataVal("countCheck");
            String messageInvestIso8583 = dataObjCheckInvest.getDataVal("message");
            logger.info("+++checkExsitPacs028SendToNP, errorCode:" + dataObjCheckInvest.getEcode() + ", countCheck:" + countCheck);

            // If not have invest msg and ready send to bank return
            if (!StringUtils.isEmpty(checkExsitIso8583ToBank) && 0 == Integer.parseInt(countCheck)) {
                map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
                return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
                        map);
            }

            // Only send back to bank when activitiId = null
			if ((checkExsitIso8583ToBank == null && !StringUtils.isEmpty(messageIso8583)) || (countCheck != null && 1 <= Integer.parseInt(countCheck))) {
				logger.debug("++++++ Get successfully original Message iso8583 Pacs002:" + ", errorCode:" + errorCode);

				DataObj dataObj = null;
				dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
						errorCode);

				String bankEcode = StringUtils.isEmpty(dataObj.getEcode()) ? "30" : dataObj.getEcode();
				String bankEdesc = StringUtils.isEmpty(dataObj.getEdesc()) ? errorDesc : dataObj.getEdesc();
				logger.debug("++++++ Map bank error code:" + bankEcode + " - " + bankEdesc);

//				String iso8583Message = MsgBuilder.buildISO8583NRTNAK(messageIso8583);
				// Push msg to queue
				CustomKafkaMessage kkMsg = new CustomKafkaMessage();
				kkMsg.setSenderRefId(senderRefId);
				kkMsg.setOrgSenderRefId(orgSenderRefId);
				kkMsg.setErrCode(bankEcode);
				kkMsg.setErrDesc(bankEdesc);
				kkMsg.setMessage(messageIso8583);
				kkMsg.setMsgIdr(AppConstant.MsgIdr.PACS002);
				kkMsg.setActStep(AppConstant.TransStep.ACT_STEP_SEND_PACS002);
				kkMsg.setActDesc("Send Pacs002 message ISO8583 to Bank");
				kkMsg.setAuthIdRes(authIdResDe038);
				kkMsg.setTransId(String.valueOf(trasId));
				if (countCheck != null && 1 <= Integer.parseInt(countCheck)) {
					kkMsg.setMessage(messageInvestIso8583);
					kkMsg.setOrgMessage(messageIso8583);
					kkMsg.setActDesc("Send Pacs002 message ISO8583 for pacs028 to Bank");
					kkMsg.setCheckInvest(countCheck);
				}

				producer.sendMessage(kkMsg, AppConstant.QueueConfig.TOPIC_NACK_IN_ISO8583);
			}
		} catch (Exception e) {
			logger.error("Exception when handle handlePacs002:" + e.getMessage());
			map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
			return new DataObj(AppConstant.ResponseType.RESP_FAILURE_TYPE, AppConstant.ResponseMsg.RESP_FAIL_MESSAGE,
					map);
		}
		map.put("duplicated", AppConstant.ResponseDupl.RESP_NOT_DUPLICATED);
		return new DataObj(AppConstant.ResponseType.RESP_SUCCESS_TYPE, AppConstant.ResponseMsg.RESP_SUCCESS_MESSAGE,
				map);
	}

	private TransactionDto checkExistAndUpdateTrans(JsonNode root, String message, String orgSenderRefId,
			String description, String errorCode, String errorDesc, String transStep, String groupStatus,
			String sessionNo, String msgDir) {
		TransactionDto transactionDto = new TransactionDto();
		try {

			TransAchDetail transAchDetail = new TransAchDetail();
			TransAchActivity transAchActivity = new TransAchActivity();
			String timeStamp = JsonUtil.getVal(root, "/Header/Timestamp").asText();
			String senderRefId = JsonUtil.getVal(root,
					"/Header/SenderReference").asText();
			String messageIdentifier = JsonUtil.getVal(root, "/Header/MessageIdentifier").asText();
			String orgnlMsgNmId = JsonUtil.getVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId").asText();
			logger.info("+++orgnlMsgNmId: " + orgnlMsgNmId);
			// Check trans exist
			transactionDto = checkTransExist(orgSenderRefId, message);
			if (transactionDto == null) {
				return null;
			}
//			transactionDto.setSenderRefId(senderRefId);
			transactionDto.setMessageIdentifier(messageIdentifier);
			transactionDto.setTransDesc(description);
			transactionDto.setMsgType(AppConstant.LogConfig.RESPONSE);
			transactionDto.setMsgContent(message);
			transactionDto.setMsgDt(timeStamp);
			transactionDto.setErrorCode(errorCode);
			transactionDto.setErrorDesc(errorDesc);
			transactionDto.setTransStep(transStep);
			transactionDto.setTransStepStat(AppConstant.TransStepState.TRANS_STEP_STATE_SUCCESS);
			transactionDto.setGroupStatus(groupStatus);
			if (AppConstant.MsgIdr.PACS002.equals(messageIdentifier)) {
				transactionDto.setSessionNo(sessionNo);
			}
			// CAll PR_HANDLE_ACH_DETAIL_ACTIVITY
			ACHUtil.buildReqHandleAchMsg(transAchDetail, transAchActivity, transactionDto);
			DataObj dataObj2 = null;
			dataObj2 = transactionRepository.handleAchDetailActivity(transAchDetail, transAchActivity);
			logger.debug("After call handleAchDetailActivity, errCode: " + dataObj2.getEcode() + ", errDesc: " + dataObj2.getEdesc() + ", transId: "
					+ transactionDto.getTransId() + ", senderRef: " + orgSenderRefId + ", orgTransId: " + transactionDto.getOrgTransId()
					+ ", groupstatus: " + transactionDto.getGroupStatus());
			if (!AppConstant.SystemResponse.SUCCESS_CODE.equals(dataObj2.getEcode())) {
				return null;
			}

			if (AppConstant.TransDirection.TRANS_OUT.equals(transactionDto.getTransInOut())) {
				boolean checkUpdateTrans = handleUpdateTrans(transactionDto.getTransId(), errorCode, errorDesc);
				if (!checkUpdateTrans) {
					return null;
				}
			}
			// UPDATR TRANSACTION error code from pacs002
            // Check update pacs002 for invest
//			DataObj dataObjCheckInvest = transAchActivityRepository.checkExsitPacs028SendToNP(String.valueOf(transactionDto.getTransId()));
//			String countCheck = dataObjCheckInvest.getDataVal("countCheck");
            // check exist only 1 time updated pacs002 OUT - IN
//			int checkExsitPacs002Updated = transAchActivityRepository.checkExsitUpdatedPacs002(transactionDto.getTransId(), AppConstant.MsgIdr.PACS002);
//            if ((countCheck != null && 1 <= Integer.parseInt(countCheck)) || checkExsitPacs002Updated == 0 ) {
			Long orgTransId = transactionDto.getOrgTransId();
			DataObj dataObj3;
			if (AppConstant.MsgIdr.PACS002.equals(msgDir)) {
				if (!StringUtils.isEmpty(orgTransId) && !AppConstant.MsgIdr.PACS004.equals(orgnlMsgNmId)) {
					boolean checkUpdateTrans = handleUpdateTrans(orgTransId, errorCode, errorDesc);
					if (!checkUpdateTrans) {
						return null;
					}
				}
				if (!StringUtils.isEmpty(transactionDto.getTransId())) {
					boolean checkUpdateTrans = handleUpdateTrans(transactionDto.getTransId(), errorCode, errorDesc);
					if (!checkUpdateTrans) {
						return null;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Exception when handle checkExistAndUpdateTrans:" + e.getMessage());
			return null;
		}
		return transactionDto;
	}

	private TransactionDto checkTransExist(String orgSenderRefId, String message) {
		TransactionDto transactionDto = new TransactionDto();
		try {
			DataObj dataObj = null;
			dataObj = transactionRepository.getTransBySenderRef(orgSenderRefId);
			logger.debug("After call getTransBySenderRef, errCode: " + dataObj.getEcode() + ", errDesc: " + dataObj.getEdesc() + ", transId: "
					+ dataObj.getDataVal("transId") + ", senderRef: " + dataObj.getDataVal("orgSenderRefId"));
			if (!"00".equals(dataObj.getEcode()) || dataObj.getDataVal("transId") == null
					|| "".equals(dataObj.getDataVal("transId"))) {
				return null;
			}
			// push log req

			producer.pushMsgLogReq(dataObj.getDataVal("transId"), message, AppConstant.ChannelId.ACH,
					AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_NAPAS);

			transactionDto.setTransInOut(dataObj.getDataVal("transInOut"));
			transactionDto.setTransId(Long.valueOf(dataObj.getDataVal("transId")));
			String orgTransId = dataObj.getDataVal("orgTransId");
			logger.info("+++, orgTransId" + orgTransId);
			if (!"null".equals(orgTransId)) {
				transactionDto.setOrgTransId(Long.valueOf(orgTransId));
			}
			transactionDto.setDbtrBrnl(dataObj.getDataVal("dbtrBrn"));
			transactionDto.setTrnRefNo(dataObj.getDataVal("trnRefNo"));
			transactionDto.setTransType(dataObj.getDataVal("transType"));
			transactionDto.setTransCate(dataObj.getDataVal("transCate"));
			transactionDto.setChannelId(dataObj.getDataVal("channelId"));
			transactionDto.setXrefId(dataObj.getDataVal("xrefId"));
			transactionDto.setDbtrAcctNo(dataObj.getDataVal("dbtrAcctNo"));
			transactionDto.setCdtrAcctNo(dataObj.getDataVal("cdtrAcctNo"));
			transactionDto.setAmount(dataObj.getDataVal("amount"));
			transactionDto.setCcy(dataObj.getDataVal("ccy"));
			transactionDto.setCreatedOn(dataObj.getDataVal("createdOn"));
			transactionDto.setTrnRefNo(dataObj.getDataVal("orgSenderRefId"));
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("Exception when handle checkTransExist:" + e.getMessage());
			return null;
		}
		return transactionDto;
	}

	private boolean handleUpdateTrans(Long transId, String errorCode, String errorDesc) {
		try {
			DataObj dataObj3 = null;
			dataObj3 = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH,
					errorCode);

			DataObj dataObj4 = null;
			dataObj4 = transactionRepository.updateTransStatusUpdated(transId, dataObj3.getEcode(), dataObj3.getEdesc());
			logger.debug("After call updateTransStatus, errCode: " + dataObj4.getEcode() + ", errDesc: " + dataObj4.getEdesc() + ", transId: "
					+ transId);
			if (!AppConstant.SystemResponse.SUCCESS_CODE.equals(dataObj4.getEcode())) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception when handle handleUpdateTrans:" + e.getMessage());
		}
		return false;
	}

//	@Override
//	public DataObj responsePayment(ManualResponsePaymentDto resPayDto) {
//		logger.info("===== Begin Process ResponsePayent =====");
//		String transId = "", eCode = "", eDesc = "";
//		Transaction trans = new Transaction();
//		TransAchDetail transAchDetail = new TransAchDetail();
//		DataObj objRes = new DataObj();
//		try {
//			//// 1.init transaction
//
//			ACHUtil.parseMsg2Obj(resPayDto, trans, transAchDetail);
//			DataObj initRes = transactionRepository.initTrans(trans, transAchDetail);
//			eCode = initRes.getEcode();
//			eDesc = initRes.getEdesc();
//			if (!AppConstant.SystemResponse.SUCCESS_CODE.equalsIgnoreCase(eCode)) {
//				return initRes;
//			}
//			transId = String.valueOf(trans.getTransId());
//			resPayDto.setTransId(transId);
//			// luu log msg
//			producer.pushMsgLogReq(transId, ACHUtil.parseObjectToString(resPayDto), resPayDto.getChannelId(),
//					AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);
//			////// Lay thong tin giao dich goc
//			DataObj objInfo = transactionRepository.getTransInfoByOrgSenderRef(resPayDto.getOrgSenderRefId());
//			eCode = objInfo.getEcode();
//			if (!AppConstant.SystemResponse.SUCCESS_CODE.equalsIgnoreCase(eCode)) {
//				producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(objInfo), resPayDto.getChannelId(),
//						AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);
//				return objInfo;
//			}
//
//			if (AppConstant.SystemResponse.TIMEOUT_ERROR_CODE.equals(resPayDto.getResCode())) {
//				objRes = new DataObj("96", "Trang thai yeu cau khong thoa man", null);
//				producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(objRes), resPayDto.getChannelId(),
//						AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);
//				return objRes;
//			}
//			/*
//			String transEcode = objInfo.getDataVal("transStat");
//			String transEdesc = objInfo.getDataVal("transStatDesc");
//			*/
//			String jsonContent = objInfo.getDataVal("msgContent");
//			String isTransCopy = objInfo.getDataVal("isTransCopy");
//			String orgTransId = objInfo.getDataVal("transId");
//
//			String errorNPCode = "", errorNPDesc = "";
//			if (!AppConstant.SystemResponse.SUCCESS_CODE.equals(resPayDto.getResCode())) {
//				errorNPCode = "RJCT";
//				errorNPDesc = "Request is reject due to any reason";
//			} else {
//				errorNPCode = resPayDto.getResCode();
//				errorNPDesc = resPayDto.getResDesc();
//			}
//
//			String senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.PACS002,
//					AppConstant.PacsCommonConfig.SENDER_ID, AppConstant.SenderRefType.SENDER_REF_NORMAL, null);
//			String jsonRequest = "";
//			if ("1".equals(isTransCopy)) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				JsonNode jsonNodeContent = objectMapper.readTree(jsonContent);
//				String messageIdentifierCopy = JsonUtil.getVal(jsonNodeContent, "/Payload/Document/Dplct/Dplct/Tp").asText();
//				JsonNode jsonPacs = JsonUtil.getVal(jsonNodeContent, "/Payload/Document/Dplct/Dplct/Data/Any");
//				if (AppConstant.MsgIdr.PACS008.equals(messageIdentifierCopy)) {
//					jsonRequest = MsgBuilder.buildPacs002ForPacs008InCamt034(ACHUtil.parseObjectToString(jsonPacs), transId,
//							senderRefId, resPayDto.getResCode(), resPayDto.getResDesc(), errorNPCode, errorNPDesc);
//				} else if (AppConstant.MsgIdr.PACS004.equals(messageIdentifierCopy)){
//					jsonRequest = MsgBuilder.buildPacs002ForPacs004InCamt034(ACHUtil.parseObjectToString(jsonPacs), transId,
//							senderRefId, resPayDto.getResCode(), resPayDto.getResDesc(), errorNPCode, errorNPDesc);
//				} else {
//					jsonRequest = MsgBuilder.buildPacs002ForPayCopy(ACHUtil.parseObjectToString(jsonPacs), transId,
//							senderRefId, resPayDto.getResCode(), resPayDto.getResDesc(), errorNPCode, errorNPDesc);
//				}
//			} else {
//				jsonRequest = MsgBuilder.buildPacs002(jsonContent, transId, senderRefId, resPayDto.getResCode(),
//						resPayDto.getResDesc(), errorNPCode, errorNPDesc);
//			}
//			/// insert log call napas va trans activity request
//			producer.pushMsgLogReq(transId, jsonRequest, AppConstant.LogConfig.BANK,
//					AppConstant.LogConfig.NAPAS, AppConstant.LogConfig.CATEGORY_NAPAS);
//
//			//Push activity request of send to napas
//			transAchActivityRepository.pushActivity(Long.parseLong(transId), senderRefId, AppConstant.MsgIdr.PACS002,
//					"Send pacs002 to Napas", AppConstant.LogConfig.REQUEST, jsonRequest, new Date(),
//					AppConstant.TransStep.ACT_STEP_PUTMX, AppConstant.SystemResponse.SUCCESS_CODE,
//					AppConstant.SystemResponse.SUCCESS_DESC);
//
//			//Call napas
//			RestDataObj restData = NapasCaller.send2Napas(jsonRequest, AppConstant.MsgIdr.PACS002, senderRefId, AppConstant.ACHService.DIRECT_CREDIT);
//			objRes = handleRespPaymentNPResp(restData);
//
//			//hard code ecode
////			objRes.setEcode("01");
////			objRes.setEdesc("Gui lenh sang Napas thanh cong");
//			//end hard code
//			/// insert log call napas va tran activity response
//			producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(restData),
//					AppConstant.LogConfig.BANK,
//					AppConstant.LogConfig.NAPAS,
//					AppConstant.LogConfig.CATEGORY_NAPAS);
//			//
//			//Push activity response of receive from napas
//			TransAchActivity transAchActivity = new TransAchActivity();
//			transAchActivity.setTransId(Long.parseLong(transId));
//			transAchActivity.setActivityDesc("Receiver from Napas");
//			transAchActivity.setActivityStep(AppConstant.TransStep.ACT_STEP_PUTMX);
//			transAchActivity.setMsgContent(ACHUtil.parseObjectToString(restData));
//			transAchActivity.setMsgIdentifier(AppConstant.MsgIdr.PACS002);
//			transAchActivity.setMsgType(AppConstant.LogConfig.RESPONSE);
//			transAchActivity.setSenderRefId(senderRefId);
//			transAchActivity.setCreatedOn(new Date());
//			transAchActivity.setMsgDt(new Date());
//			transAchActivity.setErrCode(objRes.getEcode());
//			transAchActivity.setErrDesc(objRes.getEdesc());
//
//			transAchDetail.setMsgIdentifier(AppConstant.MsgIdr.PACS002);
//			transAchDetail.setSenderRefId(senderRefId);
//			transAchDetail.setTxId(senderRefId);
//			transAchDetail.setTransStep(AppConstant.TransStep.ACT_STEP_PUTMX);
//			transAchDetail.setErrDesc(objRes.getEdesc());
//			transAchDetail.setErrCode(objRes.getEcode());
//
//			DataObj dataObjLogHandle = transactionRepository.handleAchDetailActivity(transAchDetail, transAchActivity);
//			String outEcode = dataObjLogHandle.getEcode();
//			String outEdesc = dataObjLogHandle.getEdesc();
//			// udpate trans goc
//			if (!DataUtil.isNullOrEmpty(orgTransId)) {
//				transactionRepository.updateTransStatus(Long.parseLong(orgTransId), resPayDto.getResCode(),
//						resPayDto.getResDesc());
//				transAchDetail = new TransAchDetail();
//				transAchDetail.setTransId(Long.parseLong(orgTransId));
//
//				transAchDetail.setErrDesc(resPayDto.getResDesc());
//				if (AppConstant.SystemResponse.SUCCESS_CODE.equals(resPayDto.getResCode())) {
//					transAchDetail.setGroupStatus(AppConstant.AchEcode.ECODE_ACSP_AUTH_ACSP);
//					transAchDetail.setErrCode(AppConstant.AchEcode.ECODE_ACSP_AUTH_ACSP);
//				} else if (AppConstant.SystemResponse.TIMEOUT_ERROR_CODE.equals(resPayDto.getResCode())) {
//					transAchDetail.setGroupStatus(AppConstant.AchEcode.ECODE_ACSP_NOAN_ACSP);
//					transAchDetail.setErrCode(AppConstant.AchEcode.ECODE_ACSP_NOAN_ACSP);
//				} else {
//					transAchDetail.setGroupStatus(AppConstant.AchEcode.ECODE_ACSP_NAUT_ACSP);
//					transAchDetail.setErrCode(AppConstant.AchEcode.ECODE_ACSP_NAUT_ACSP);
//				}
//				transactionRepository.updateTransAchDetail(transAchDetail);
//			}
//		} catch (Exception e) {
//			logger.error("Exception: " + e.getMessage());
//			eCode = AppConstant.SystemResponse.EXCEPRION_ERROR_CODE;
//			eDesc = AppConstant.SystemResponse.EXCEPRION_ERROR_DESC;
//			objRes.setEcode(eCode);
//			objRes.setEdesc(eDesc);
//		} finally {
//			if (!DataUtil.isNullOrEmpty(trans.getTransId().toString()))
//				transactionRepository.updateTransStatus(trans.getTransId(), objRes.getEcode(), objRes.getEdesc());
//		}
//		// log msg
//		producer.pushMsgLogRes(transId, ACHUtil.parseObjectToString(objRes), resPayDto.getChannelId(),
//				AppConstant.LogConfig.BANK, AppConstant.LogConfig.CATEGORY_INTERNAL);
//		return objRes;
//	}
//
//	@Override
//	public Page<Transaction> selectTranByServiceGroupAndServiceId(GetTranDto getTranDto) {
//		String serviceGroupId = getTranDto.getServiceGroupId();
//		String serviceId = getTranDto.getServiceId();
//		String dtFrom = getTranDto.getTransDtFrom();
//		String dtTo = getTranDto.getTransDtTo();
//
//		PageRequest pageable = PageRequest.of(getTranDto.getPage(), getTranDto.getSize(), Sort.by(Sort.Direction.DESC, "trans_id"));
//
//		return transactionRepository.selectTranByServiceGroupAndServiceId(serviceGroupId, serviceId, dtFrom, dtTo, pageable);
//	}
//
//	private DataObj handleRespPaymentNPResp(RestDataObj restDataObj) {
//		DataObj dataObj = new DataObj();
//		try {
//			String partnerCode = AppConstant.AchEcode.ECODE_UNKONW;
//			if (restDataObj != null && restDataObj.getHttpStatus() != null && restDataObj.getResponse() != null) {
//				if (AppConstant.HTTPConfig.HTTP_STATUS_200.equals(restDataObj.getHttpStatus())) {
//					NPResponse npResponse = JsonUtil.parseJson2NPResponse(restDataObj.getResponse());
//					partnerCode = restDataObj.getHttpStatus().toUpperCase() + "_" + (StringUtils.isEmpty(npResponse.getType()) ? "NULL" : npResponse.getType().toUpperCase())
//							+ "_" + (StringUtils.isEmpty(npResponse.getDuplicated()) ? "NULL" : npResponse.getDuplicated().toUpperCase());
//				} else if ("".equals(restDataObj.getHttpStatus())) {
//					partnerCode = AppConstant.HTTPConfig.HTTP_STATUS_5XX;
//				} else {
//					partnerCode = restDataObj.getHttpStatus();
//				}
//			} else {
//				partnerCode = AppConstant.AchEcode.ECODE_SYSTEM_ERROR;
//			}
//			dataObj = transactionRepository.mapErrorCode(AppConstant.Common.ORG_NAPAS, AppConstant.ChannelId.ACH, partnerCode);
//		} catch (Exception e) {
//			logger.error("Exception when handle handleRespPaymentNPResp:" + e.getMessage());
//			dataObj.setEcode(AppConstant.SystemResponse.SYSTEM_ERROR_CODE);
//			dataObj.setEdesc(AppConstant.SystemResponse.SYSTEM_ERROR_DESC);
//		}
//		return dataObj;
//	}
}
