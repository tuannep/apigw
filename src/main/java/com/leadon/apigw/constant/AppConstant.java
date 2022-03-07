package com.leadon.apigw.constant;

public class AppConstant {

	public static final String getChannelCode(String channelId) {
		switch (channelId) {
			case "ATM":
				return "01";
			case "COUNTER":
				return "02";
			case "POS":
				return "03";
			case "IB": // internet
				return "04";
			case "MB": // mobile
				return "05";
			case "SMS":
				return "06";
			case "OTHER":
				return "07";
			default:
				return "00";
		}
	}

	public enum LEVEL {
		LOW, NORMAL, HIGH, SEVERE
	}

	public static final class Common {
		private Common() {
		}

		public static final String CURRENCY = "VND";
		public static final String ORG_NAPAS = "NAPAS";
		public static final String ORG_BANK = "BANK";
		public static final String SENDER_ID = "970457"; //Update xx
		public static final String SENDER_CODE = "HVBKVNVN"; //Update
		public static final String SENDER_NAME = "WOORIBANK"; //Update

		public static final String DEFAULT = "DEFAULT";
		public static final String CALLBACK_CLIENT = "1";
		public static final String NOT_CALLBACK_CLIENT = "0";
		public static final String RECEIVE_NAME = "NAPAS";
		public static final String BIC_NAPAS = "NAPASVNV";

		public static final String ACCT = "20";
		public static final String CARD = "00";

		public static final String CARD_LENGTH = "16";
		public static final String ACCT_LENGHT = "10";

		public static final String IS_CARD_FLAG = "1";
		public static final String IS_ACCT_FLAG = "0";

		public static final String TRANS_TYPE_NRT = "0";
		public static final String TRANS_TYPE_BTCH = "1";

		public static final String ACCT_STT_OPEN = "0";
	}

	public static final class FunctionCode {
		private FunctionCode() {
		}

		public static final String SEND_INQ_INVES = "SEND_INVEST";
		public static final String GET_INQ_INVES = "GET_INVEST";
	}

	public static final class QueueConfig {
		private QueueConfig() {
		}

		public static final String TOPIC_NRT_IN_PACS008 = "ACH.NRT.IN.PACS008";
		public static final String TOPIC_ACH_IN_NRT_RETURN = "ACH.IN.NRT.RETURN";
		public static final String TOPIC_ACH_IN_REPORT = "ACH.IN.REPORT";
		public static final String TOPIC_ACH_IN_SEND = "ACH.IN.SEND";
		public static final String TOPIC_LOG_MSG = "ACH.MSG.LOG";
		public static final String TOPIC_NACK_IN_ISO8583 = "ACH.NACK.IN.ISO8583";
		public static final String TOPIC_INQUIRY_IN = "ACH.INQUIRY.IN";
		public static final String TOPIC_INVEST_NRT_TO_CORE = "INVEST.NRT.TO.CORE";
		public static final String TOPIC_RETURN_ISO8583_OUT = "ACH.RETURN.ISO8583.OUT";
		public static final String TOPIC_RETURN_PAYMENT_OUT = "ACH.RETURN.PAYMENT.OUT";
		public static final String TOPIC_INQUIRY_PAYMENT_OUT = "ACH.INQUIRY.PAYMENT.OUT";
		public static final String TOPIC_INVEST_IN_RECON = "TOPIC.INVEST.IN.RECON";
		public static final String TOPIC_COPY_MISSTRANS_IN = "TOPIC.COPY.MISSTRANS.IN";
		public static final String TOPIC_INQUIRY_SAVE_INFO = "TOPIC.INQUIRY.SAVE.INFO";

	}

	public static final class HTTPConfig {
		private HTTPConfig() {
		}

		public static final String GET_METHOD = "GET";
		public static final String POST_METHOD = "POST";
		public static final String PUT_METHOD = "PUT";

		public static final String HTTP_STATUS_200 = "200";
		public static final String HTTP_STATUS_404 = "404";
		public static final String HTTP_STATUS_5XX = "5xx";
	}

