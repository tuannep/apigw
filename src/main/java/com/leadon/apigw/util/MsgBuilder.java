package com.leadon.apigw.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.constant.MsgConstant;
import com.leadon.apigw.model.TransAchDetail;
import com.leadon.apigw.model.Transaction;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

public class MsgBuilder {

	public static Logger logger = LoggerFactory.getLogger(MsgBuilder.class);

	private static final ObjectMapper mapper = new ObjectMapper();

	private static JsonNode PACS008JSON = null;
	private static JsonNode PACS002SUCCESJSON = null;
	private static JsonNode PACS002FAILJSON = null;
	private static JsonNode PACS028JSON = null;
	private static JsonNode PACS004JSON = null;
	private static JsonNode CAMT033JSON = null;
	private static JsonNode CAMT998DISPUTEJSON = null;
	private static JsonNode CAMT009JSON = null;
	private static JsonNode CAMT011JSON = null;
	private static JsonNode DASJSON = null;
	private static JsonNode ISO8583JSON = null;
	// Load json templates
	static {
		try {
			PACS008JSON = mapper
					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_PACS008));
//			PACS002FAILJSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_PACS002_FAIL));
//			PACS002SUCCESJSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_PACS002_SUCCESS));
//			PACS028JSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_PACS028));
//			PACS004JSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_PACS004));
//			CAMT033JSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_CAMT033));
//			CAMT998DISPUTEJSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_CAMT998_DISP));
//			CAMT009JSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_CAMT009));
//			CAMT011JSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_CAMT011));
//			DASJSON = mapper.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_DAS));
//			ISO8583JSON = mapper
//					.readTree(ResourceUtils.getFile("classpath:" + AppConstant.JsonConfig.JSON_TEMP_ISO8583));
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static String buildPacs008(JsonNode rootIso8583, Transaction transaction, TransAchDetail transAchDetail) {
		JsonNode root = PACS008JSON.deepCopy();
		try {
			Date currDt = new Date();

			DateUtil dateUtil = new DateUtil();
			String currDt2 = dateUtil.formatTimeStampXXX(dateUtil
					.parseTimestampyyyyMMddHHmmss(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE013_LOC_TRN_DATE").asText()
					+ JsonUtil.getVal(rootIso8583, "/body/iso8583/DE012_LOC_TRN_TIME").asText()));

			String transId = String.valueOf(transaction.getTransId());
			// InstrId
			String pmtIdInstrId = "";
			String pmtIdInstrIdPrefix1 = "";
			String pmtIdInstrIdPrefix2 = "";
			String pmtIdInstrIdPrefix3 = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE013_LOC_TRN_DATE").asText();
			String pmtIdInstrIdPrefix4 = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE012_LOC_TRN_TIME").asText();
			String pmtIdInstrIdPrefix5 = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE037_REL_REF_NO").asText();

			pmtIdInstrIdPrefix1 = transaction.getTransCate();
			pmtIdInstrIdPrefix2 = transAchDetail.getDbtrMemId();
			pmtIdInstrId = pmtIdInstrIdPrefix1 + pmtIdInstrIdPrefix2 + pmtIdInstrIdPrefix3 + pmtIdInstrIdPrefix4 + pmtIdInstrIdPrefix5;
			transAchDetail.setInstrId(pmtIdInstrId);

			// EndToEndId
			String pmtIdEndToEndId = "";
			String pmtIdEndToEndIdPrefix1 = "";
			String pmtIdEndToEndIdPrefix2 = "";
			String pmtIdEndToEndIdPrefix3 = "";
			String pmtIdEndToEndIdPrefix4 = "";

			// Xu ly ngay
			Calendar cal = Calendar.getInstance();
			Date currentTime = cal.getTime();
			Calendar calLimit = Calendar.getInstance();
			calLimit.set(Calendar.HOUR_OF_DAY, 23);
			calLimit.set(Calendar.MINUTE, 00);
			calLimit.set(Calendar.SECOND, 00);
			Date dateTimeLimit = calLimit.getTime();

			if (currentTime.equals(dateTimeLimit) || currentTime.before(dateTimeLimit)) {
				pmtIdEndToEndIdPrefix1 = DateUtil.formatMMdd(dateTimeLimit);
			} else {
				calLimit.add(Calendar.DATE, 1);
				Date setlDate = calLimit.getTime();
				pmtIdEndToEndIdPrefix1 = DateUtil.formatMMdd(setlDate);
			}

			pmtIdEndToEndIdPrefix2 = transaction.getChannelId();
			pmtIdEndToEndIdPrefix3 = "IF_DEP";
			pmtIdEndToEndIdPrefix4 = ACHUtil.subStringbyIndex(transId, 16);
			pmtIdEndToEndId = pmtIdEndToEndIdPrefix1 + pmtIdEndToEndIdPrefix2 + pmtIdEndToEndIdPrefix3
					+ pmtIdEndToEndIdPrefix4;
			transAchDetail.setEndtoendId(pmtIdEndToEndId);

			String TAM = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE004_TRN_AMT").asText();
			String TDT = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE007_TRN_DT").asText();
			String SCR = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE009_STL_CONV_RT").asText();
			String MCC = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE018_MER_CAT_CD").asText())) ? "7399"
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE018_MER_CAT_CD").asText();
			String AIC = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE019_ACQ_CTRY_CD").asText())) ? "704"
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE019_ACQ_CTRY_CD").asText();
			String PEM = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE022_POS_MODE").asText())) ? "000"
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE022_POS_MODE").asText();
			String PCD = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE025_POS_COND_CD").asText())) ? "00"
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE025_POS_COND_CD").asText();
			String FID = "980471";

			String subTransId = ACHUtil.subStringbyIndex(transId, 15);
			String MID = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE042_CRD_ACPT_ID").asText()))
					? subTransId
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE042_CRD_ACPT_ID").asText();
			String MNM = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE043_CRD_ACPT_LOC").asText()))
					? "NGAN HANG              HANOI         704"
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE043_CRD_ACPT_LOC").asText();
			String SCC = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE049_TRN_CCY").asText())) ? "704"
					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE049_TRN_CCY").asText();

			String MAC = ACHUtil.subStringbyIndex(transAchDetail.getSenderRefId(), 16);
			String transDesc = transaction.getTransDesc();
			if (StringUtils.isEmpty(transDesc))
				transDesc = AppConstant.MsgDesc.DESC_CONTENT_DEFAULT;

			String transDescSub1 = "";
			String transDescSub2 = "";
			if (transDesc.length() > 120) {
				transDescSub1 = transDesc.substring(0, 120);
				transDescSub2 = transDesc.substring(120, transDesc.length());
			} else {
				transDescSub1 = transDesc;
			}

			String info_1 = "/TAM/" + TAM + "/TDT/" + TDT + "/SCR/" + SCR + "/MCC/" + MCC + "/AIC/" + AIC + "/PEM/"
					+ PEM + "/PCD/" + PCD + "/FID/" + FID + "/MID/" + MID;
			String info_2 = "/MNM/" + MNM + "/SCC/" + SCC + "/BID/" + transAchDetail.getCdtrMemId() + "/FAI/"
					+ transAchDetail.getDbtrAcctNo() + "/TAI/" + transAchDetail.getCdtrAcctNo();
			String info_3 = "/CTR/" + transDescSub1;
			String info_4 = transDescSub2 + "/MAC/" + MAC;

			// Build pacs008
			String creDt = dateUtil.formatTimeStampZ(dateUtil
					.parseTimestampyyyyMMddHHmmss(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE007_TRN_DT").asText()));
//			String creDt = checkGetCredt(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE007_TRN_DT").asText());
			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS008, transAchDetail.getSenderRefId(), creDt);
			logger.info("~~~~~~creDt:" + creDt);

			// Document/FIToFICstmrCdtTrf/GrpHdr
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId", transAchDetail.getSenderRefId());
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm", currDt2);
//					DateUtil.formatTimeStampXXX(currDt));
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Ccy",
					transAchDetail.getCcy());
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Value",
					ACHUtil.formatAmount(String.valueOf(transAchDetail.getAmount())));
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt",
					DateUtil.formatDateYMD(currDt));
			transAchDetail.setSettleDt(DateUtil.FORMAT_DATE_YMD.parse(DateUtil.formatDateYMD(currDt)));
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/SttlmInf/SttlmMtd",
					AppConstant.PacsCommonConfig.PAYLOAD_STTLM);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/NbOfTxs",
					String.valueOf(transAchDetail.getNumberOfTxs()));

			// Document/FIToFICstmrCdtTrf/CdtTrfTxInf/PmtId
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/InstrId", pmtIdInstrId, 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/EndToEndId", pmtIdEndToEndId,
					1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/TxId",
					transAchDetail.getSenderRefId(), 1);

			// Document/FIToFICstmrCdtTrf/CdtTrfTxInf/PmtTpInf
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtTpInf/ClrChanl",
					AppConstant.PacsCommonConfig.PAYLOAD_CLRCHANL, 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtTpInf/SvcLvl/Prtry",
					AppConstant.PacsCommonConfig.PAYLOAD_SVCLVL, 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtTpInf/LclInstrm/Prtry",
					AppConstant.PacsCommonConfig.PAYLOAD_LCLINSTRM, 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtTpInf/CtgyPurp/Prtry",
					AppConstant.PacsCommonConfig.PAYLOAD_CTGYPURP, 1);

			// Document/FIToFICstmrCdtTrf/CdtTrfTxInf/IntrBkSttlmAmt
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/IntrBkSttlmAmt/Ccy",
					transAchDetail.getCcy(), 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/IntrBkSttlmAmt/Value",
					ACHUtil.formatAmount(String.valueOf(transAchDetail.getAmount())), 1);

			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/ChrgBr",
					AppConstant.PacsCommonConfig.PAYLOAD_CHRGBR, 1);

			JsonUtil.setVal(root,
					"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
					transAchDetail.getDbtrMemCode(), 1);
			JsonUtil.setVal(root,
					"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
					transAchDetail.getCdtrMemCode(), 1);

			// Document/FIToFICstmrCdtTrf/CdtTrfTxInf/Dbtr
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/Nm",
					transAchDetail.getDbtrName(), 1);

			String dbtrOfAddress = transAchDetail.getDbtrAddress();
			String Address1 = AppConstant.Common.DEFAULT;
			String Address2 = AppConstant.Common.DEFAULT;
			String Address3 = AppConstant.Common.DEFAULT;
			int lengthAddress = dbtrOfAddress.length();

			if (0 < lengthAddress && lengthAddress <= 70) {
				Address1 = dbtrOfAddress.substring(0, lengthAddress);
			} else if (70 < lengthAddress && lengthAddress <= 140) {
				Address1 = dbtrOfAddress.substring(0, 70);
				Address2 = dbtrOfAddress.substring(70, lengthAddress);
			} else if (140 < lengthAddress) {
				Address1 = dbtrOfAddress.substring(0, 70);
				Address2 = dbtrOfAddress.substring(70, 140);
				if (210 <= lengthAddress)
					Address3 = dbtrOfAddress.substring(140, 210);
				else
					Address3 = dbtrOfAddress.substring(140, lengthAddress);
			}

			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/0",
					Address1, 0);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/1",
					Address2, 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/2",
					Address3, 2);

			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id",
					transAchDetail.getDbtrAcctNo(), 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Tp/Prtry",
					transAchDetail.getDbtrAcctType(), 1);
			JsonUtil.setVal(root,
					"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId",
					transAchDetail.getDbtrMemCode(), 1);

			// Document/FIToFICstmrCdtTrf/CdtTrfTxInf/Cdtr
			String nameCdtr = transAchDetail.getCdtrName();
			if (StringUtils.isNotEmpty(nameCdtr))
				JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/Nm",
					transAchDetail.getCdtrName(), 1);
			else
				JsonUtil.remove(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/Nm");

			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/0",
					AppConstant.Common.DEFAULT, 0);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/1",
					AppConstant.Common.DEFAULT, 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/2",
					AppConstant.Common.DEFAULT, 2);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Id/Othr/Id",
					transAchDetail.getCdtrAcctNo(), 1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAcct/Tp/Prtry",
					transAchDetail.getCdtrAcctType(), 1);
			JsonUtil.setVal(root,
					"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId",
					transAchDetail.getCdtrMemCode(), 1);

			// Document/FIToFICstmrCdtTrf/CdtTrfTxInf/InstrForNxtAgt
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/0/InstrInf", info_1,
					1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/1/InstrInf", info_2,
					1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/2/InstrInf", info_3,
					1);
			JsonUtil.setVal(root, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/3/InstrInf", info_4,
					1);

			String payload = JsonUtil.getVal(root, "/Payload").toString();
			//TODO
			// String signature = Crypto.createSign("RSA2048", payload, null);
			String signature = "";
			// "D:\\\\RSA Key
			// live\\\\Bank_PrivateKey.cer");
//			String signature = "";
			String timestamp = DateUtil.formatTimeStampXXX(currDt);
			buildMsgHeader(root, transAchDetail.getSenderRefId(), AppConstant.MsgIdr.PACS008, timestamp, signature);
		} catch (Exception e) {
			logger.error("Exception when handle buildPacs008:" + e.getMessage());
			return "";
		}
		return root.toPrettyString();
	}

//	public static String buildPacs004(ReturnPaymentDto returnPayDto, String transId, String senderRefId,
//			String jsonPacs008, String isTransCopy, String errorCode, String errorDesc, String caseId) {
//		JsonNode root;
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs008 = objectMapper.readTree(jsonPacs008);
//
//			// convert address line for dbtr and cdrt
//			String dbtrAddress1 = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/0").asText();
//			String dbtrAddress2 = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/1").asText();
//			String dbtrAddress3 = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/2").asText();
//
//			String cdtrAddress1 = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/0").asText();
//			String cdtrAddress2 = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/1").asText();
//			String cdtrAddress3 = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/2").asText();
//
//			root = getPacs004JsonNode(dbtrAddress1, dbtrAddress2, dbtrAddress3, cdtrAddress1, cdtrAddress2, cdtrAddress3);
//
//			String TtlIntrBkSttlmAmt_ccy = "", TtlIntrBkSttlmAmt_value = "", InstdAgt_MmbId = "", OrgnlMsgId = "",
//					OrgnlCreDtTm = "";
//
//			if ("1".equals(isTransCopy)) {
//				OrgnlMsgId = JsonUtil
//						.getVal(rootPacs008,
//								"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId")
//						.asText();
//				OrgnlCreDtTm = JsonUtil
//						.getVal(rootPacs008,
//								"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm")
//						.asText();
//
//				JsonNode paymentNode = JsonUtil.getVal(rootPacs008,
//						"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//				if (paymentNode.isArray()) {
//					for (JsonNode jsonElement : paymentNode) {
//
//						TtlIntrBkSttlmAmt_ccy = JsonUtil.getVal(jsonElement, "/IntrBkSttlmAmt/Ccy").asText();
//						TtlIntrBkSttlmAmt_value = returnPayDto.getAmount();
//						if (DataUtil.isNullOrEmpty(TtlIntrBkSttlmAmt_value)) {
//							TtlIntrBkSttlmAmt_value = JsonUtil.getVal(jsonElement, "/IntrBkSttlmAmt/Value").asText();
//						}
//						InstdAgt_MmbId = JsonUtil.getVal(jsonElement, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId")
//								.asText();
//					}
//				}
//
//			} else {
//				OrgnlMsgId = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId").asText();
//				OrgnlCreDtTm = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm")
//						.asText();
//				JsonNode paymentNode = JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//				if (paymentNode.isArray()) {
//					for (JsonNode jsonElement : paymentNode) {
//						TtlIntrBkSttlmAmt_ccy = JsonUtil.getVal(jsonElement, "/IntrBkSttlmAmt/Ccy").asText();
//						TtlIntrBkSttlmAmt_value = returnPayDto.getAmount();
//						if (DataUtil.isNullOrEmpty(TtlIntrBkSttlmAmt_value)) {
//							TtlIntrBkSttlmAmt_value = JsonUtil.getVal(jsonElement, "/IntrBkSttlmAmt/Value").asText();
//						}
//						InstdAgt_MmbId = JsonUtil.getVal(jsonElement, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId")
//								.asText();
//					}
//				}
//			}
//			Date currDt = new Date();
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS004, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/CreDtTm", DateUtil.formatTimeStampXXX(currDt));
//
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/TtlRtrdIntrBkSttlmAmt/Ccy", TtlIntrBkSttlmAmt_ccy);
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/TtlRtrdIntrBkSttlmAmt/Value",
//					ACHUtil.formatAmount(TtlIntrBkSttlmAmt_value));
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/IntrBkSttlmDt", DateUtil.formatDateYMD(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/SttlmInf/SttlmMtd",
//					AppConstant.PacsCommonConfig.PAYLOAD_STTLM);
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/GrpHdr/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
//					InstdAgt_MmbId);
//
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/OrgnlGrpInf/OrgnlMsgId", OrgnlMsgId);
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/OrgnlGrpInf/OrgnlMsgNmId", AppConstant.MsgIdr.PACS008);
//			JsonUtil.setVal(root, "/Payload/Document/PmtRtr/OrgnlGrpInf/OrgnlCreDtTm", OrgnlCreDtTm);
//
//			if ("1".equals(isTransCopy)) {
//				buildDetailNRTReturn(root,
//						JsonUtil.getVal(rootPacs008,
//								"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf"),
//						returnPayDto, errorCode, errorDesc, null);
//			} else {
//				buildDetailNRTReturn(root, JsonUtil.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf"),
//						returnPayDto, errorCode, errorDesc, caseId);
//			}
//
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//            String signature = Crypto.createSign("RSA2048", payload, null);
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.PACS004, timestamp, signature);
//		} catch (Exception e) {
//			logger.error("Exception when handle buildPacs004:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}

	/**
	 * convert addrLine for dbtr and cdtr
	 * @param dbtrAddress1
	 * @param dbtrAddress2
	 * @param dbtrAddress3
	 * @param cdtrAddress1
	 * @param cdtrAddress2
	 * @param cdtrAddress3
	 * @return
	 */
	private static JsonNode getPacs004JsonNode(String dbtrAddress1, String dbtrAddress2, String dbtrAddress3, String cdtrAddress1, String cdtrAddress2, String cdtrAddress3) {
		try {
			if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isNotEmpty(dbtrAddress2) && StringUtils.isNotEmpty(dbtrAddress3)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isNotEmpty(cdtrAddress2) && StringUtils.isNotEmpty(cdtrAddress3)) { // dbtr - 3, cdtr - 3
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isEmpty(dbtrAddress2)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isEmpty(cdtrAddress2)) { // dbtr - 1, cdtr - 1
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine1
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine1
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isEmpty(dbtrAddress2)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isNotEmpty(cdtrAddress2) && StringUtils.isEmpty(cdtrAddress3)) { // dbtr - 1, cdtr - 2
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine1
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine2
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isEmpty(dbtrAddress2)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isNotEmpty(cdtrAddress2) && StringUtils.isNotEmpty(cdtrAddress3)) { // dbtr - 1, cdtr - 3
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine1
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isNotEmpty(dbtrAddress2) && StringUtils.isEmpty(dbtrAddress3)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isEmpty(cdtrAddress2)) { // dbtr - 2, cdtr - 1
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine2
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine1
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isNotEmpty(dbtrAddress2) && StringUtils.isEmpty(dbtrAddress3)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isNotEmpty(cdtrAddress2) && StringUtils.isEmpty(cdtrAddress3)) { // dbtr - 2, cdtr - 2
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine2
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine2
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isNotEmpty(dbtrAddress2) && StringUtils.isEmpty(dbtrAddress3)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isNotEmpty(cdtrAddress2) && StringUtils.isNotEmpty(cdtrAddress3)) { // dbtr - 2, cdtr - 3
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine2
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isNotEmpty(dbtrAddress2) && StringUtils.isNotEmpty(dbtrAddress3)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isEmpty(cdtrAddress2)) { // dbtr - 3, cdtr - 1
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine1
								+ MsgConstant.message.pacs004_3);
			} else if (StringUtils.isNotEmpty(dbtrAddress1) && StringUtils.isNotEmpty(dbtrAddress2) && StringUtils.isNotEmpty(dbtrAddress3)
					&& StringUtils.isNotEmpty(cdtrAddress1) && StringUtils.isNotEmpty(cdtrAddress2) && StringUtils.isEmpty(cdtrAddress3)) { // dbtr - 3, cdtr - 2
				PACS004JSON = mapper
						.readTree(MsgConstant.message.pacs004_1 + MsgConstant.subJson.AdrLine
								+ MsgConstant.message.pacs004_2 + MsgConstant.subJson.AdrLine2
								+ MsgConstant.message.pacs004_3);
			} else {
				return PACS004JSON.deepCopy();
			}
		} catch (Exception e) {
			logger.error("getPacs004JsonNode exceprion: " + e.toString());
		}

		return PACS004JSON;
	}

//	private static void buildDetailNRTReturn(JsonNode jsonNode, JsonNode pacs008Node, ReturnPaymentDto returnPayDto,
//			String errorCode, String errorDesc, String caseId) {
//		String rtnAmt = returnPayDto.getAmount();
//		JsonNode jsonReturn = pacs008Node.get("CdtTrfTxInf");
//		if (jsonReturn.isArray()) {
//			for (JsonNode jsonReturnEle : jsonReturn) {
//
//				if (DataUtil.isNullOrEmpty(rtnAmt)) {
//					rtnAmt = JsonUtil.getVal(jsonReturnEle, "/IntrBkSttlmAmt/Value").asText();
//				}
//				rtnAmt = ACHUtil.formatAmount(rtnAmt);
//
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/GrpHdr/NbOfTxs", "1");
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/IntrBkSttlmDt",
//						JsonUtil.getVal(pacs008Node, "/GrpHdr/IntrBkSttlmDt").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlIntrBkSttlmDt",
//						JsonUtil.getVal(pacs008Node, "/GrpHdr/IntrBkSttlmDt").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrId", returnPayDto.getSenderRefId());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Value", rtnAmt);
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/IntrBkSttlmDt",
//						DateUtil.formatDateYMD(new Date()));
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/ChrgBr",
//						AppConstant.PacsCommonConfig.PAYLOAD_CHRGBR);
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrRsnInf/0/Orgtr/Id/OrgId/Othr/Id",
//						AppConstant.PacsCommonConfig.SENDER_CODE);
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrRsnInf/0/Rsn/Prtry", errorCode);
//				if (!StringUtils.isEmpty(caseId)) {
//					JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrRsnInf/0/AddtlInf/0", "/DSPTCS/" + caseId, 0);
//				} else {
//					JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrRsnInf/0/AddtlInf/0", errorDesc, 0);
//				}
////				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/IntrBkSttlmAmt/Value", rtnAmt);
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/IntrBkSttlmAmt/Value",
//						JsonUtil.getVal(pacs008Node, "/CdtTrfTxInf/0/IntrBkSttlmAmt/Value").asText());
//
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlInstrId",
//						JsonUtil.getVal(jsonReturnEle, "/PmtId/InstrId").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlEndToEndId",
//						JsonUtil.getVal(jsonReturnEle, "/PmtId/EndToEndId").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxId",
//						JsonUtil.getVal(jsonReturnEle, "/PmtId/TxId").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Ccy",
//						JsonUtil.getVal(jsonReturnEle, "/IntrBkSttlmAmt/Ccy").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//						JsonUtil.getVal(jsonReturnEle, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/IntrBkSttlmAmt/Ccy",
//						JsonUtil.getVal(jsonReturnEle, "/IntrBkSttlmAmt/Ccy").asText());
//
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/PmtTpInf/ClrChanl",
//						JsonUtil.getVal(jsonReturnEle, "/PmtTpInf/ClrChanl").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry",
//						JsonUtil.getVal(jsonReturnEle, "/PmtTpInf/SvcLvl/Prtry").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/PmtTpInf/LclInstrm/Prtry",
//						JsonUtil.getVal(jsonReturnEle, "/PmtTpInf/LclInstrm/Prtry").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/PmtTpInf/CtgyPurp/Prtry",
//						JsonUtil.getVal(jsonReturnEle, "/PmtTpInf/CtgyPurp/Prtry").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Dbtr/Pty/Nm",
//						JsonUtil.getVal(jsonReturnEle, "/Dbtr/Nm").asText());
//
//				/// build note
//				JsonNode jsonOrgDBAddress = JsonUtil.getVal(jsonReturnEle, "/Dbtr/PstlAdr/AdrLine");
//				//Convert address line for Dbtr
//				convertAdrLine(jsonNode, jsonOrgDBAddress, "Dbtr");
//
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id",
//						JsonUtil.getVal(jsonReturnEle, "/DbtrAcct/Id/Othr/Id").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Tp/Prtry",
//						DataUtil.isNullObject(JsonUtil.getVal(jsonReturnEle, "/DbtrAcct/Tp/Prtry")) ? ""
//								: JsonUtil.getVal(jsonReturnEle, "/DbtrAcct/Tp/Prtry").asText());
//				JsonUtil.setVal(jsonNode,
//						"/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId",
//						JsonUtil.getVal(jsonReturnEle, "/DbtrAgt/FinInstnId/ClrSysMmbId/MmbId").asText());
//				JsonUtil.setVal(jsonNode,
//						"/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId",
//						JsonUtil.getVal(jsonReturnEle, "/CdtrAgt/FinInstnId/ClrSysMmbId/MmbId").asText());
//
//				String Nm = JsonUtil.getVal(jsonReturnEle, "/Cdtr/Nm").asText();
//				if (StringUtils.isEmpty(Nm))
//					JsonUtil.remove(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/Nm");
//				else
//					JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/Cdtr/Pty/Nm", Nm);
//				// add end
//				JsonNode jsonOrgCRAddress = JsonUtil.getVal(jsonReturnEle, "/Cdtr/PstlAdr/AdrLine");
//				//Convert address line for Cdtr
//				convertAdrLine(jsonNode, jsonOrgCRAddress, "Cdtr");
//
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Id/Othr/Id",
//						JsonUtil.getVal(jsonReturnEle, "/CdtrAcct/Id/Othr/Id").asText());
//				JsonUtil.setVal(jsonNode, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Tp/Prtry",
//						DataUtil.isNullObject(JsonUtil.getVal(jsonReturnEle, "/CdtrAcct/Tp/Prtry")) ? ""
//								: JsonUtil.getVal(jsonReturnEle, "/CdtrAcct/Tp/Prtry").asText());
//
//			}
//		}
//	}

//	/**
//	 * convert address line
//	 * @param root
//	 * @param jsonOrgDBAddress
//	 * @param check
//	 */
//	private static void convertAdrLine(JsonNode root, JsonNode jsonOrgDBAddress, String check){
//		String preDbtrOrCdtr;
//		if (check.equalsIgnoreCase("Cdtr"))
//			preDbtrOrCdtr = "Cdtr";
//		else
//			preDbtrOrCdtr = "Dbtr";
//
//		if (0 < jsonOrgDBAddress.size() && jsonOrgDBAddress.isArray()) {
//			int i = 0;
//			for (JsonNode jsonNodeAddress : jsonOrgDBAddress) {
//
//				JsonUtil.setVal(root,
//						"/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/" + preDbtrOrCdtr + "/Pty/PstlAdr/AdrLine/" + i,
//						StringUtils.isEmpty(jsonNodeAddress.asText()) ? AppConstant.Common.DEFAULT : jsonNodeAddress.asText(), i);
//				i++;
//			}
//		} else {
//			JsonUtil.remove(root,
//					"/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/" + preDbtrOrCdtr + "/Pty/PstlAdr");
//		}
//
//	}

	// pacs002 lay tu /payload/...