	public static final class ReportConfig {
		public static final String CAMT998_REPORT_MSG = "cma.reconciliationRep";

		private ReportConfig() {
		}
	}

	public static final class LimitConfig {
		private LimitConfig() {
		}

		public static final String IS_DEBIT_CAP = "PA";
		public static final String IS_SETTLE_LIMIT = "SLA";
		public static final String GET_METHOD = "GET";
		public static final String SET_METHOD = "SET";

	}

	public static final class CopyConfig {
		public static final String IS_TRANS_COPY = "1";

		private CopyConfig() {
		}
	}

	public static final class DisputeConfig {
		private DisputeConfig() {
		}

		public static final String CAMT998_DSPT_MSG = "cma.dsptMsg";

		public static final String TP_VALUE = "cma.dsptMsg.001.05";

		public static final class DisputeType {
			private DisputeType() {
			}

			public static final String DISP_EDIT_TYPE = "RQAD";
			public static final String DISP_RTN_TYPE = "RQRN";
			public static final String DISP_INFO_TYPE = "RQNF";
			public static final String DISP_SUPPORT_TYPE = "RQSP";
			public static final String DISP_FAITH_TYPE = "GDFT";
			public static final String DISPUTE_CLAR_FUNCTION = "CLAR";
		}

		public static final class DisputeStatus {
			private DisputeStatus() {
			}

			public static final String DISP_OPEN_STATUS = "OPEN";
			public static final String DISP_PROCESS_STATUS = "PRCD";
			public static final String DISP_REJECT_STATUS = "RJCT";
			public static final String DISP_EXPIRED_STATUS = "EXPI";
			public static final String DISP_CLOSED_STATUS = "CLSD";
		}

		public static final class DisputeTransCate {
			private DisputeTransCate() {
			}

			public static final String DISP_EDIT = "DISP_EDIT";
			public static final String DISP_RTN = "DISP_RTN";
			public static final String DISP_INFO = "DISP_INFO";
			public static final String DISP_SUPPORT = "DISP_SUPPORT";
			public static final String DISP_FAITH = "DISP_FAITH";
			public static final String DISP_CLAR = "DISP_CLAR";
		}

	}

	public static final class InquiryConfig {
		private InquiryConfig() {
		}

		public static final String FUNC_INQ_DAS = "INQ_DAS";
		public static final String FUNC_INQ_INVEST = "INQ_INVEST";
		public static final String FUNC_INQ_RESULT = "INQ_RESULT";
		public static final String TYPE_SINGLE = "SINGLE";
		public static final String TYPE_BATCH = "BATCH";

		public static final class Cas {
			private Cas() {
			}

		}

		public static final class Invest {
			private Invest() {
			}

			public static final String CHECK_BANK = "BANK";
			public static final String CHECK_NAPAS = "NAPAS";
			public static final String NUM_INQ_NAPAS = "3";
			public static final String NUM_INQ_BANK = "10";
			public static final String NUM_DAY_INQ = "10";
			public static final String INQ_TIME_BANK = "5";
			public static final String INQ_TIME_2_NAPAS = "900";
			public static final String INQ_TIME_3_NAPAS = "3600";

			public static final String RESPONSE_FOR_INVEST = "1";
			public static final String NOT_RESPONSE_FOR_INVEST = "2";
			public static final int MAX_TOLTAL_INVEST_TO_CORE_AUTO = 3;
			public static final int MAX_TOLTAL_COPY = 1;
			public static final int MAX_TOLTAL_INVEST_TO_CORE_AUTO_JOB = 3;
			public static final int MAX_TOLTAL_INVEST_TO_CORE_API = 7;
			public static final int MAX_TOLTAL_INVEST_TO_CORE_RECON_JOB = 9;

		}

		public static final class Das {
			private Das() {
			}

			public static final String DAS_CARD_CARD = "430000";
			public static final String DAS_CARD_ACCT = "430020";
			public static final String DAS_ACCT_CARD = "432000";
			public static final String DAS_ACCT_ACCT = "432020";

			public static final String MSG_TYPE_DAS = "0200";
			public static final String MSG_TYPE_DAS_RESP = "0210";
			public static final String TRANS_AMOUNT_DAS = "000000000000";
			public static final String POINT_SERVICE_ENTRY_CODE_DAS = "021";
			public static final String RET_REF_NUM_DAS = "906300145021"; //Update
			public static final String CARD_ACCEPTOR_ID_DAS = "NAME BANK"; //Update
			public static final String CARD_ACCEPTOR_NAME_LOCATION_DAS = "ACH BANKNAMEV                 HANOI       VNM"; //Update
			public static final String TRANS_CURRENCY_CODE_DAS = "704";
			public static final String USER_DEFINEED_FIELD_DAS = "02";
			public static final String SERVICE_CODE_DAS = "IF_INQ";
			public static final String CONTENT_TRANSFERS_DAS = "TRUY VAN THONG TIN";
			public static final String MAC_DAS = "0000F44D30E90F99";
			public static final String TRANS_DESC_DAS = "Gui ban tin truy van thong tin dinh danh(DAS)";

			public static final String DAS_ERROR_CODE_ACCT = "76";
			public static final String DAS_ERROR_CODE_CARD = "01";

			public static final String DAS_ERROR_CODE_FORMAT = "30";
			public static final String DAS_ERROR_CODE_SYSTEM = "68";
			public static final String DAS_ERROR_CODE_EXCEPTION = "68";

			public static final String INQ_DAS_FROM_NAME = "BANKNAME"; //Update
			public static final String INQ_DAS_FROM_ADDRESS = "BANKADDRESS"; //Update
		}
	}

	public static final class SystemResponse {
		private SystemResponse() {
		}

		public static final String STATUS_NOTFOUND = "NOTFOUND";
		public static final String STATUS_SUCCESS = "0";
		public static final String STATUS_UNSUCCESS = "1";
		public static final String SUCCESS_CODE = "00";
		public static final String SUCCESS_DESC = "Success";
		public static final String SYSTEM_ERROR_CODE = "68";
		public static final String SYSTEM_ERROR_DESC = "System Error";
		public static final String EXCEPRION_ERROR_CODE = "68";
		public static final String EXCEPRION_ERROR_DESC = "Exception Error";
		public static final String TIMEOUT_ERROR_CODE = "68";
		public static final String TIMEOUT_ERROR_DESC = "Timeout Error";
		public static final String DUPLICATE_XREF_ID_CODE = "30";
		public static final String DUPLICATE_XREF_ID_DESC = "Duplicate Xref Id";
		public static final String NAPAS_RESPONSE_CODE_SUCCESS = "01";
		public static final String SYSTEM_ERROR_EXCEPTION_CODE = "96";
	}

	public static final class SrvGroup {
		private SrvGroup() {
		}

		public static final String ACH_INQ = "ACH_INQ";
		public static final String ACH_NRT = "ACH_NRT";
		public static final String ACH_LIMIT = "ACH_LIMIT";
		public static final String ACH_COPY = "ACH_COPY";
		public static final String ACH_DISP = "ACH_DISP";
		public static final String ACH_RTN = "ACH_RTN";
		public static final String ACH_RES_PAYMENT = "ACH_RES_PAYMENT";

	}

	public static final class ServiceId {
		private ServiceId() {
		}