//	public static String buildPacs002(String jsonPacs008, String transId, String senderRefId, String errorCode,
//			String errorDesc, String errorNPCode, String errorNPDesc) {
//		JsonNode root = null;
//		try {
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				root = PACS002SUCCESJSON.deepCopy();
//			} else {
//				root = PACS002FAILJSON.deepCopy();
//			}
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNodePacs008 = objectMapper.readTree(jsonPacs008);
//
//			String msgIdr = JsonUtil.getVal(jsonNodePacs008, "/Header/MessageIdentifier").asText();
//			String OrgnlMsgId;
//			String OrgnlCreDtTm;
//			String resDescToNapas;
//			String orgPreDesc;
//			JsonNode paymentNod;
//			String IntrBkSttlmDt;
//			if (AppConstant.MsgIdr.CAMT034.equals(msgIdr)) {
//				return null;
//				/* 18/12/2020  if invest for copy will not build pacs002 to NP
//				OrgnlMsgId = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId").asText();
//				OrgnlCreDtTm = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm").asText();
//				resDescToNapas = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();
//				orgPreDesc = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();
//				paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//				IntrBkSttlmDt = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt").asText();
//				**end 18/12/2020  if invest for copy will not build pacs002 to NP */
//			} else {
//				OrgnlMsgId = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId").asText();
//				OrgnlCreDtTm = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm").asText();
//				resDescToNapas = JsonUtil.getVal(jsonNodePacs008,
//						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//				orgPreDesc = JsonUtil.getVal(jsonNodePacs008,
//						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();
//				paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//				IntrBkSttlmDt = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt")
//						.asText();
//
//			}
//
//			String OrgnlGrpInfAndSts_StsRsnInf_Prtry = "";
//			String StsId = "";
//			String TxInfAndSts_StsRsnInf_Prtry = "";
//
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = "";
//
//			String TxInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String TxInfAndSts_StsRsnInf_AddtlInf_2 = "";
//
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				StsId = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				String resCodeToNapas = ACHUtil.subStringbyIndex(transId, 6);
////				String resDescToNapas = JsonUtil.getVal(jsonNodePacs008,
////						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//			} else {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				StsId = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//
//				String orgPreCode = "000000";// --Version1.Common.ESQLs.getCounterFromTransId(transId,6);
////				String orgPreDesc = JsonUtil.getVal(jsonNodePacs008,
////						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + orgPreCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + orgPreDesc;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//			}
//
//			// JsonUtil.setVal(root, "/Header/SenderReference", senderRefId);
//			// JsonUtil.setVal(root, "/Header/MessageIdentifier",
//			// AppConstant.MsgIdr.PACS002);
//			// JsonUtil.setVal(root, "/Header/Format",
//			// AppConstant.PacsCommonConfig.HEADER_FORMAT);
//			// JsonUtil.setVal(root, "/Header/Sender/ID",
//			// AppConstant.PacsCommonConfig.SENDER_CODE);
//			// JsonUtil.setVal(root, "/Header/Sender/Name",
//			// AppConstant.PacsCommonConfig.SENDER_NAME);
//			// JsonUtil.setVal(root, "/Header/Receiver/ID",
//			// AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			// JsonUtil.setVal(root, "/Header/Receiver/ID",
//			// AppConstant.PacsCommonConfig.RECEIVER_NAME);
//			// SimpleDateFormat format = new
//			// SimpleDateFormat(AppConstant.TimeFormat.FORMAT_FULL);
//			// String timeStamp = format.format(new Date());
//			// JsonUtil.setVal(root, "/Header/Timestamp", timeStamp);
//			Date currDt = new Date();
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS002, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/CreDtTm",
//					DateUtil.formatTimeStampXXX(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgId",
//					OrgnlMsgId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId",
//					AppConstant.MsgIdr.PACS008);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlCreDtTm",
//					OrgnlCreDtTm);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/Rsn/Prtry",
//					OrgnlGrpInfAndSts_StsRsnInf_Prtry); /// sua
//			/// lai
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//			} else {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/2",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3, 2);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/3",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4, 3);
//			}
//
//			/////// lay dang mang tu pacs008
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsId", StsId);
////			JsonNode paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//			if (paymentNod.isArray()) {
//				for (JsonNode jsonPayElemet : paymentNod) {
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlInstrId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/InstrId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlEndToEndId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/EndToEndId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/TxId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/Rsn/Prtry",
//							TxInfAndSts_StsRsnInf_Prtry, 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_1, 0);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_2, 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//							JsonUtil.getVal(jsonPayElemet, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/IntrBkSttlmDt",
//							IntrBkSttlmDt,
//							1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/ClrChanl",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/ClrChanl").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/SvcLvl/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/LclInstrm/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/LclInstrm/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/CtgyPurp/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/CtgyPurp/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/RmtInf/Ustrd",
//							AppConstant.PacsCommonConfig.PAYLOAD_USTRD, 1);
//				}
//			}
//
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.PACS002, timestamp, signature);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Exception when handle buildPacs002:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}

	/*
	Building pacs002 for pacs008 IN camt034
	 */
//	public static String buildPacs002ForPacs008InCamt034(String jsonPacs008, String transId, String senderRefId, String errorCode,
//									  String errorDesc, String errorNPCode, String errorNPDesc) {
//		JsonNode root = null;
//		try {
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				root = PACS002SUCCESJSON.deepCopy();
//			} else {
//				root = PACS002FAILJSON.deepCopy();
//			}
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNodePacs008 = objectMapper.readTree(jsonPacs008);
//
//			String msgIdr = JsonUtil.getVal(jsonNodePacs008, "/Header/MessageIdentifier").asText();
//
//			String OrgnlMsgId = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId").asText();
//			String OrgnlCreDtTm = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm").asText();
//			String resDescToNapas = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();
//			String orgPreDesc = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();
//			JsonNode paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//			String IntrBkSttlmDt = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt").asText();
//
//			String OrgnlGrpInfAndSts_StsRsnInf_Prtry = "";
//			String StsId = "";
//			String TxInfAndSts_StsRsnInf_Prtry = "";
//
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = "";
//
//			String TxInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String TxInfAndSts_StsRsnInf_AddtlInf_2 = "";
//
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				StsId = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				String resCodeToNapas = ACHUtil.subStringbyIndex(transId, 6);
////				String resDescToNapas = JsonUtil.getVal(jsonNodePacs008,
////						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//			} else {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				StsId = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//
//				String orgPreCode = "000000";// --Version1.Common.ESQLs.getCounterFromTransId(transId,6);
////				String orgPreDesc = JsonUtil.getVal(jsonNodePacs008,
////						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + orgPreCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + orgPreDesc;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//			}
//
//			Date currDt = new Date();
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS002, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/CreDtTm",
//					DateUtil.formatTimeStampXXX(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgId",
//					OrgnlMsgId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId",
//					AppConstant.MsgIdr.PACS008);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlCreDtTm",
//					OrgnlCreDtTm);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/Rsn/Prtry",
//					OrgnlGrpInfAndSts_StsRsnInf_Prtry); /// sua
//			/// lai
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//			} else {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/2",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3, 2);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/3",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4, 3);
//			}
//
//			/////// lay dang mang tu pacs008
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsId", StsId);
////			JsonNode paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//			if (paymentNod.isArray()) {
//				for (JsonNode jsonPayElemet : paymentNod) {
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlInstrId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/InstrId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlEndToEndId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/EndToEndId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/TxId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/Rsn/Prtry",
//							TxInfAndSts_StsRsnInf_Prtry, 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_1, 0);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_2, 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//							JsonUtil.getVal(jsonPayElemet, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/IntrBkSttlmDt",
//							IntrBkSttlmDt,
//							1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/ClrChanl",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/ClrChanl").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/SvcLvl/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/LclInstrm/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/LclInstrm/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/CtgyPurp/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/CtgyPurp/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/RmtInf/Ustrd",
//							AppConstant.PacsCommonConfig.PAYLOAD_USTRD, 1);
//				}
//			}
//
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.PACS002, timestamp, signature);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Exception when handle buildPacs002ForCamt034:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}

	/*
	Building pacs002 for pacs004 in camt034
	 */
//	public static String buildPacs002ForPacs004InCamt034(String jsonPacs004, String transId, String senderRefId, String errorCode,
//												String errorDesc, String errorNPCode, String errorNPDesc) {
//		JsonNode root = null;
//		try {
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				root = PACS002SUCCESJSON.deepCopy();
//			} else {
//				root = PACS002FAILJSON.deepCopy();
//			}
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNodePacs004 = objectMapper.readTree(jsonPacs004);
//
//			String msgIdr = JsonUtil.getVal(jsonNodePacs004, "/Header/MessageIdentifier").asText();
//
//			String OrgnlMsgId = JsonUtil.getVal(jsonNodePacs004, "/Document/PmtRtr/GrpHdr/MsgId").asText();
//			String OrgnlCreDtTm = JsonUtil.getVal(jsonNodePacs004, "/Document/PmtRtr/GrpHdr/CreDtTm").asText();
//			String resDescToNapas = JsonUtil.getVal(jsonNodePacs004, "/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id").asText();
//			String orgPreDesc = JsonUtil.getVal(jsonNodePacs004, "/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id").asText();
//			JsonNode paymentNod = JsonUtil.getVal(jsonNodePacs004, "/Document/PmtRtr/TxInf");
//			String IntrBkSttlmDt = JsonUtil.getVal(jsonNodePacs004, "/Document/PmtRtr/GrpHdr/IntrBkSttlmDt").asText();
//
//			String OrgnlGrpInfAndSts_StsRsnInf_Prtry = "";
//			String StsId = "";
//			String TxInfAndSts_StsRsnInf_Prtry = "";
//
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = "";
//
//			String TxInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String TxInfAndSts_StsRsnInf_AddtlInf_2 = "";
//
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				StsId = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				String resCodeToNapas = ACHUtil.subStringbyIndex(transId, 6);
////				String resDescToNapas = JsonUtil.getVal(jsonNodePacs008,
////						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//			} else {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				StsId = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//
//				String orgPreCode = "000000";// --Version1.Common.ESQLs.getCounterFromTransId(transId,6);
////				String orgPreDesc = JsonUtil.getVal(jsonNodePacs008,
////						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id").asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + orgPreCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + orgPreDesc;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//			}
//
//			Date currDt = new Date();
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS002, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/CreDtTm",
//					DateUtil.formatTimeStampXXX(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgId",
//					OrgnlMsgId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId",
//					AppConstant.MsgIdr.PACS008);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlCreDtTm",
//					OrgnlCreDtTm);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/Rsn/Prtry",
//					OrgnlGrpInfAndSts_StsRsnInf_Prtry); /// sua
//			/// lai
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//			} else {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/2",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3, 2);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/3",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4, 3);
//			}
//
//			/////// lay dang mang tu pacs008
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsId", StsId);
////			JsonNode paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//			if (paymentNod.isArray()) {
//				for (JsonNode jsonPayElemet : paymentNod) {
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlInstrId",
//							JsonUtil.getVal(jsonPayElemet, "/OrgnlInstrId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlEndToEndId",
//							JsonUtil.getVal(jsonPayElemet, "/OrgnlEndToEndId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxId",
//							JsonUtil.getVal(jsonPayElemet, "/OrgnlTxId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/Rsn/Prtry",
//							TxInfAndSts_StsRsnInf_Prtry, 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_1, 0);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_2, 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//								JsonUtil.getVal(jsonPayElemet, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/IntrBkSttlmDt",
//							IntrBkSttlmDt,
//							1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/ClrChanl",
//								JsonUtil.getVal(jsonPayElemet, "/OrgnlTxRef/PmtTpInf/ClrChanl").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/LclInstrm/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/OrgnlTxRef/PmtTpInf/LclInstrm/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/CtgyPurp/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/OrgnlTxRef/PmtTpInf/CtgyPurp/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/RmtInf/Ustrd",
//							AppConstant.PacsCommonConfig.PAYLOAD_USTRD, 1);
//				}
//			}
//
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.PACS002, timestamp, signature);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Exception when handle buildPacs002ForPacs004InCamt034:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}

	// Pacs002copy lay tu /Document/xxxx