		public static final String SERVICE_INQ_DAS = "INQ_DAS";
		public static final String SERVICE_INQ_INVEST = "INQ_INVEST";
		public static final String SERVICE_NRT_IN = "ACH_NRT_IN";
		public static final String SERVICE_NRT_OUT = "ACH_NRT_OUT";
		public static final String SERVICE_LIMIT_GET = "LIMIT_GET";
		public static final String SERVICE_LIMIT_SET = "LIMIT_SET";
		public static final String SERVICE_COPY_PAYMENT = "COPY_PAYMENT";
		public static final String SERVICE_RESPONSE_PAYMENT = "RES_PAYMENT";
		public static final String SERVICE_DISPUTE_IN = "DISPUTE_IN";
		public static final String SERVICE_NRT_RETURN = "ACH_NRT_RETURN";
		public static final String SERVICE_COPY_NRT_NOTEQ_CRRDATE = "IF_COPY";
		public static final String SERVICE_COPY_NRT_EQUAL_CRRDATE = "IF_ACH";
		public static final String SERVICE_COPY_RETURN = "IF_RQRN";
		public static final String SERVICE_RETURN = "IF_RQRN";

	}

	public static final class TransCate {
		private TransCate() {
		}

		public static final String ACH_INVEST = "INVEST";
		public static final String ACH_SET_LIMIT = "SET_LIMIT_SETTLE";
		public static final String ACH_GET_LIMIT = "GET_LIMIT_DEBIT";
		public static final String ACH_COPY_PAYMENT = "COPY_PAYMENT";
		public static final String ACH_RTN_NRT_PAYMENT = "NRT_RETURN";
		public static final String ACH_RESPONSE_PAYMENT = "RES_PAYMENT";
		public static final String BANK_PAYMENT = "91";
	}

	public static final class PacsStatus {
		private PacsStatus() {
		}

		public static final String ACH_PREFIX_CODE = "/AIR/";
		public static final String ACH_PREFIX_DESC = "/FAI/";
		public static final String ACH_INDENT_ERROR_CODE = "/RCD/";
		public static final String ACH_INDENT_ERROR_DESC = "/DSC/";
	}

	public static final class SenderRefType {
		private SenderRefType() {
		}

		public static final String SENDER_REF_DAS = "DAS";
		public static final String SENDER_REF_SPEC = "SPEC";
		public static final String SENDER_REF_NORMAL = "NORMAL";

	}

	public static final class LogConfig {
		private LogConfig() {
		}

		public static final String CATEGORY_NAPAS = "NAPAS";
		public static final String CATEGORY_INTERNAL = "INTERNAL";
		public static final String REQUEST = "Request";
		public static final String RESPONSE = "Response";
		public static final String BANK = "BANK";
		public static final String NAPAS = "NAPAS";
		public static final String COREBANKING = "COREBANK";
		public static final String CARDSYSTEM = "CARDSYS";
		public static final String CATEGORY_EXTERNAL = "EXTERNAL";
	}

	public static final class MethodType {
		private MethodType() {
		}

		public static final String CARD_TYPE = "PAN";
		public static final String ACCT_TYPE = "ACC";

	}

	public static final class PacsCommonConfig {
		private PacsCommonConfig() {
		}

		public static final String HEADER_FORMAT = "MX";
		public static final String SENDER_ID = "970457";
		public static final String SENDER_CODE = "HVBKVNVN";
		public static final String SENDER_CODE_URL = "hvbkvnvn";
		public static final String SENDER_NAME = "WOORIBANK";
		public static final String RECEIVER_ID = "NAPASVNV";
		public static final String RECEIVER_CODE = "NAPASVNV";
		public static final String RECEIVER_NAME = "Napas";
		public static final String PAYLOAD_BIZSVC = "ACH";
		public static final String PAYLOAD_STTLM = "CLRG";
		public static final String PAYLOAD_CLRCHANL = "RTNS";
		public static final String PAYLOAD_LCLINSTRM = "CSDC";
		public static final String PAYLOAD_CHRGBR = "SLEV";
		public static final String PAYLOAD_SVCLVL = "0100";
		public static final String PAYLOAD_CTGYPURP = "001";
		public static final String PAYLOAD_USTRD = "Payment";

	}

	public static final class TimeFormat {
		public static final String FORMAT_FULL = "yyyy-MM-dd'T'HH:mm:ssXXX";
		public static final String FORMAT_Z = "yyyy-MM-dd'T'HH:mm:ss'Z'";
		public static final String FORMAT_DATE = "yyyy-MM-dd";
		public static final String FORMAT_SUB = "MMddHHmmss";
		public static final String FORMAT_SUB_FULL = "yyyyMMddHHmmss";
		public static final String FORMAT_SUB_1 = "MMdd";
		public static final String FORMAT_SUB_2 = "HHmmss";

		private TimeFormat() {
		}
	}

	public static final class MsgIdr {
		private MsgIdr() {
		}

		public static final String PACS008 = "pacs.008.001.07";
		public static final String PACS002 = "pacs.002.001.09";
		public static final String ADMI002 = "admi.002.001.01";
		public static final String PACS004 = "pacs.004.001.08";
		public static final String CAMT025 = "camt.025.001.04";
		public static final String PACS028 = "pacs.028.001.02";
		public static final String CAMT009 = "camt.009.001.06";
		public static final String CAMT010 = "camt.010.001.07";
		public static final String CAMT011 = "camt.011.001.06";
		public static final String CAMT052 = "camt.052.001.07";
		public static final String CAMT998 = "camt.998.001.03";
		public static final String CAMT998_DISPUTE = "camt.998.001.03";
		public static final String CAMT053 = "camt.053.001.07";
		public static final String CAMT034 = "camt.034.001.05";
		public static final String CAMT033 = "camt.033.001.05";
		public static final String ACKNACK = "acknack";
		public static final String DAS = "DAS";
		public static final String ISO8583 = "ISO8583";

	}

	public static final class MsgDesc {
		private MsgDesc() {
		}

		public static final String DESC_ACKNACK = "System receive response message acknack from NAPAS";
		public static final String DESC_CAMT025 = "System receive Response Msg camt.025.001.04 from NAPAS";
		public static final String DESC_ADMI = "System receive Response Msg admi.002.001.01 from NAPAS";
		public static final String DESC_PACS002 = "System receive Response Msg pacs.002.001.09 from NAPAS";
		public static final String DESC_CAMT998_DPT = "System receive Response Msg camt.998.001.03 dispute from NAPAS";
		public static final String DESC_CAMT010 = "System receive Response Msg camt.010.001.07 from NAPAS";
		public static final String DESC_CAMT034 = "System receive Response Msg camt.034.001.05 from NAPAS";
		public static final String DESC_PACS004_COPY = "System receive Request MsgIn pacs.004.001.08 copy from NAPAS";
		public static final String DESC_PACS004 = "System receive Request MsgIn pacs.004.001.08 from NAPAS";
		public static final String DESC_PACS008_COPY = "System receive Request MsgIn pacs.008.001.07 copy from NAPAS";
		public static final String DESC_PACS008 = "System receive Request MsgIn pacs.008.001.07 from NAPAS";
		public static final String DESC_PACS028 = "System receive Request MsgIn pacs.028.001.02 from NAPAS";
		public static final String DESC_DAS = "System receive Request MsgIn DAS from NAPAS";
		public static final String DESC_CONTENT_DEFAULT = "woori bank";

	}

	public static final class TransStep {
		private TransStep() {
		}