//	public static String buildPacs002ForPayCopy(String jsonPacs008, String transId, String senderRefId,
//			String errorCode, String errorDesc, String errorNPCode, String errorNPDesc) {
//		JsonNode root = null;
//		try {
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				root = PACS002SUCCESJSON.deepCopy();
//			} else {
//				root = PACS002FAILJSON.deepCopy();
//			}
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode jsonNodePacs008 = objectMapper.readTree(jsonPacs008);
//
//			String OrgnlGrpInfAndSts_StsRsnInf_Prtry = "";
//			String StsId = "";
//			String TxInfAndSts_StsRsnInf_Prtry = "";
//
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = "";
//			String OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = "";
//
//			String TxInfAndSts_StsRsnInf_AddtlInf_1 = "";
//			String TxInfAndSts_StsRsnInf_AddtlInf_2 = "";
//
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				StsId = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_AUTH;// Version1.Common.ESQLs.getVal('STATUS_ID_AUTH');
//				String resCodeToNapas = ACHUtil.subStringbyIndex(transId, 6);
//				String resDescToNapas = JsonUtil
//						.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id")
//						.asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + resCodeToNapas;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + resDescToNapas;
//			} else {
//				OrgnlGrpInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				TxInfAndSts_StsRsnInf_Prtry = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//				StsId = AppConstant.StatusId.STSID_NAUT;// Version1.Common.ESQLs.getVal('STATUS_ID_NAUT');
//
//				String orgPreCode = "000000";// --Version1.Common.ESQLs.getCounterFromTransId(transId,6);
//				String orgPreDesc = JsonUtil
//						.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id")
//						.asText();// COALESCE(bodyReqRef.CdtTrfTxInf.Item[1].DbtrAcct.Id.Othr.Id,'');
//
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_PREFIX_CODE + orgPreCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_PREFIX_DESC + orgPreDesc;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//				TxInfAndSts_StsRsnInf_AddtlInf_1 = AppConstant.PacsStatus.ACH_INDENT_ERROR_CODE + errorNPCode;
//				TxInfAndSts_StsRsnInf_AddtlInf_2 = AppConstant.PacsStatus.ACH_INDENT_ERROR_DESC + errorNPDesc;
//			}
//
//			// JsonUtil.setVal(root, "/Header/SenderReference", senderRefId);
//			// JsonUtil.setVal(root, "/Header/MessageIdentifier",
//			// AppConstant.MsgIdr.PACS002);
//			// JsonUtil.setVal(root, "/Header/Format",
//			// AppConstant.PacsCommonConfig.HEADER_FORMAT);
//			// JsonUtil.setVal(root, "/Header/Sender/ID",
//			// AppConstant.PacsCommonConfig.SENDER_CODE);
//			// JsonUtil.setVal(root, "/Header/Sender/Name",
//			// AppConstant.PacsCommonConfig.SENDER_NAME);
//			// JsonUtil.setVal(root, "/Header/Receiver/ID",
//			// AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			// JsonUtil.setVal(root, "/Header/Receiver/ID",
//			// AppConstant.PacsCommonConfig.RECEIVER_NAME);
//			// SimpleDateFormat format = new
//			// SimpleDateFormat(AppConstant.TimeFormat.FORMAT_FULL);
//			// String timeStamp = format.format(new Date());
//			// JsonUtil.setVal(root, "/Header/Timestamp", timeStamp);
//			Date currDt = new Date();
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS002, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/CreDtTm",
//					DateUtil.formatTimeStampXXX(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/GrpHdr/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgId",
//					JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId").asText());
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlMsgNmId",
//					AppConstant.MsgIdr.PACS008);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/OrgnlCreDtTm",
//					JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm").asText());
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/Rsn/Prtry",
//					OrgnlGrpInfAndSts_StsRsnInf_Prtry); /// sua
//			/// lai
//			if (AppConstant.SystemResponse.SUCCESS_CODE.equals(errorCode)) {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//
//			} else {
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/0",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_1, 0);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/1",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_2, 1);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/2",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_3, 2);
//				JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/OrgnlGrpInfAndSts/StsRsnInf/AddtlInf/3",
//						OrgnlGrpInfAndSts_StsRsnInf_AddtlInf_4, 3);
//			}
//
//			/////// lay dang mang tu pacs008
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsId", StsId);
//			JsonNode paymentNod = JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//			if (paymentNod.isArray()) {
//				for (JsonNode jsonPayElemet : paymentNod) {
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlInstrId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/InstrId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlEndToEndId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/EndToEndId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxId",
//							JsonUtil.getVal(jsonPayElemet, "/PmtId/TxId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/Rsn/Prtry",
//							TxInfAndSts_StsRsnInf_Prtry, 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/0",
//							TxInfAndSts_StsRsnInf_AddtlInf_1, 0);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/StsRsnInf/AddtlInf/1",
//							TxInfAndSts_StsRsnInf_AddtlInf_2, 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/InstgAgt/AddtlInf/FinInstnId/ClrSysMmbId/MmbId",
//							JsonUtil.getVal(jsonPayElemet, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId").asText(), 1);
//					JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/IntrBkSttlmDt",
//							JsonUtil.getVal(jsonNodePacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt")
//									.asText(),
//							1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/ClrChanl",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/ClrChanl").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/SvcLvl/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/SvcLvl/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/LclInstrm/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/LclInstrm/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/CtgyPurp/Prtry",
//							JsonUtil.getVal(jsonPayElemet, "/PmtTpInf/CtgyPurp/Prtry").asText(), 1);
//					JsonUtil.setVal(root,
//							"/Payload/Document/FIToFIPmtStsRpt/TxInfAndSts/0/OrgnlTxRef/PmtTpInf/RmtInf/Ustrd",
//							AppConstant.PacsCommonConfig.PAYLOAD_USTRD, 1);
//				}
//			}
//
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.PACS002, timestamp, signature);
//		} catch (Exception e) {
//			e.printStackTrace();
//			logger.error("Exception when handle buildPacs002ForPayCopy:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildPacs028(String senderRefId, String transId, String orgSenderRefId, String orgTransDt) {
//		JsonNode root = PACS028JSON.deepCopy();
//		try {
//			Date dateNow = new Date();
//
//			Date currDt = new Date();
//
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.PACS028, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/GrpHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/GrpHdr/CreDtTm", timestamp);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/GrpHdr/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.Common.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/GrpHdr/InstdAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.Common.BIC_NAPAS);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlMsgId", orgSenderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlMsgNmId",
//					AppConstant.MsgIdr.PACS008);
//
//			SimpleDateFormat sdf6 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/OrgnlGrpInf/OrgnlCreDtTm", sdf6.format(dateNow));
//
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/TxInf/OrgnlTxId", orgSenderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/TxInf/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.Common.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/FIToFIPmtStsReq/TxInf/OrgnlTxRef/IntrBkSttlmDt",
//					orgTransDt.substring(0, 10));
//
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.PACS028, timestamp, signature);
//		} catch (Exception e) {
//			logger.error("Exception when handle buildPacs028:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildCamt033(CopyPaymentDto copyPayDto, String transId, String senderRefId) {
//		JsonNode root = CAMT033JSON.deepCopy();
//		try {
//			Date currDt = new Date();
//
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.CAMT033, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/Assgnmt/Id", senderRefId);
//
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/Assgnmt/Assgnr/Agt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/Assgnmt/Assgne/Agt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.RECEIVER_CODE);
//
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/Assgnmt/CreDtTm", DateUtil.formatTimeStampXXX(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/Case/Id", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/Case/Cretr/Agt/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/SplmtryData/0/PlcAndNm", "cma.paymentSearch.001.01");
//
//			JsonUtil.setVal(root, "/Payload/Document/ReqForDplct/SplmtryData/0/Envlp/Document/PmtSch/MsgId",
//					copyPayDto.getOrgSenderRefId());
//			if (!DataUtil.isNullObject(copyPayDto.getOrgTxId())) {
//				JsonUtil.setVal(root,
//						"/Payload/Document/ReqForDplct/SplmtryData/0/Envlp/Document/PmtSch/ShrtBizId/TxId",
//						copyPayDto.getOrgTxId());
//			} else {
//				JsonUtil.remove(root,
//						"/Payload/Document/ReqForDplct/SplmtryData/0/Envlp/Document/PmtSch/ShrtBizId/TxId");
//			}
//			JsonUtil.setVal(root,
//					"/Payload/Document/ReqForDplct/SplmtryData/0/Envlp/Document/PmtSch/ShrtBizId/IntrBkSttlmDt",
//					copyPayDto.getOrgSettleDate());
//			JsonUtil.setVal(root,
//					"/Payload/Document/ReqForDplct/SplmtryData/0/Envlp/Document/PmtSch/ShrtBizId/InstgAgt/FinInstnId/ClrSysMmbId/MmbId",
//					copyPayDto.getOrgSenderCode());
//
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.CAMT033, timestamp, signature);
//		} catch (Exception e) {
//			logger.error("Exception when handle buildCamt033:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildCamt998Dispute(DisputeDto dispDto, String transId, String transCopy,
//			String pacs008Content, String pacs004Content, String toOrgCode, String frOrgCode, TransAchDispute achDisp) {
//		JsonNode root = CAMT998DISPUTEJSON.deepCopy();
//		try {
//			String orgMsgId = "", orgMsgName = "", orgCreTime = "", orgSenderCode = "", orgInstrId = "",
//					orgEndtoEndId = "", orgTxId = "", orgCurrency = "", orgSttlmAmt = "", orgSttlmDate = "",
//					isRootDispute = "", DsptTpCd = "", typeMsgSenderReference = "", disputeService = "",
//					Sbjct = "YEU CAU TRA SOAT", rtnMsgId = "", rtnMsgName = "", rtnCreTime = "", rtnSenderCode = "",
//					rtnInstrId = "", rtnEndtoEndId = "", rtnTxId = "", rtnCurrency = "", rtnSttlmAmt = "",
//					rtnSttlmDate = "";
//			DsptTpCd = dispDto.getDispType();
//			if ("1".equals(transCopy)) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				JsonNode jsonNodePacs008 = objectMapper.readTree(pacs008Content);
//				orgMsgId = JsonUtil
//						.getVal(jsonNodePacs008,
//								"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId")
//						.asText();
//				orgMsgName = AppConstant.MsgIdr.PACS008;
//				orgCreTime = JsonUtil
//						.getVal(jsonNodePacs008,
//								"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm")
//						.asText();
//				orgSttlmDate = JsonUtil.getVal(jsonNodePacs008,
//						"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt")
//						.asText();
//				JsonNode paymentNode = JsonUtil.getVal(jsonNodePacs008,
//						"/Payload/Document/Dplct/Dplct/Data/Any/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//				if (paymentNode.isArray()) {
//					for (JsonNode jsonPayElemet : paymentNode) {
//						orgSenderCode = JsonUtil.getVal(jsonPayElemet, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId")
//								.asText();
//						orgInstrId = JsonUtil.getVal(jsonPayElemet, "/PmtId/InstrId").asText();
//						orgEndtoEndId = JsonUtil.getVal(jsonPayElemet, "/PmtId/EndToEndId").asText();
//						orgTxId = JsonUtil.getVal(jsonPayElemet, "/PmtId/TxId").asText();
//						orgCurrency = JsonUtil.getVal(jsonPayElemet, "/IntrBkSttlmAmt/Ccy").asText();
//						orgSttlmAmt = JsonUtil.getVal(jsonPayElemet, "/IntrBkSttlmAmt/Value").asText();
//					}
//				}
//			} else {
//				ObjectMapper objectMapper = new ObjectMapper();
//				JsonNode jsonNodePacs008 = objectMapper.readTree(pacs008Content);
//				orgMsgId = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId")
//						.asText();
//				orgMsgName = AppConstant.MsgIdr.PACS008;
//				orgCreTime = JsonUtil.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm")
//						.asText();
//				orgSttlmDate = JsonUtil
//						.getVal(jsonNodePacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/IntrBkSttlmDt").asText();
//				JsonNode paymentNode = JsonUtil.getVal(jsonNodePacs008,
//						"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf");
//				if (paymentNode.isArray()) {
//					for (JsonNode jsonPayElemet : paymentNode) {
//						orgSenderCode = JsonUtil.getVal(jsonPayElemet, "/InstgAgt/FinInstnId/ClrSysMmbId/MmbId")
//								.asText();
//						orgInstrId = JsonUtil.getVal(jsonPayElemet, "/PmtId/InstrId").asText();
//						orgEndtoEndId = JsonUtil.getVal(jsonPayElemet, "/PmtId/EndToEndId").asText();
//						orgTxId = JsonUtil.getVal(jsonPayElemet, "/PmtId/TxId").asText();
//						orgCurrency = JsonUtil.getVal(jsonPayElemet, "/IntrBkSttlmAmt/Ccy").asText();
//						orgSttlmAmt = JsonUtil.getVal(jsonPayElemet, "/IntrBkSttlmAmt/Value").asText();
//					}
//				}
//
//			}
//
//			if (!DataUtil.isNullOrEmpty(dispDto.getClarDispType())
//					&& AppConstant.DisputeConfig.DisputeType.DISPUTE_CLAR_FUNCTION.equals(dispDto.getClarDispType())) {
//
//				disputeService = "clarificationinformation";
//				if (AppConstant.DisputeConfig.DisputeType.DISP_EDIT_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "01";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_RTN_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "02";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_INFO_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "03";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_SUPPORT_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "04";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_FAITH_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "05";
//				}
//			} else {
//
//				if (AppConstant.DisputeConfig.DisputeType.DISP_EDIT_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "01";
//					disputeService = "requestanamendment";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_RTN_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "02";
//					disputeService = "requestforreturn";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_INFO_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "03";
//					disputeService = "requesttoprovidetransactioninformation";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_SUPPORT_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "04";
//					disputeService = "supportcollection";
//				} else if (AppConstant.DisputeConfig.DisputeType.DISP_FAITH_TYPE.equals(dispDto.getDispType())) {
//					typeMsgSenderReference = "05";
//					disputeService = "goodfaith";
//				}
//			}
//
//			String[] arrParam = { typeMsgSenderReference };
//			String senderRefId = ACHUtil.generateSenderRefId(transId, AppConstant.MsgIdr.CAMT998_DISPUTE,
//					AppConstant.PacsCommonConfig.SENDER_ID, AppConstant.SenderRefType.SENDER_REF_SPEC, arrParam);
//			dispDto.setSenderRefId(senderRefId);
//			dispDto.setDispService(disputeService);
//			if (DataUtil.isNullOrEmpty(dispDto.getCaseId())) {
//				dispDto.setCaseId(senderRefId);
//				isRootDispute = "1"; // --danh dau ban ghi dispute goc
//			}
//
//			// set vao doi tuong json
//			Date currDt = new Date();
//
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.CAMT998_DISPUTE, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/MsgHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/MsgHdr/CreDtTm",
//					DateUtil.formatTimeStampXXX(currDt));
//
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Tp", AppConstant.DisputeConfig.TP_VALUE);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Assgnmt/Id",
//					senderRefId);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Assgnmt/Assgnr/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Assgnmt/Assgne/ClrSysMmbId/MmbId",
//					toOrgCode);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Assgnmt/CreDtTm",
//					DateUtil.formatTimeStampXXX(currDt));
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/DsptTpCd",
//					DsptTpCd);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Case/Id",
//					dispDto.getCaseId());
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Case/Cretr/ClrSysMmbId/MmbId",
//					frOrgCode);
////					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Case/Sbjct",
//					Sbjct);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/DsptAmt/Ccy",
//					dispDto.getCurrency());
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/DsptAmt/Value",
//					ACHUtil.formatAmount(dispDto.getAmount()));
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlGrpInf/OrgnlMsgId",
//					orgMsgId);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlGrpInf/OrgnlMsgNmId",
//					orgMsgName);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlGrpInf/OrgnlCreDtTm",
//					orgCreTime);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlGrpInf/OrgnlInstgAgt/ClrSysMmbId/MmbId",
//					orgSenderCode);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlInstrId",
//					orgInstrId);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlEndToEndId",
//					orgEndtoEndId);
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlTxId",
//					orgTxId);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlIntrBkSttlmAmt/Ccy",
//					orgCurrency);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlIntrBkSttlmAmt/Value",
//					orgSttlmAmt);
//			JsonUtil.setVal(root,
//					"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Undrlyg/OrgnlIntrBkSttlmDt",
//					orgSttlmDate);
//
//			if (!DataUtil.isNullOrEmpty(pacs004Content)) {
//				ObjectMapper objectMapper = new ObjectMapper();
//				JsonNode jsonNodePacs004 = objectMapper.readTree(pacs004Content);
//
//				rtnMsgId = JsonUtil.getVal(jsonNodePacs004, "/Payload/Document/PmtRtr/GrpHdr/MsgId").asText();
//				rtnMsgName = AppConstant.MsgIdr.PACS004;
//				rtnCreTime = JsonUtil.getVal(jsonNodePacs004, "/Payload/Document/PmtRtr/GrpHdr/CreDtTm").asText();
//				rtnSenderCode = AppConstant.PacsCommonConfig.SENDER_CODE;
//				rtnSttlmDate = JsonUtil.getVal(jsonNodePacs004, "/Payload/Document/PmtRtr/GrpHdr/IntrBkSttlmDt")
//						.asText();
//				JsonNode paymentNode = JsonUtil.getVal(jsonNodePacs004, "/Payload/Document/PmtRtr/TxInf");
//				if (paymentNode.isArray()) {
//					for (JsonNode jsonPayElemet : paymentNode) {
//
//						rtnInstrId = JsonUtil.getVal(jsonPayElemet, "/OrgnlInstrId").asText();
//						rtnEndtoEndId = JsonUtil.getVal(jsonPayElemet, "/OrgnlEndToEndId").asText();
//						rtnTxId = JsonUtil.getVal(jsonPayElemet, "/RtrId").asText();
//						rtnCurrency = JsonUtil.getVal(jsonPayElemet, "/RtrdIntrBkSttlmAmt/Ccy").asText();
//						rtnSttlmAmt = JsonUtil.getVal(jsonPayElemet, "/RtrdIntrBkSttlmAmt/Value").asText();
//
//					}
//				}
//
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlGrpInf/OrgnlMsgId",
//						rtnMsgId);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlGrpInf/OrgnlMsgNmId",
//						rtnMsgName);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlGrpInf/OrgnlCreDtTm",
//						rtnCreTime);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlGrpInf/OrgnlInstgAgt/ClrSysMmbId/MmbId",
//						rtnSenderCode);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlInstrId",
//						rtnInstrId);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlEndToEndId",
//						rtnEndtoEndId);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlTxId",
//						rtnTxId);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlIntrBkSttlmAmt/Ccy",
//						rtnCurrency);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlIntrBkSttlmAmt/Value",
//						rtnSttlmAmt);
//				JsonUtil.setVal(root,
//						"/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx/OrgnlIntrBkSttlmDt",
//						rtnSttlmDate);
//
//			} else {
//				JsonUtil.remove(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/CrrctnTx");
//			}
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Msg",
//					dispDto.getDispDescription());
//			JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/DsptSts",
//					dispDto.getDispStatus());
//
//			/// attachment
//			int i = 0;
//			List<AttachmentInfo> lsAttch = dispDto.getAttchmentInfo();
//			if (!DataUtil.isNullObject(lsAttch) && lsAttch.size() > 0) {
//				for (AttachmentInfo attachmentInfo : lsAttch) {// tam set co 1
//																// mang
//					JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Attchmnt/"
//							+ i + "/AttchmntDscr", attachmentInfo.getAttchDesc());
//					JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Attchmnt/"
//							+ i + "/MIMETp", attachmentInfo.getAttchType());
//					JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Attchmnt/"
//							+ i + "/NcodgTp", attachmentInfo.getAttchNCode());
//					JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Attchmnt/"
//							+ i + "/CharSet", attachmentInfo.getAttchCharSet());
//					JsonUtil.setVal(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Attchmnt/"
//							+ i + "/InclBinryObjct", attachmentInfo.getAttchContent());
//					i++;
//				}
//			} else {
//				JsonUtil.remove(root, "/Payload/Document/CshMgmtPrtryMsg/PrtryData/Data/Document/DsptMsg/Attchmnt");
//			}
//
//			///////////////////////// set dispute
//			achDisp.setCaseId(dispDto.getCaseId());
//			achDisp.setBizSvc(AppConstant.PacsCommonConfig.PAYLOAD_BIZSVC);
//			achDisp.setCaseSbjct(Sbjct);
//			achDisp.setCaseSenderCode(AppConstant.PacsCommonConfig.SENDER_CODE);
//			achDisp.setDispAmt(dispDto.getAmount());
//			achDisp.setDispCurrency(dispDto.getCurrency());
//			achDisp.setDispDesc(dispDto.getDispDescription());
//			achDisp.setDispType(dispDto.getDispType());
//			achDisp.setDispStatus(dispDto.getDispStatus());
//			achDisp.setIsRootMsg(isRootDispute);
//			achDisp.setFrSystem(AppConstant.PacsCommonConfig.SENDER_CODE);
//			achDisp.setToSystem(AppConstant.PacsCommonConfig.RECEIVER_CODE);
//			achDisp.setMsgId(senderRefId);
//			achDisp.setMsgName(AppConstant.MsgIdr.CAMT998_DISPUTE);
//			achDisp.setMsgSenderRefId(senderRefId);
//			achDisp.setCreatedOn(new Date());
//			achDisp.setOrgCrdDatetime(orgCreTime);
//			achDisp.setOrgCurrency(orgCurrency);
//			achDisp.setOrgEndtoendId(orgEndtoEndId);
//			achDisp.setOrgInstrId(orgInstrId);
//			achDisp.setOrgMsgId(orgMsgId);
//			achDisp.setOrgMsgName(orgMsgName);
//			achDisp.setOrgSenderCode(orgSenderCode);
//			achDisp.setOrgSttlmAmt(orgSttlmAmt);
//			achDisp.setOrgSttlmDate(orgSttlmDate);
//			achDisp.setOrgTxid(orgTxId);
//			if (!DataUtil.isNullOrEmpty(pacs004Content)) {
//				achDisp.setRtnCrdDatetime(rtnCreTime);
//				achDisp.setRtnCurrency(rtnCurrency);
//				achDisp.setRtnEndtoendId(rtnEndtoEndId);
//				achDisp.setRtnInstrId(rtnInstrId);
//				achDisp.setRtnMsgId(rtnMsgId);
//				achDisp.setRtnMsgName(rtnMsgName);
//				achDisp.setRtnSenderCode(rtnSenderCode);
//				achDisp.setRtnSttlmAmt(rtnSttlmAmt);
//				achDisp.setRtnSttlmDate(rtnSttlmDate);
//				achDisp.setRtnTxid(rtnTxId);
//			}
//
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.CAMT998_DISPUTE, timestamp, signature);
//		} catch (Exception e) {
//			logger.error("Exception when handle buildCamt998Dispute:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildCamt009(LimitDto limitDto, String transId, String senderRefId) {
//		JsonNode root = CAMT009JSON.deepCopy();
//		try {
//			Date currDt = new Date();
//
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.CAMT009, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/GetLmt/MsgHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/GetLmt/MsgHdr/CreDtTm", timestamp);
//			JsonUtil.setVal(root,
//					"/Payload/Document/GetLmt/LmtQryDef/LmtCrit/NewCrit/SchCrit/AcctOwnr/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/GetLmt/LmtQryDef/LmtCrit/NewCrit/SchCrit/AcctId/Othr/Id",
//					limitDto.getAccountLimit());
//			if (DataUtil.isNullOrEmpty(limitDto.getCurrency())) {
//				JsonUtil.remove(root, "/Payload/Document/GetLmt/LmtQryDef/LmtCrit/NewCrit/SchCrit/LmtCcy");
//			} else {
//				JsonUtil.setVal(root, "/Payload/Document/GetLmt/LmtQryDef/LmtCrit/NewCrit/SchCrit/LmtCcy",
//						limitDto.getCurrency());
//			}
//
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.CAMT009, timestamp, signature);
//		} catch (Exception e) {
//			logger.error("Exception when handle buildCamt009:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildCamt011(LimitDto limitDto, String transId, String senderRefId) {
//		JsonNode root = CAMT011JSON.deepCopy();
//		try {
//			Date currDt = new Date();
//
//			String creDt = DateUtil.formatTimeStampZGMT0(currDt);
//			String timestamp = DateUtil.formatTimeStampXXX(currDt);
//			buildPayloadAppHdr(root, AppConstant.MsgIdr.CAMT011, senderRefId, creDt);
//
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/MsgHdr/MsgId", senderRefId);
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/MsgHdr/CreDtTm", timestamp);
//
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/LmtDtls/LmtId/Cur/Tp/Prtry", "CLEARING");
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/LmtDtls/LmtId/Cur/AcctOwnr/FinInstnId/ClrSysMmbId/MmbId",
//					AppConstant.PacsCommonConfig.SENDER_CODE);
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/LmtDtls/LmtId/Cur/AcctId/Othr/Id",
//					limitDto.getAccountLimit());
//
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/LmtDtls/NewLmtValSet/Amt/AmtWthCcy/Ccy",
//					limitDto.getCurrency());
//
//			JsonUtil.setVal(root, "/Payload/Document/ModfyLmt/LmtDtls/NewLmtValSet/Amt/AmtWthCcy/Value",
//					ACHUtil.formatAmount(limitDto.getAmountLimit()));
//
//			// String signature = Crypto.createSign("RSA2048", payload,
//			// "D:\\\\RSA Key
//			// live\\\\Bank_PrivateKey.cer");
////			String signature = "";
//			String payload = JsonUtil.getVal(root, "/Payload").toString();
//			String signature = Crypto.createSign("RSA2048", payload, null);
//			buildMsgHeader(root, senderRefId, AppConstant.MsgIdr.CAMT011, timestamp, signature);
//		} catch (Exception e) {
//			logger.error("Exception when handle buildCamt011:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildDas(JsonNode rootIso8583, String transId) {
//		JsonNode root = DASJSON.deepCopy();
//		try {
//
//			JsonUtil.setVal(root, "/msgType", JsonUtil.getVal(rootIso8583, "/body/iso8583/MTI").asText());
//			JsonUtil.setVal(root, "/PAN", JsonUtil.getVal(rootIso8583, "/body/iso8583/DE002_PAN").asText());
//			JsonUtil.setVal(root, "/processingCode",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE003_PROC_CD").asText());
//			JsonUtil.setVal(root, "/transAmount", JsonUtil.getVal(rootIso8583, "/body/iso8583/DE004_TRN_AMT").asText());
//			JsonUtil.setVal(root, "/transmissionDateTime",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE007_TRN_DT").asText());
//			JsonUtil.setVal(root, "/systemTraceAuditNum",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE011_TRACE_NO").asText());
//			JsonUtil.setVal(root, "/localTime",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE012_LOC_TRN_TIME").asText());
//			JsonUtil.setVal(root, "/localDate",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE013_LOC_TRN_DATE").asText());
//			JsonUtil.setVal(root, "/settlementDate",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE015_STL_DATE").asText());
//			JsonUtil.setVal(root, "/pointOfServiceEntryCode",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE022_POS_MODE").asText());
//			JsonUtil.setVal(root, "/pointOfServiceConditionCode",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE025_POS_COND_CD").asText());
//			JsonUtil.setVal(root, "/sendingMember",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE032_ACQ_CD").asText());
//			JsonUtil.setVal(root, "/retRefNumber",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE037_REL_REF_NO").asText());
//			JsonUtil.setVal(root, "/cardAcceptorTerminalId",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE041_CRD_ACPT_TRM").asText());
//			JsonUtil.setVal(root, "/cardAcceptorId",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE042_CRD_ACPT_ID").asText());
//			JsonUtil.setVal(root, "/cardAcceptorNameLocation",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE043_CRD_ACPT_LOC").asText());
//			JsonUtil.setVal(root, "/additionalDataPrivate",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE048_ADD_PRV_INF").asText());
//			JsonUtil.setVal(root, "/transCurrencyCode",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE049_TRN_CCY").asText());
//			JsonUtil.setVal(root, "/usrDefinedField",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE060_CNL_TP").asText());
//			JsonUtil.setVal(root, "/serviceCode",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE062_NAP_SVC_CD").asText());
//			JsonUtil.setVal(root, "/receivingMember",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE100_BEN_CD").asText());
//			JsonUtil.setVal(root, "/senderAcc",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE102_SND_ACC_INF").asText());
//
//			String transRefNumber = ("".equals(JsonUtil.getVal(rootIso8583, "/body/iso8583/DE063_TRN_REF_NO").asText()))
//					? ACHUtil.subStringbyIndex(transId, 16)
//					: JsonUtil.getVal(rootIso8583, "/body/iso8583/DE063_TRN_REF_NO").asText();
//			JsonUtil.setVal(root, "/transRefNumber", transRefNumber);
//
//			JsonUtil.setVal(root, "/receiverAcc",
//					JsonUtil.getVal(rootIso8583, "/body/iso8583/DE103_RCV_ACC_INF").asText());
//
//			String contentTransfers = JsonUtil.getVal(rootIso8583, "/body/iso8583/DE104_TRN_CONT").asText();
//			JsonUtil.setVal(root, "/contentTransfers", StringUtils.isEmpty(contentTransfers) ? AppConstant.MsgDesc.DESC_CONTENT_DEFAULT : contentTransfers);
//
//			JsonUtil.setVal(root, "/MAC", JsonUtil.getVal(rootIso8583, "/body/iso8583/DE128_MAC_DAT").asText());
//		} catch (Exception e) {
//			logger.error("Exception when handle buildDas:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
	public static void buildMsgHeader(JsonNode root, String senderRef, String msgIdr, String timestamp, String sign) {
		JsonUtil.setVal(root, "/Header/SenderReference", senderRef);
		JsonUtil.setVal(root, "/Header/MessageIdentifier", msgIdr);
		JsonUtil.setVal(root, "/Header/Format", AppConstant.PacsCommonConfig.HEADER_FORMAT);
		JsonUtil.setVal(root, "/Header/Sender/ID", AppConstant.PacsCommonConfig.SENDER_CODE);
		JsonUtil.setVal(root, "/Header/Sender/Name", AppConstant.PacsCommonConfig.SENDER_NAME);
		JsonUtil.setVal(root, "/Header/Receiver/ID", AppConstant.PacsCommonConfig.RECEIVER_CODE);
		JsonUtil.setVal(root, "/Header/Receiver/Name", AppConstant.PacsCommonConfig.RECEIVER_NAME);
		JsonUtil.setVal(root, "/Header/Timestamp", timestamp);
		JsonUtil.setVal(root, "/Header/Signature", sign);
	}