		public static final String ACT_STEP_ACK = "ACK";
		public static final String ACT_STEP_NACK = "NAK";
		public static final String ACT_STEP_RECEIPT = "RECEIPT";
		public static final String ACT_STEP_REJECT = "REJECT";
		public static final String ACT_STEP_POSTED = "POSTED";
		public static final String ACT_STEP_UPD_NAUT_TIMEOUT = "UPDATE_NAUT_TIMEOUT";
		public static final String ACT_STEP_UPD_AUTH = "UPDATE_AUTH";
		public static final String ACT_STEP_UPD_NAUT = "UPDATE_NAUT";
		public static final String ACT_STEP_GET_LIMIT = "GET_LIMIT";
		public static final String ACT_STEP_SET_LIMIT = "SET_LIMIT";
		public static final String ACT_STEP_GET_SETTLEMENT_LIMIT = "GET_SETTLEMENT_LIMIT";
		public static final String ACT_STEP_SET_SETTLEMENT_LIMIT = "SET_SETTLEMENT_LIMIT";
		public static final String ACT_STEP_GET_DEBIT_CAP = "GET_DEBIT_CAP";
		public static final String ACT_STEP_SET_DEBIT_CAP = "SET_DEBIT_CAP";
		public static final String ACT_STEP_PUTMX = "PUTMX";
		public static final String ACT_STEP_UPDATE_PNDG = "UPDATE_PNDG";
		public static final String ACT_STEP_UPDATE_ACSP = "UPDATE_ACSP";
		public static final String ACT_STEP_UPDATE_PART = "UPDATE_PART";
		public static final String ACT_STEP_UPDATE_ACCP = "UPDATE_ACCP";
		public static final String ACT_STEP_COPY_PAYMENT = "COPY_PAYMENT";
		public static final String ACT_STEP_SEND_NRT = "SEND_NRT_ISO8583";
		public static final String ACT_STEP_SEND_NACK = "SEND_NACK_ISO8583";
		public static final String ACT_STEP_SEND_ADMI = "SEND_ADMI_ISO8583";
		public static final String ACT_STEP_SEND_CAMT025 = "SEND_CAMT025_ISO8583";
		public static final String ACT_STEP_SEND_PACS002 = "SEND_PACS002_ISO8583";
		public static final String ACT_STEP_SEND_PACS008 = "SEND_PACS008_ISO8583";
		public static final String ACT_STEP_SEND_PACS004 = "SEND_PACS004_ISO8583";

	}

	public static final class TransStepState {
		private TransStepState() {
		}

		public static final String TRANS_STEP_STATE_SUCCESS = "SUCCESS";
		public static final String TRANS_STEP_STATE_FAIL = "FAIL";

	}

	public static final class GroupStatus {
		private GroupStatus() {
		}

		public static final String GRPSTS_RJCT = "RJCT";
		public static final String GRPSTS_ACSP = "ACSP";
		public static final String GRPSTS_PNDG = "PNDG";
		public static final String GRPSTS_PART = "PART";
		public static final String GRPSTS_ACCP = "ACCP";
		public static final String GRPSTS_ACSC = "ACSC";

	}

	public static final class TxStatus {
		private TxStatus() {
		}

		public static final String TXSTS_ACCP = "ACCP";
		public static final String TXSTS_RJCT = "RJCT";
		public static final String TXSTS_PNDG = "PNDG";
		public static final String TXSTS_ACSP = "ACSP";
		public static final String TXSTS_ACSC = "ACSC";

	}

	public static final class StatusId {
		private StatusId() {
		}

		public static final String STSID_NOAN = "NOAN";
		public static final String STSID_AUTH = "AUTH";
		public static final String STSID_NAUT = "NAUT";

	}

	public static final class AchEcode {
		private AchEcode() {
		}

		public static final String INDENTIFICATION_ERROR_CODE = "/RCD/";
		public static final String INDENTIFICATION_ERROR_DESC = "/DSC/";

		public static final String ECODE_UNKONW = "68";
		public static final String ECODE_SUCCESS = "00";
		public static final String ECODE_PAYMENT_LIMIT_TIME = "28";
		public static final String ECODE_ACSP_NOAN_ACSP_P4 = "ACSP_NOAN_ACSP_P4";
		public static final String ECODE_SYSTEM_ERROR = "68";
		public static final String ECODE_RJCT_RJCT = "RJCT_RJCT";
		public static final String ECODE_ACCP_RJCT = "ACCP_RJCT";
		public static final String ECODE_ACCP_ACCP = "ACCP_ACCP";
		public static final String ECODE_PDNG_PDNG = "PDNG_PDNG";
		public static final String ECODE_ACSP_ACSP = "ACSP_ACSP";
		public static final String ECODE_PART_PDNG = "PART_PDNG";
		public static final String ECODE_PART_RJCT = "PART_RJCT";
		public static final String ECODE_PART_ACSP = "PART_ACSP";
		public static final String ECODE_BATCH_ACSP = "BATCH_ACSP";
		public static final String ECODE_BATCH_PNDG = "BATCH_PNDG";
		public static final String ECODE_BATCH_PART = "BATCH_PART";
		public static final String ECODE_BATCH_ACCP = "BATCH_ACCP";
		public static final String ECODE_BATCH_RJCT = "BATCH_RJCT";
		public static final String ECODE_ACK = "ACK";
		public static final String ECODE_NACK = "NACK";
		public static final String ECODE_ADMI = "ADMI";
		public static final String ECODE_CAMT025_RJCT = "CAMT025_RJCT";
		public static final String ECODE_ACSP_AUTH_ACSP = "ACSP_AUTH_ACSP";
		public static final String ECODE_ACSP_NOAN_ACSP = "ACSP_NOAN_ACSP";
		public static final String ECODE_RJCT_NOAN_RJCT = "RJCT_NOAN_RJCT";
		public static final String ECODE_ACSP_NAUT_ACSP = "ACSP_NAUT_ACSP";
		public static final String ECODE_RJCT_NAUT_RJCT = "RJCT_NAUT_RJCT";
		public static final String ECODE_ACSC_AUTH_ACSC = "ACSC_AUTH_ACSC";
		public static final String ECODE_ACSC_NAUT_ACSC = "ACSC_NAUT_ACSC";

		public static final String EDESC_SUCCESS = "Success";
		public static final String EDESC_PAYMENT_LIMIT_TIME = "The update date is past the allowed date";
		public static final String EDESC_ACSP_NOAN_ACSP_P4 = "Transaction is successful";
		public static final String EDESC_SYSTEM_ERROR = "System Error";
		public static final String EDESC_RJCT_RJCT = "Transactions declined";
		public static final String EDESC_ACCP_RJCT = "Transactions declined";
		public static final String EDESC_ACCP_ACCP = "Transaction is valid";
		public static final String EDESC_PDNG_PDNG = "Transaction into the queue";
		public static final String EDESC_ACSP_ACSP = "Transaction is successful";
		public static final String EDESC_PART_PDNG = "Transaction into the queue";
		public static final String EDESC_PART_RJCT = "Transaction failed";
		public static final String EDESC_PART_ACSP = "Transaction is successful";
		public static final String EDESC_BATCH_ACSP = "Batch transactions are posted and ready for settlement";
		public static final String EDESC_BATCH_PNDG = "Batch transactions are pending at NP";
		public static final String EDESC_BATCH_PART = "Batch transactions are partially processed";
		public static final String EDESC_BATCH_ACCP = "Batch transactions will handle in the future";
		public static final String EDESC_BATCH_RJCT = "Batch transactions declined";
		public static final String EDESC_ACSP_AUTH_ACSP = "Transaction is successful";
		public static final String EDESC_RJCT_NAUT_RJCT = "The transaction was rejected by the receiving organization";
		public static final String EDESC_ACK = "Napas accepts the transaction";
		public static final String EDESC_NACK = "Napas refused to receive the transaction";
		public static final String EDESC_ADMI = "Napas refused the transaction because the message was invalid";
		public static final String EDESC_CAMT025_RJCT = "Napas refused the transaction because it was invalid";
		public static final String EDESC_RJCT_NOAN_RJCT = "Napas refused the transaction because the message was invalid";
		public static final String EDESC_ACSP_NAUT_ACSP = "Transaction declined after timeout";
		public static final String EDESC_ACSP_NOAN_ACSP = "Transaction is timeout";
		public static final String EDESC_ACSC_AUTH_ACSC = "Transaction is successful";
		public static final String EDESC_ACSC_NAUT_ACSC = "Transaction declined after timeout";

	}

	public static final class ResponseType {
		private ResponseType() {
		}

		public static final String RESP_FAILURE_TYPE = "failure";
		public static final String RESP_SUCCESS_TYPE = "success";
		public static final String RESP_TIMEOUT_TYPE = "timeout";
	}