//
	public static void buildPayloadAppHdr(JsonNode root, String msgDefIdr, String bizMsgIdr, String creDt) {
		JsonUtil.setVal(root, "/Payload/AppHdr/Fr/FIId/FinInstnId/ClrSysMmbId/MmbId",
				AppConstant.PacsCommonConfig.SENDER_CODE);
		JsonUtil.setVal(root, "/Payload/AppHdr/To/FIId/FinInstnId/ClrSysMmbId/MmbId",
				AppConstant.PacsCommonConfig.RECEIVER_CODE);
		JsonUtil.setVal(root, "/Payload/AppHdr/BizMsgIdr", bizMsgIdr);
		JsonUtil.setVal(root, "/Payload/AppHdr/MsgDefIdr", msgDefIdr);
		JsonUtil.setVal(root, "/Payload/AppHdr/BizSvc", AppConstant.PacsCommonConfig.PAYLOAD_BIZSVC);
		// lay gio GMT0
		JsonUtil.setVal(root, "/Payload/AppHdr/CreDt", creDt);
	}
//
//	public static String buildISO8583Das(String messageDas, String globalId) {
//		JsonNode root = ISO8583JSON.deepCopy();
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootDas = objectMapper.readTree(messageDas);
//
////			JsonUtil.setVal(root, "/header/GLB_ID", JsonUtil.getVal(rootDas, "/").asText());
//			JsonUtil.setVal(root, "/header/GLB_ID", globalId);
//
//			JsonUtil.setVal(root, "/body/iso8583/MTI", JsonUtil.getVal(rootDas, "/msgType").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE002_PAN", JsonUtil.getVal(rootDas, "/PAN").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE003_PROC_CD", JsonUtil.getVal(rootDas, "/processingCode").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE004_TRN_AMT", JsonUtil.getVal(rootDas, "/transAmount").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE007_TRN_DT",
//					JsonUtil.getVal(rootDas, "/transmissionDateTime").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE011_TRACE_NO",
//					JsonUtil.getVal(rootDas, "/systemTraceAuditNum").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE012_LOC_TRN_TIME", JsonUtil.getVal(rootDas, "/localTime").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE013_LOC_TRN_DATE", JsonUtil.getVal(rootDas, "/localDate").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE", JsonUtil.getVal(rootDas, "/settlementDate").asText());
////			JsonUtil.setVal(root, "/body/iso8583/DE018_MER_CAT_CD", JsonUtil.getVal(rootDas, "/merchantType").asText());
//
//			if ("0200".equals(JsonUtil.getVal(root, "/body/iso8583/MTI").asText())){
//				// remove
//				JsonUtil.remove(root, "/body/iso8583/DE038_AUTH_ID_RES");
//				JsonUtil.remove(root, "/body/iso8583/DE039_RES_CD");
//				JsonUtil.remove(root, "/body/iso8583/DE120_BEN_INF");
//
//
//				JsonUtil.setVal(root, "/body/iso8583/DE022_POS_MODE",
//						JsonUtil.getVal(rootDas, "/pointOfServiceEntryCode").asText());
//				JsonUtil.setVal(root, "/body/iso8583/DE025_POS_COND_CD",
//						JsonUtil.getVal(rootDas, "/pointOfServiceConditionCode").asText());
//				if (StringUtils.isNotEmpty(JsonUtil.getVal(rootDas, "/cardAcceptorId").asText())) {
//                    JsonUtil.setVal(root, "/body/iso8583/DE042_CRD_ACPT_ID",
//                            JsonUtil.getVal(rootDas, "/cardAcceptorId").asText());
//                } else {
//                    JsonUtil.remove(root, "/body/iso8583/DE042_CRD_ACPT_ID");
//                }
//				JsonUtil.setVal(root, "/body/iso8583/DE043_CRD_ACPT_LOC",
//						JsonUtil.getVal(rootDas, "/cardAcceptorNameLocation").asText());
//
//
//			}
//
//
//			JsonUtil.setVal(root, "/body/iso8583/DE032_ACQ_CD", JsonUtil.getVal(rootDas, "/sendingMember").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE037_REL_REF_NO", JsonUtil.getVal(rootDas, "/retRefNumber").asText());
//
//
//			if ("0210".equals(JsonUtil.getVal(root, "/body/iso8583/MTI").asText())) {
//				// remove
//				JsonUtil.remove(root, "/body/iso8583/DE022_POS_MODE");
//				JsonUtil.remove(root, "/body/iso8583/DE025_POS_COND_CD");
//				JsonUtil.remove(root, "/body/iso8583/DE042_CRD_ACPT_ID");
//				JsonUtil.remove(root, "/body/iso8583/DE043_CRD_ACPT_LOC");
//
//				JsonUtil.setVal(root, "/body/iso8583/DE039_RES_CD", JsonUtil.getVal(rootDas, "/responseCode").asText());
//
//				String authIdResponse = JsonUtil.getVal(rootDas, "/authIdResponse").asText();
//				JsonUtil.setVal(root, "/body/iso8583/DE038_AUTH_ID_RES",
//						StringUtils.isEmpty(authIdResponse) ?  ACHUtil.getNumberRandom(6): authIdResponse);
//				JsonUtil.setVal(root, "/body/iso8583/DE120_BEN_INF",
//						JsonUtil.getVal(rootDas, "/accountHolderName").asText());
//			}
//			JsonUtil.setVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM",
//					JsonUtil.getVal(rootDas, "/cardAcceptorTerminalId").asText());
//
//			if (StringUtils.isNotEmpty(JsonUtil.getVal(rootDas, "/additionalDataPrivate").asText())) {
//                JsonUtil.setVal(root, "/body/iso8583/DE048_ADD_PRV_INF",
//                        JsonUtil.getVal(rootDas, "/additionalDataPrivate").asText());
//            } else {
//                JsonUtil.remove(root, "/body/iso8583/DE048_ADD_PRV_INF");
//            }
//			JsonUtil.setVal(root, "/body/iso8583/DE049_TRN_CCY",
//					JsonUtil.getVal(rootDas, "/transCurrencyCode").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE060_CNL_TP", JsonUtil.getVal(rootDas, "/usrDefinedField").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD", JsonUtil.getVal(rootDas, "/serviceCode").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE063_TRN_REF_NO",
//					JsonUtil.getVal(rootDas, "/transRefNumber").asText());
//			if (StringUtils.isNotEmpty(JsonUtil.getVal(rootDas, "/receivingMember").asText()))
//			    JsonUtil.setVal(root, "/body/iso8583/DE100_BEN_CD", JsonUtil.getVal(rootDas, "/receivingMember").asText());
//			else
//                JsonUtil.remove(root, "/body/iso8583/DE100_BEN_CD");
//			JsonUtil.setVal(root, "/body/iso8583/DE102_SND_ACC_INF", JsonUtil.getVal(rootDas, "/senderAcc").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE103_RCV_ACC_INF", JsonUtil.getVal(rootDas, "/receiverAcc").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE104_TRN_CONT",
//					JsonUtil.getVal(rootDas, "/contentTransfers").asText());
//
//			JsonUtil.setVal(root, "/body/iso8583/DE128_MAC_DAT", JsonUtil.getVal(rootDas, "/MAC").asText());
//
//			JsonUtil.remove(root, "/body/iso8583/DE005_STL_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE006_BIL_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE018_MER_CAT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE026_PIN_CAP_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE035_TRK2_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE036_TRK3_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE023_CRD_SEQ_NO");
//			JsonUtil.remove(root, "/body/iso8583/DE010_BIL_CONV_RT");
//
//			JsonUtil.remove(root, "/body/iso8583/DE051_BIL_CCY");
//			JsonUtil.remove(root, "/body/iso8583/DE045_TRK1_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE052_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE054_ADD_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE055_EMV_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE070_NET_MGT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE090_ORG_TRN_KEY");
//			JsonUtil.remove(root, "/body/iso8583/DE105_NEW_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE009_STL_CONV_RT");
//			JsonUtil.remove(root, "/body/iso8583/DE019_ACQ_CTRY_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE050_STL_CCY");
//			JsonUtil.remove(root, "/body/iso8583/DE060_CNL_TP");
//
//		} catch (Exception e) {
//			logger.error("Exception when handle buildDas8583:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	public static String buildISO8583NRT(String messagePacs008, String dbtrMemCode) {
//		JsonNode root = ISO8583JSON.deepCopy();
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs008 = objectMapper.readTree(messagePacs008);
//
//			JsonUtil.setVal(root, "/header/GLB_ID", JsonUtil.getVal(rootPacs008, "/").asText());
//
//			String InstrId = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/InstrId").asText();
//			String EndToEndId = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/EndToEndId").asText();
//			String InstrInf1 = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/0/InstrInf")
//					.asText();
//			String InstrInf2 = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/1/InstrInf")
//					.asText();
//			String InstrInf3 = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/2/InstrInf")
//					.asText();
//			String InstrInf4 = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/3/InstrInf")
//					.asText();
//
////			String[] InstrInf1Arr = InstrInf1.split("/");
////			String[] InstrInf2Arr = InstrInf2.split("/");
////			String[] InstrInf3Arr = InstrInf3.split("/");
////			String[] InstrInf4Arr = InstrInf4.split("/");
//
//            String IntrBkSttlmAmt = JsonUtil
//                    .getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/IntrBkSttlmAmt/Value").asText();
//
//            JsonUtil.setVal(root, "/body/iso8583/DE004_TRN_AMT", ACHUtil.getAmount(IntrBkSttlmAmt));
//
//            Map<String, String> map = ACHUtil.getInstrInf1(InstrInf1 + InstrInf2 + InstrInf3);
//            buildMsgIso8583FromInstrInf(root, map);
//
//			JsonUtil.setVal(root, "/body/iso8583/DE003_PROC_CD", InstrId.substring(0, 6));
//			JsonUtil.setVal(root, "/body/iso8583/DE032_ACQ_CD", dbtrMemCode);
//			JsonUtil.setVal(root, "/body/iso8583/DE013_LOC_TRN_DATE", InstrId.substring(12, 16));
//			JsonUtil.setVal(root, "/body/iso8583/DE012_LOC_TRN_TIME", InstrId.substring(16, 22));
//			JsonUtil.setVal(root, "/body/iso8583/DE037_REL_REF_NO", InstrId.substring(22, 34));
//
//			JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE", EndToEndId.substring(0, 4));
//			JsonUtil.setVal(root, "/body/iso8583/DE060_CNL_TP", EndToEndId.substring(4, 6));
////			JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD", EndToEndId.substring(6, 12));
//			JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD", AppConstant.ServiceId.SERVICE_COPY_NRT_EQUAL_CRRDATE);
//			JsonUtil.setVal(root, "/body/iso8583/DE063_TRN_REF_NO", EndToEndId.substring(12, 28));
//			String senderRefId = JsonUtil.getVal(rootPacs008, "/Header/SenderReference").asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE011_TRACE_NO", senderRefId.substring(28, 34));
//
////			String sttLmAmt = JsonUtil
////					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Value").asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE005_STL_AMT",ACHUtil.getAmount(IntrBkSttlmAmt));
//
//			String cdAcctName = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/Nm").asText();
//			String cdAcctAddress = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/0")
//					.asText()
//					+ " "
//					+ JsonUtil.getVal(rootPacs008,
//							"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/1").asText()
//					+ " "
//					+ JsonUtil
//							.getVal(rootPacs008,
//									"/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/2")
//							.asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE048_ADD_PRV_INF", cdAcctName + "\r" + cdAcctAddress);
//
////			String ccy = JsonUtil
////					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Ccy").asText();
//
//			String cdAcctNo = JsonUtil
//					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id")
//					.asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE002_PAN", cdAcctNo);
//
//			JsonUtil.setVal(root, "/body/iso8583/MTI", "0200");
//
//			JsonUtil.setVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM", senderRefId.substring(20, 28));
//            JsonUtil.remove(root, "/body/iso8583/DE023_CRD_SEQ_NO");
//            JsonUtil.remove(root, "/body/iso8583/DE010_BIL_CONV_RT");
//            JsonUtil.remove(root, "/body/iso8583/DE006_BIL_AMT");
//            JsonUtil.remove(root, "/body/iso8583/DE038_AUTH_ID_RES");
//            JsonUtil.remove(root, "/body/iso8583/DE026_PIN_CAP_CD");
//            JsonUtil.remove(root, "/body/iso8583/DE035_TRK2_DAT");
//            JsonUtil.remove(root, "/body/iso8583/DE051_BIL_CCY");
//            JsonUtil.remove(root, "/body/iso8583/DE036_TRK3_DAT");
//            JsonUtil.remove(root, "/body/iso8583/DE039_RES_CD");
//            JsonUtil.remove(root, "/body/iso8583/DE045_TRK1_DAT");
//            JsonUtil.remove(root, "/body/iso8583/DE120_BEN_INF");
//            JsonUtil.remove(root, "/body/iso8583/DE052_PIN");
//            JsonUtil.remove(root, "/body/iso8583/DE054_ADD_AMT");
//            JsonUtil.remove(root, "/body/iso8583/DE055_EMV_DAT");
//            JsonUtil.remove(root, "/body/iso8583/DE070_NET_MGT_CD");
//            JsonUtil.remove(root, "/body/iso8583/DE090_ORG_TRN_KEY");
//            JsonUtil.remove(root, "/body/iso8583/DE105_NEW_PIN");
//            JsonUtil.remove(root, "/body/iso8583/DE128_MAC_DAT");
//
//		} catch (Exception e) {
//			logger.error("Exception when handle buildNrt8583:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//	/*
//	Building 0200 iso8583 from pacs008 in camt034
//	 */
//	public static String buildISO8583Pacs008FromCamt034(String messagePacs008, String dbtrMemCode) {
//		JsonNode root = ISO8583JSON.deepCopy();
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs008 = objectMapper.readTree(messagePacs008);
//
//			String OrgnlCreDtTm = JsonUtil.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/CreDtTm").asText();
//
//			JsonUtil.setVal(root, "/header/GLB_ID", JsonUtil.getVal(rootPacs008, "/").asText());
//
//			String InstrId = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/InstrId").asText();
//			String EndToEndId = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/PmtId/EndToEndId").asText();
//			String InstrInf1 = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/0/InstrInf")
//					.asText();
//			String InstrInf2 = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/1/InstrInf")
//					.asText();
//			String InstrInf3 = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/2/InstrInf")
//					.asText();
//			String InstrInf4 = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/InstrForNxtAgt/3/InstrInf")
//					.asText();
//
////			String[] InstrInf1Arr = InstrInf1.split("/");
////			String[] InstrInf2Arr = InstrInf2.split("/");
////			String[] InstrInf3Arr = InstrInf3.split("/");
////			String[] InstrInf4Arr = InstrInf4.split("/");
//
//			String IntrBkSttlmAmt = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/IntrBkSttlmAmt/Value").asText();
//
//			JsonUtil.setVal(root, "/body/iso8583/DE004_TRN_AMT", ACHUtil.getAmount(IntrBkSttlmAmt));
//			/*
//			JsonUtil.setVal(root, "/body/iso8583/DE007_TRN_DT", InstrInf1Arr[4]);
//			JsonUtil.setVal(root, "/body/iso8583/DE009_STL_CONV_RT", InstrInf1Arr[6]);
//			JsonUtil.setVal(root, "/body/iso8583/DE018_MER_CAT_CD", InstrInf1Arr[8]);
//			JsonUtil.setVal(root, "/body/iso8583/DE019_ACQ_CTRY_CD", InstrInf1Arr[10]);
//			JsonUtil.setVal(root, "/body/iso8583/DE022_POS_MODE", InstrInf1Arr[12]);
//			JsonUtil.setVal(root, "/body/iso8583/DE025_POS_COND_CD", InstrInf1Arr[14]);
//			JsonUtil.setVal(root, "/body/iso8583/DE042_CRD_ACPT_ID", InstrInf1Arr[18]);
//
//			JsonUtil.setVal(root, "/body/iso8583/DE043_CRD_ACPT_LOC", InstrInf2Arr[2]);
//			JsonUtil.setVal(root, "/body/iso8583/DE049_TRN_CCY", InstrInf2Arr[4]);
//			JsonUtil.setVal(root, "/body/iso8583/DE100_BEN_CD", InstrInf2Arr[6]);
//			JsonUtil.setVal(root, "/body/iso8583/DE102_SND_ACC_INF", InstrInf2Arr[8]);
//			JsonUtil.setVal(root, "/body/iso8583/DE103_RCV_ACC_INF", InstrInf2Arr[10]);
//
//			JsonUtil.setVal(root, "/body/iso8583/DE104_TRN_CONT", InstrInf3Arr[2]);
////			JsonUtil.setVal(root, "/body/iso8583/DE128_MAC_DAT", InstrInf4Arr[2]); */
//
//            Map<String, String> map = ACHUtil.getInstrInf1(InstrInf1 + InstrInf2 + InstrInf3);
//            buildMsgIso8583FromInstrInf(root, map);
//
//			JsonUtil.setVal(root, "/body/iso8583/DE003_PROC_CD", InstrId.substring(0, 6));
//			JsonUtil.setVal(root, "/body/iso8583/DE032_ACQ_CD", dbtrMemCode);
//			JsonUtil.setVal(root, "/body/iso8583/DE013_LOC_TRN_DATE", InstrId.substring(12, 16));
//			JsonUtil.setVal(root, "/body/iso8583/DE012_LOC_TRN_TIME", InstrId.substring(16, 22));
//			JsonUtil.setVal(root, "/body/iso8583/DE037_REL_REF_NO", InstrId.substring(22, 34));
//
//			JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE", EndToEndId.substring(0, 4));
//			JsonUtil.setVal(root, "/body/iso8583/DE060_CNL_TP", EndToEndId.substring(4, 6));
//			if (ACHUtil.isEqualCurrentDate(OrgnlCreDtTm)) { //Valid CreDtTm equals current date format yyyy-MM-dd
//				JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD", AppConstant.ServiceId.SERVICE_COPY_NRT_EQUAL_CRRDATE);
//			} else {
//				JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD", AppConstant.ServiceId.SERVICE_COPY_NRT_NOTEQ_CRRDATE);
//			}
//			JsonUtil.setVal(root, "/body/iso8583/DE063_TRN_REF_NO", EndToEndId.substring(12, 28));
//			String senderRefId = JsonUtil.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/GrpHdr/MsgId").asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE011_TRACE_NO", senderRefId.substring(28, 34));
//
////			String sttLmAmt = JsonUtil
////					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Value").asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE005_STL_AMT",ACHUtil.getAmount(IntrBkSttlmAmt));
//
//			String cdAcctName = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/Nm").asText();
//			String cdAcctAddress = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Dbtr/PstlAdr/AdrLine/0")
//					.asText()
//					+ " "
//					+ JsonUtil.getVal(rootPacs008,
//					"/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/1").asText()
//					+ " "
//					+ JsonUtil
//					.getVal(rootPacs008,
//							"/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/Cdtr/PstlAdr/AdrLine/2")
//					.asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE048_ADD_PRV_INF", cdAcctName + "\r" + cdAcctAddress);
//
////			String ccy = JsonUtil
////					.getVal(rootPacs008, "/Payload/Document/FIToFICstmrCdtTrf/GrpHdr/TtlIntrBkSttlmAmt/Ccy").asText();
////			JsonUtil.setVal(root, "/body/iso8583/DE050_STL_CCY", InstrInf2Arr[4]);
//
//			String cdAcctNo = JsonUtil
//					.getVal(rootPacs008, "/Document/FIToFICstmrCdtTrf/CdtTrfTxInf/0/DbtrAcct/Id/Othr/Id")
//					.asText();
//			JsonUtil.setVal(root, "/body/iso8583/DE002_PAN", cdAcctNo);
//
//			JsonUtil.setVal(root, "/body/iso8583/MTI", "0200");
//
//			JsonUtil.setVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM", senderRefId.substring(20, 28));
//			JsonUtil.remove(root, "/body/iso8583/DE023_CRD_SEQ_NO");
//			JsonUtil.remove(root, "/body/iso8583/DE010_BIL_CONV_RT");
//			JsonUtil.remove(root, "/body/iso8583/DE006_BIL_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE038_AUTH_ID_RES");
//			JsonUtil.remove(root, "/body/iso8583/DE026_PIN_CAP_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE035_TRK2_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE051_BIL_CCY");
//			JsonUtil.remove(root, "/body/iso8583/DE036_TRK3_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE039_RES_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE045_TRK1_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE120_BEN_INF");
//			JsonUtil.remove(root, "/body/iso8583/DE052_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE054_ADD_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE055_EMV_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE070_NET_MGT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE090_ORG_TRN_KEY");
//			JsonUtil.remove(root, "/body/iso8583/DE105_NEW_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE128_MAC_DAT");
//
//		} catch (Exception e) {
//			logger.error("Exception when handle buildISO8583Camt034:" + e.getMessage());
//			return "";
//		}
//		return root.toPrettyString();
//	}
//
//    public static void buildMsgIso8583FromInstrInf(JsonNode root, Map<String, String> map) {
//
////			InstrInf1
//        String TDT, SCR, MCC, AIC, PEM, PCD, FID, MID;
//        TDT = map.get("TDT");
//        SCR = map.get("SCR");
//        MCC = map.get("MCC");
//        AIC = map.get("AIC");
//        PEM = map.get("PEM");
//        PCD = map.get("PCD");
//        FID = map.get("FID");
//        MID = map.get("MID");
//
//        //DE007_TRN_DT
//        JsonUtil.setVal(root, "/body/iso8583/DE007_TRN_DT", TDT);
//        //DE009_STL_CONV_RT
//        JsonUtil.setVal(root, "/body/iso8583/DE009_STL_CONV_RT", SCR);
//        //DE018_MER_CAT_CD
//        if (StringUtils.isNotEmpty(MCC))
//            JsonUtil.setVal(root, "/body/iso8583/DE018_MER_CAT_CD", MCC);
//        else
//            JsonUtil.remove(root, "/body/iso8583/DE018_MER_CAT_CD");
//        //DE019_ACQ_CTRY_CD
//        if (StringUtils.isNotEmpty(AIC))
//            JsonUtil.setVal(root, "/body/iso8583/DE019_ACQ_CTRY_CD", AIC);
//        else
//            JsonUtil.remove(root, "/body/iso8583/DE019_ACQ_CTRY_CD");
//        //DE022_POS_MODE
//        JsonUtil.setVal(root, "/body/iso8583/DE022_POS_MODE", PEM);
//        //DE025_POS_COND_CD
//        JsonUtil.setVal(root, "/body/iso8583/DE025_POS_COND_CD", PCD);
//        //DE042_CRD_ACPT_ID
//        if (StringUtils.isNotEmpty(MID))
//            JsonUtil.setVal(root, "/body/iso8583/DE042_CRD_ACPT_ID", MID);
//        else
//            JsonUtil.remove(root, "/body/iso8583/DE042_CRD_ACPT_ID");
//
////			InstrInf2
//        String MNM, SCC, BID, FAI, TAI;
//        MNM = map.get("MNM");
//        SCC = map.get("SCC");
//        BID = map.get("BID");
//        FAI = map.get("FAI");
//        TAI = map.get("TAI");
//
//        //DE043_CRD_ACPT_LOC
//        JsonUtil.setVal(root, "/body/iso8583/DE043_CRD_ACPT_LOC", MNM);
//        //DE049_TRN_CCY, DE050_STL_CCY
//        JsonUtil.setVal(root, "/body/iso8583/DE049_TRN_CCY", SCC);
//        JsonUtil.setVal(root, "/body/iso8583/DE050_STL_CCY", SCC);
//
//        //DE100_BEN_CD
//        if (StringUtils.isNotEmpty(BID))
//            JsonUtil.setVal(root, "/body/iso8583/DE100_BEN_CD", BID);
//        else
//            JsonUtil.remove(root, "/body/iso8583/DE100_BEN_CD");
//        //DE102_SND_ACC_INF
//        JsonUtil.setVal(root, "/body/iso8583/DE102_SND_ACC_INF", FAI);
//        //DE103_RCV_ACC_INF
//        JsonUtil.setVal(root, "/body/iso8583/DE103_RCV_ACC_INF", TAI);
//
////			InstrInf3
//        String CTR;
//        CTR = map.get("CTR");
//
//        //DE104_TRN_CONT
//        JsonUtil.setVal(root, "/body/iso8583/DE104_TRN_CONT", CTR);
//    }
//
//	public static String buildISO8583NRTNAK(String message, String err_code, String authIdRes) {
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs008 = objectMapper.readTree(message);
//
//			JsonUtil.setVal(rootPacs008, "/body/iso8583/MTI", "0210");
//			JsonUtil.setVal(rootPacs008, "/body/iso8583/DE039_RES_CD", err_code);
//
//			if(!StringUtils.isEmpty(authIdRes))
//				JsonUtil.setVal(rootPacs008, "/body/iso8583/DE038_AUTH_ID_RES", ACHUtil.subStringbyIndex(authIdRes, 6));
//
//			return rootPacs008.toPrettyString();
//		} catch (Exception e) {
//			logger.error("Exception when handle buildISO8583NRTNAK:" + e.getMessage());
//			return "";
//		}
//	}
//
//	public static String buildISO8583Investigation(String message, String orgMessage, String err_code, String authIdRes, String errException) {
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs008 = objectMapper.readTree(message);
//
//			JsonUtil.setVal(rootPacs008, "/body/iso8583/MTI", "0210");
//			JsonUtil.setVal(rootPacs008, "/body/iso8583/DE039_RES_CD", "00");
//			if (StringUtils.isNotEmpty(orgMessage)) {
//				JsonNode rootOrgMessage = objectMapper.readTree(orgMessage);
////				#Add 48: 13 for Original Trx(4Bytes)+ #41 for Original Trx(8Bytes) + #39 forOriginal Trx(2 Bytes)
//				String orgDe013 = ACHUtil.subStringbyIndex(JsonUtil.getVal(rootOrgMessage, "/body/iso8583/DE013_LOC_TRN_DATE").asText(), 4);
//				String orgDe041 = ACHUtil.subStringbyIndex(JsonUtil.getVal(rootOrgMessage, "/body/iso8583/DE041_CRD_ACPT_TRM").asText(), 8);
//				if ("12".equals(err_code)) {
//				    String investDe048 = JsonUtil.getVal(rootPacs008, "/body/iso8583/DE048_ADD_PRV_INF").asText() + err_code;
//                    JsonUtil.setVal(rootPacs008, "/body/iso8583/DE048_ADD_PRV_INF", investDe048);
//                }
//				else {
////                    String orgDe039 = ACHUtil.subStringbyIndex(JsonUtil.getVal(rootOrgMessage, "/body/iso8583/DE039_RES_CD").asText(), 2);
//                    JsonUtil.setVal(rootPacs008, "/body/iso8583/DE048_ADD_PRV_INF", orgDe013 + orgDe041 + err_code);
//                }
////				JsonUtil.setVal(rootPacs008, "/body/iso8583/DE012_LOC_TRN_TIME", JsonUtil.getVal(rootOrgMessage, "/body/iso8583/DE012_LOC_TRN_TIME").asText());
////				JsonUtil.setVal(rootPacs008, "/body/iso8583/DE013_LOC_TRN_DATE", JsonUtil.getVal(rootOrgMessage, "/body/iso8583/DE013_LOC_TRN_DATE").asText());
//			}
//            if (AppConstant.SystemResponse.SYSTEM_ERROR_EXCEPTION_CODE.equals(errException)) {
//                JsonUtil.setVal(rootPacs008, "/body/iso8583/DE039_RES_CD", errException);
//            }
//            //remove 22,25,42,43
//			JsonUtil.remove(rootPacs008, "/body/iso8583/DE022_POS_MODE");
//			JsonUtil.remove(rootPacs008, "/body/iso8583/DE025_POS_COND_CD");
//			JsonUtil.remove(rootPacs008, "/body/iso8583/DE042_CRD_ACPT_ID");
//			JsonUtil.remove(rootPacs008, "/body/iso8583/DE043_CRD_ACPT_LOC");
//
//			if(!StringUtils.isEmpty(authIdRes))
//				JsonUtil.setVal(rootPacs008, "/body/iso8583/DE038_AUTH_ID_RES", ACHUtil.subStringbyIndex(authIdRes, 6));
//
//			return rootPacs008.toPrettyString();
//		} catch (Exception e) {
//			logger.error("Exception when handle buildISO8583Investigation:" + e.getMessage());
//			return "";
//		}
//	}
//
//	public static String buildISO8583PACS004(String messagePacs004, String orgMessageIso8583, String dbtrMemCode) {
//		try {
//			JsonNode root = ISO8583JSON.deepCopy();
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs004 = objectMapper.readTree(messagePacs004);
//			JsonNode orgMessage = objectMapper.readTree(orgMessageIso8583);
//
//			String senderRefId = JsonUtil.getVal(rootPacs004, "/Header/SenderReference").asText();
//			String fromAcc = JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Id/Othr/Id").asText();
//
//			JsonUtil.setVal(root, "/body/iso8583/MTI",
//					"0200");
//			JsonUtil.setVal(root, "/body/iso8583/DE002_PAN", fromAcc);
//			JsonUtil.setVal(root, "/body/iso8583/DE003_PROC_CD",
//					getProcessCode(JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Tp/Prtry").asText(),
//							JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Tp/Prtry").asText()));
////					JsonUtil.getVal(rootPacs008, "Document/PmtRtr/TxInf/OrgnlInstrId").asText().substring(0, 6));
//			JsonUtil.setVal(root, "/body/iso8583/DE004_TRN_AMT",
//					ACHUtil.getAmount(JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Value").asText()));
//			JsonUtil.setVal(root, "/body/iso8583/DE005_STL_AMT",
//					ACHUtil.getAmount(JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Value").asText()));
//			JsonUtil.setVal(root, "/body/iso8583/DE007_TRN_DT",
//					DateUtil.formatMMddHHmmss(DateUtil.parseTimestampZ2Date(JsonUtil.getVal(rootPacs004, "/Payload/AppHdr/CreDt").asText())));
//			JsonUtil.setVal(root, "/body/iso8583/DE009_STL_CONV_RT",
//					"00000001");
//			JsonUtil.setVal(root, "/body/iso8583/DE011_TRACE_NO",
//					ACHUtil.subStringbyIndex(senderRefId.substring(senderRefId.length() - 6), 6));
//			String MMddHHmmss = DateUtil.formatMMddHHmmss(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/GrpHdr/CreDtTm").asText()));
//			JsonUtil.setVal(root, "/body/iso8583/DE012_LOC_TRN_TIME",
//					MMddHHmmss.substring(4));
//			JsonUtil.setVal(root, "/body/iso8583/DE013_LOC_TRN_DATE",
//					MMddHHmmss.substring(0, 4));
//			// handle optional IntrBkSttlmDt
//			String intrBkSttlmDt = JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/GrpHdr/IntrBkSttlmDt").asText();
//			if (StringUtils.isNotEmpty(intrBkSttlmDt)) {
//				JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE",
//						intrBkSttlmDt.substring(5).replace("-", ""));
//			} else {
//				intrBkSttlmDt = JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/IntrBkSttlmDt").asText();
//				JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE",
//						StringUtils.isEmpty(intrBkSttlmDt) ? MMddHHmmss.substring(0, 4) : intrBkSttlmDt.substring(5).replace("-", ""));
//			}
//			JsonUtil.setVal(root, "/body/iso8583/DE019_ACQ_CTRY_CD",
//					"704");
//			JsonUtil.setVal(root, "/body/iso8583/DE022_POS_MODE",
//					"000");
//			JsonUtil.setVal(root, "/body/iso8583/DE025_POS_COND_CD",
//					"00");
//			JsonUtil.setVal(root, "/body/iso8583/DE032_ACQ_CD",
//					dbtrMemCode);
//			JsonUtil.setVal(root, "/body/iso8583/DE037_REL_REF_NO",
//					ACHUtil.subStringbyIndex(senderRefId.substring(senderRefId.length() - 12), 12));
//			JsonUtil.setVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM",
//					"00000001");
//			JsonUtil.setVal(root, "/body/iso8583/DE042_CRD_ACPT_ID",
//					"GRP01 000015686");
//			JsonUtil.setVal(root, "/body/iso8583/DE043_CRD_ACPT_LOC",
//					"ACH NAPASVNVN            HANOI       VNM");
//			JsonUtil.setVal(root, "/body/iso8583/DE048_ADD_PRV_INF",
//					"Hoan Tra Giao Dich \r");
//			JsonUtil.setVal(root, "/body/iso8583/DE049_TRN_CCY",
//					"704");
//			JsonUtil.setVal(root, "/body/iso8583/DE050_STL_CCY",
//					"704");
//			JsonUtil.setVal(root, "/body/iso8583/DE060_CNL_TP",
//					"02");
//			JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD",
//					AppConstant.ServiceId.SERVICE_RETURN);
//			JsonUtil.setVal(root, "/body/iso8583/DE063_TRN_REF_NO",
//					ACHUtil.subStringbyIndex(senderRefId.substring(senderRefId.length() - 16), 16));
//			String de090 = JsonUtil.getVal(orgMessage, "/body/iso8583/MTI").asText() + JsonUtil.getVal(orgMessage, "/body/iso8583/DE011_TRACE_NO").asText()
//					+ JsonUtil.getVal(orgMessage, "/body/iso8583/DE007_TRN_DT").asText()  + StringUtils.leftPad(JsonUtil.getVal(orgMessage, "/body/iso8583/DE032_ACQ_CD").asText(), 11, "0")
//					+ "00000000000";
//			JsonUtil.setVal(root, "/body/iso8583/DE090_ORG_TRN_KEY",
//					de090);
//			JsonUtil.setVal(root, "/body/iso8583/DE100_BEN_CD",
//					"970457");
//			JsonUtil.setVal(root, "/body/iso8583/DE102_SND_ACC_INF", fromAcc);
//			JsonUtil.setVal(root, "/body/iso8583/DE103_RCV_ACC_INF",
//					JsonUtil.getVal(rootPacs004, "/Payload/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE104_TRN_CONT",
//					"Hoan Tra Giao Dich");
//
//			//Remove empty
//			JsonUtil.remove(root, "/body/iso8583/DE023_CRD_SEQ_NO");
//			JsonUtil.remove(root, "/body/iso8583/DE010_BIL_CONV_RT");
//			JsonUtil.remove(root, "/body/iso8583/DE006_BIL_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE018_MER_CAT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE038_AUTH_ID_RES");
//			JsonUtil.remove(root, "/body/iso8583/DE026_PIN_CAP_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE035_TRK2_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE051_BIL_CCY");
//			JsonUtil.remove(root, "/body/iso8583/DE036_TRK3_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE039_RES_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE045_TRK1_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE120_BEN_INF");
//			JsonUtil.remove(root, "/body/iso8583/DE052_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE054_ADD_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE055_EMV_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE070_NET_MGT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE105_NEW_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE128_MAC_DAT");
//
//			return root.toPrettyString();
//		} catch (Exception e) {
//			logger.error("Exception when handle buildISO8583PACS004:" + e.getMessage());
//			return "";
//		}
//	}
//
//	public static String buildISO8583Pacs004FromCam043(String message, String orgMessageIso8583, String CreDt, String dbtrMemCode) {
//		try {
//			JsonNode root = ISO8583JSON.deepCopy();
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode rootPacs004 = objectMapper.readTree(message);
//			JsonNode orgMessage = objectMapper.readTree(orgMessageIso8583);
//
//			String senderRefId = JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/GrpHdr/MsgId").asText();
//			String fromAcc = JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Id/Othr/Id").asText();
//
//			JsonUtil.setVal(root, "/body/iso8583/MTI",
//					"0200");
//			JsonUtil.setVal(root, "/body/iso8583/DE002_PAN", fromAcc);
//			JsonUtil.setVal(root, "/body/iso8583/DE003_PROC_CD",
//					getProcessCode(JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Tp/Prtry").asText(),
//							JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/OrgnlTxRef/CdtrAcct/Tp/Prtry").asText()));
////					JsonUtil.getVal(rootPacs008, "Document/PmtRtr/TxInf/OrgnlInstrId").asText().substring(0, 6));
//			JsonUtil.setVal(root, "/body/iso8583/DE004_TRN_AMT",
//					ACHUtil.getAmount(JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Value").asText()));
//			JsonUtil.setVal(root, "/body/iso8583/DE005_STL_AMT",
//					ACHUtil.getAmount(JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/RtrdIntrBkSttlmAmt/Value").asText()));
//			JsonUtil.setVal(root, "/body/iso8583/DE007_TRN_DT",
//					DateUtil.formatMMddHHmmss(DateUtil.parseTimestampZ2Date(CreDt)));
//			JsonUtil.setVal(root, "/body/iso8583/DE009_STL_CONV_RT",
//					"00000001");
//			JsonUtil.setVal(root, "/body/iso8583/DE011_TRACE_NO",
//					ACHUtil.subStringbyIndex(senderRefId.substring(senderRefId.length() - 6), 6));
//			String MMddHHmmss = DateUtil.formatMMddHHmmss(DateUtil.parseTimestampXXX2Date(JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/GrpHdr/CreDtTm").asText()));
//			JsonUtil.setVal(root, "/body/iso8583/DE012_LOC_TRN_TIME",
//					MMddHHmmss.substring(4));
//			JsonUtil.setVal(root, "/body/iso8583/DE013_LOC_TRN_DATE",
//					MMddHHmmss.substring(0, 4));
//
//			// handle optional IntrBkSttlmDt
//			String intrBkSttlmDt = JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/GrpHdr/IntrBkSttlmDt").asText();
//			if (StringUtils.isNotEmpty(intrBkSttlmDt)) {
//				JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE",
//						intrBkSttlmDt.substring(5).replace("-", ""));
//			} else {
//				intrBkSttlmDt = JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/IntrBkSttlmDt").asText();
//				JsonUtil.setVal(root, "/body/iso8583/DE015_STL_DATE",
//						StringUtils.isEmpty(intrBkSttlmDt) ? MMddHHmmss.substring(0, 4) : intrBkSttlmDt.substring(5).replace("-", ""));
//			}
//
//			JsonUtil.setVal(root, "/body/iso8583/DE019_ACQ_CTRY_CD",
//					"704");
//			JsonUtil.setVal(root, "/body/iso8583/DE022_POS_MODE",
//					"000");
//			JsonUtil.setVal(root, "/body/iso8583/DE025_POS_COND_CD",
//					"00");
//			JsonUtil.setVal(root, "/body/iso8583/DE032_ACQ_CD",
//					dbtrMemCode);
//			JsonUtil.setVal(root, "/body/iso8583/DE037_REL_REF_NO",
//					ACHUtil.subStringbyIndex(senderRefId.substring(senderRefId.length() - 12), 12));
//			JsonUtil.setVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM",
//					"00000001");
//			JsonUtil.setVal(root, "/body/iso8583/DE042_CRD_ACPT_ID",
//					"GRP01 000015686");
//			JsonUtil.setVal(root, "/body/iso8583/DE043_CRD_ACPT_LOC",
//					"ACH NAPASVNVN            HANOI       VNM");
//			JsonUtil.setVal(root, "/body/iso8583/DE048_ADD_PRV_INF",
//					"Hoan Tra Giao Dich \r");
//			JsonUtil.setVal(root, "/body/iso8583/DE049_TRN_CCY",
//					"704");
//			JsonUtil.setVal(root, "/body/iso8583/DE050_STL_CCY",
//					"704");
//			JsonUtil.setVal(root, "/body/iso8583/DE060_CNL_TP",
//					"02");
//			JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD",
//					AppConstant.ServiceId.SERVICE_COPY_RETURN);
//			JsonUtil.setVal(root, "/body/iso8583/DE063_TRN_REF_NO",
//					ACHUtil.subStringbyIndex(senderRefId.substring(senderRefId.length() - 16), 16));
//			String de090 = JsonUtil.getVal(orgMessage, "/body/iso8583/MTI").asText() + JsonUtil.getVal(orgMessage, "/body/iso8583/DE011_TRACE_NO").asText()
//					+ JsonUtil.getVal(orgMessage, "/body/iso8583/DE007_TRN_DT").asText()  + StringUtils.leftPad(JsonUtil.getVal(orgMessage, "/body/iso8583/DE032_ACQ_CD").asText(), 11, "0")
//					+ "00000000000";
//			JsonUtil.setVal(root, "/body/iso8583/DE090_ORG_TRN_KEY",
//					de090);
//			JsonUtil.setVal(root, "/body/iso8583/DE100_BEN_CD",
//					"970457");
//			JsonUtil.setVal(root, "/body/iso8583/DE102_SND_ACC_INF", fromAcc);
//			JsonUtil.setVal(root, "/body/iso8583/DE103_RCV_ACC_INF",
//					JsonUtil.getVal(rootPacs004, "/Document/PmtRtr/TxInf/0/OrgnlTxRef/DbtrAcct/Id/Othr/Id").asText());
//			JsonUtil.setVal(root, "/body/iso8583/DE104_TRN_CONT",
//					"Hoan Tra Giao Dich");
//
//			//Remove empty
//			JsonUtil.remove(root, "/body/iso8583/DE023_CRD_SEQ_NO");
//			JsonUtil.remove(root, "/body/iso8583/DE010_BIL_CONV_RT");
//			JsonUtil.remove(root, "/body/iso8583/DE006_BIL_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE018_MER_CAT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE038_AUTH_ID_RES");
//			JsonUtil.remove(root, "/body/iso8583/DE026_PIN_CAP_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE035_TRK2_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE051_BIL_CCY");
//			JsonUtil.remove(root, "/body/iso8583/DE036_TRK3_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE039_RES_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE045_TRK1_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE120_BEN_INF");
//			JsonUtil.remove(root, "/body/iso8583/DE052_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE054_ADD_AMT");
//			JsonUtil.remove(root, "/body/iso8583/DE055_EMV_DAT");
//			JsonUtil.remove(root, "/body/iso8583/DE070_NET_MGT_CD");
//			JsonUtil.remove(root, "/body/iso8583/DE105_NEW_PIN");
//			JsonUtil.remove(root, "/body/iso8583/DE128_MAC_DAT");
//
//			return root.toPrettyString();
//		} catch (Exception e) {
//			logger.error("Exception when handle buildISO8583Pacs004FromCam043:" + e.getMessage());
//			return "";
//		}
//	}
//
//	private static String getProcessCode(String input1, String input2){
//		String methodType1;
//		String methodType2;
//		if (AppConstant.MethodType.ACCT_TYPE.equals(input1.toUpperCase()))
//			methodType1 = "20";
//		else
//			methodType1 = "00";
//
//		if (AppConstant.MethodType.ACCT_TYPE.equals(input2.toUpperCase()))
//			methodType2 = "20";
//		else
//			methodType2 = "00";
//
//		return "91" + methodType1 + methodType2;
//	}
//
//	public static String buildISO8583DASNACK(String msg, String err_code) {
//		try {
//			ObjectMapper objectMapper = new ObjectMapper();
//			JsonNode root = objectMapper.readTree(msg);
//
//			JsonUtil.setVal(root, "/body/iso8583/MTI", "0210");
//			JsonUtil.setVal(root, "/body/iso8583/DE039_RES_CD", err_code);
//			JsonUtil.setVal(root, "/body/iso8583/DE0120_BEN_INF", "");
//
//			return root.toPrettyString();
//		} catch (Exception e) {
//			logger.error("Exception when handle buildDAS8583 NACK:" + e.getMessage());
//			return "";
//		}
//	}
//	public static String checkGetCredt(String OrgDate) {
//		DateUtil dateUtil = new DateUtil();
//		logger.info("+++parse formatTimeStampZ:" + dateUtil.formatTimeStampZ(dateUtil.parseTimestampyyyyMMddHHmmss(OrgDate)));
//
//		String date = new SimpleDateFormat("MMddHHmmss").format(new Date());
//		String parseOrgDate = new SimpleDateFormat("MMddHHmmss").format(dateUtil.parseTimestampyyyyMMddHHmmss(OrgDate));
//		logger.info("+++parseOrgDate:" + parseOrgDate);
//		int hour = Integer.parseInt(OrgDate.substring(4, 6)) + 7;
//		String hours = String.valueOf(hour);
//
//		if (24 < hour)
//			hours = String.format("%02d" , hour - 24);
//
//		String timeStamp = date.substring(0, 4) + hours + OrgDate.substring(6);
//		if(Integer.parseInt(parseOrgDate.substring(0, 2)) < Integer.parseInt(OrgDate.substring(0, 2))) {
//			return dateUtil.formatTimeStampZ(dateUtil.parseTimestampyyyyMMddHHmmss(timeStamp));
//		}
//		else if(Integer.parseInt(parseOrgDate.substring(2, 4)) < Integer.parseInt(OrgDate.substring(2, 4))) {
//			return dateUtil.formatTimeStampZ(dateUtil.parseTimestampyyyyMMddHHmmss(timeStamp));
//		}
//		else if(Integer.parseInt(parseOrgDate.substring(4, 6)) < Integer.parseInt(OrgDate.substring(4, 6))) {
//			return dateUtil.formatTimeStampZ(dateUtil.parseTimestampyyyyMMddHHmmss(parseOrgDate.substring(0, 4) + hours + OrgDate.substring(6)));
//		} else {
//			logger.info("+++else parse formatTimeStampZ");
//			return  dateUtil.formatTimeStampZ(dateUtil.parseTimestampyyyyMMddHHmmss(OrgDate));
//		}
//	}
//
//	public static String buildMsgInvestToCoreISO8583(String originMsg) throws JsonProcessingException {
//		ObjectMapper mapper = new ObjectMapper();
//		JsonNode root = mapper.readTree(originMsg);
//		String oldDe011 = JsonUtil.getVal(root, "/body/iso8583/DE011_TRACE_NO").asText();
//
//		//set new de003
//		String de003 = JsonUtil.getVal(root, "/body/iso8583/DE003_PROC_CD").asText();
//		String newDe003 = "17" + de003.substring(2);
//		JsonUtil.setVal(root, "/body/iso8583/DE003_PROC_CD", newDe003);
//
//		JsonUtil.setVal(root, "/body/iso8583/DE011_TRACE_NO", String.valueOf(RandomUtils.nextInt(100000, 999999)));
//		JsonUtil.setVal(root, "/body/iso8583/DE062_NAP_SVC_CD", "IF_INW");
//
////        #13 for Original Trx(4Bytes)+ #41 for Original Trx(8Bytes)
//		String orgDe013 = ACHUtil.subStringbyIndex(JsonUtil.getVal(root, "/body/iso8583/DE013_LOC_TRN_DATE").asText(), 4);
//		String orgDe041 = ACHUtil.subStringbyIndex(JsonUtil.getVal(root, "/body/iso8583/DE041_CRD_ACPT_TRM").asText(), 8);
//		JsonUtil.setVal(root, "/body/iso8583/DE048_ADD_PRV_INF", orgDe013 + orgDe041);
//
//		// set de90
//		String mti = JsonUtil.getVal(root, "/body/iso8583/MTI").asText();
//		String de007 = JsonUtil.getVal(root, "/body/iso8583/DE007_TRN_DT").asText();
//		String de032 = "00000" + JsonUtil.getVal(root, "/body/iso8583/DE032_ACQ_CD").asText();
//		String de033 = "00000000000";
//		String de090 = mti + oldDe011 + de007 + de032 + de033;
//		JsonUtil.setVal(root, "/body/iso8583/DE090_ORG_TRN_KEY", de090);
//
//		Date currentDate = new Date();
//		JsonUtil.setVal(root , "/body/iso8583/DE012_LOC_TRN_TIME" , DateUtil.formatHHmmss(currentDate));
//		JsonUtil.setVal(root , "/body/iso8583/DE013_LOC_TRN_DATE" , DateUtil.formatMMdd(currentDate));
//
//
//		return root.toPrettyString();
//
//	}
//
//	public static void main(String[] args) {
//		String test = "";
//
//		System.out.println("TEST: " + buildPacs004(null, "1", "2",
//				test, "", "00", "desc err", "3") );
//
//	}

}