	public static final class ResponseMsg {
		private ResponseMsg() {
		}

		public static final String RESP_INVALID_MESSAGE = "Message is invalid or missing";
		public static final String RESP_SUCCESS_MESSAGE = "Message successfully processed";
		public static final String RESP_FAIL_MESSAGE = "Handle the message have error";
		public static final String RESP_TIMEOUT_MESSAGE = "The transaction is timeout expired";
		public static final String RESP_SIGNATURE_MESSAGE = "Data signature for the format is not available or invalid";
	}

	public static final class ResponseDupl {
		private ResponseDupl() {
		}

		public static final String RESP_DUPLICATED = "true";
		public static final String RESP_NOT_DUPLICATED = "false";
	}

	public static final class ChannelId {
		private ChannelId() {
		}

		public static final String ACH = "ACH";
		public static final String ACH_OUT = "ACH_OUT";
		public static final String ACH_IN = "ACH_IN";
	}

	public static final class TransDirection {
		private TransDirection() {
		}

		public static final String TRANS_OUT = "OUT";
		public static final String TRANS_IN = "IN";
	}

	public static final class ClientId {
		private ClientId() {
		}

		public static final String APP_SYSTEM = "NPGW";
	}

	public static final class JsonConfig {
		private JsonConfig() {
		}

		public static final String JSON_TEMP_PACS008 = "pacs008.json";
		public static final String JSON_TEMP_PACS004 = "pacs004.json";
		public static final String JSON_TEMP_PACS002_FAIL = "pacs002_fail.json";
		public static final String JSON_TEMP_PACS002_SUCCESS = "pacs002_success.json";
		public static final String JSON_TEMP_CAMT009 = "camt009.json";
		public static final String JSON_TEMP_CAMT011 = "camt011.json";
		public static final String JSON_TEMP_CAMT033 = "camt033.json";
		public static final String JSON_TEMP_PACS028 = "pacs028.json";
		public static final String JSON_TEMP_CAMT998_DISP = "camt998_Settle_Dispute.json";
		public static final String JSON_TEMP_DAS = "DAS.json";
		public static final String JSON_TEMP_ISO8583 = "ISO8583.json";
	}

	public static final class ACHService {
		private ACHService() {
		}

		public static final String PAYMENT_FOR_COPY = "paymentforcopy";
		public static final String GET_DEBIT_CAP = "getdebitcap";
		public static final String SET_DEBIT_CAP = "setdebitcap";
		public static final String DIRECT_CREDIT = "directcredit";
		public static final String RETURN = "return";
		public static final String CLARIFICATION_INFO = "clarificationinformation";
		public static final String REQUEST_ANAMENDMENT = "requestanamendment";
		public static final String REQUEST_FOR_RETURN = "requestforreturn";
		public static final String REQUEST_TO_PROVIDE_TRANS_INFO = "requesttoprovidetransactioninformation";
		public static final String SUPPORT_COLLECTION = "supportcollection";
		public static final String GOOD_FAITH = "goodfaith";
		public static final String DAS = "das";
		public static final String INVESTIGATION_TRANS = "investigationtransaction";

	}

	public static final class MsgBankIdr {
		private MsgBankIdr() {
		}

		public static final String PACS008_91 = "91";

	}

	public static final class JobName {
		public static final String RECONCILIATION = "Push data and auto reconciliation";
		public static final String SAVE_FILE_RECON = "Save file reconciliation";
		public static final String COPY_MISS_TRANS = "Copy miss transaction IN";
		public static final String INSERT_SESSION_ID = "Insert newest session Id";
		public static final String INVEST_NRT_TO_CORE = "Investigate NRT to core";

		private JobName() {
		}
	}

	public static final class TransType {
		public static final String DBIT = "DBIT"; //OUT
		public static final String CRDT = "CRDT"; //IN

		private TransType() {
		}
	}

	public static final class ReconFileType{
		public static final String TC = "TC"; //Thành công
		public static final String KTC = "KTC"; //Không thàng công
	}

}
