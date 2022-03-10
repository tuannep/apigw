package com.leadon.apigw.model;

import lombok.*;

import javax.persistence.*;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "TRANS_MESSAGE_ISO8583")
public class TransMessageIso8583 extends AbstractModel {

	private static final long serialVersionUID = 608866742902774422L;
	
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "MSG_ISO8583_GEN")
    @SequenceGenerator(sequenceName = "SEQ_TRANS_MESSAGE_ISO8583", allocationSize = 1, name = "MSG_ISO8583_GEN")
	@Column(name = "MSG_ID")
    private Long msgId;
	
	@Column(name = "TRANS_ID")
    private Long transId;
	
	@Column(name = "SENDER")
	private String sender;
	
	@Column(name = "RECEIVER")
	private String receiver;
	
	@Column(name = "CATEGORY")
	private String category;
	
	@Column(name = "MSG_TYPE")
	private String msgType;
	
	@Lob
	@Column(name = "MSG_CONTENT")
	private String msgContent;
	
	@Column(name = "CREATED_ON")
    private Date createdOn;

	@Column(name = "GLOBAL_ID")
	private String globalId;

	@Column(name = "ORG_SENDER_REF_ID")
	private String orgSenderRefId;

	@Column(name = "MTI")
	private String mti;

	@Column(name = "DE002_PAN")
	private String de002Pan;

	@Column(name = "DE003_PROC_CD")
	private String de003ProcCd;

	@Column(name = "DE004_TRN_AMT")
	private String de004TrnAmt;

	@Column(name = "DE005_STL_AMT")
	private String de005StlAmt;

	@Column(name = "DE006_BIL_AMT")
	private String de006BilAmt;

	@Column(name = "DE007_TRN_DT")
	private String de007TrnDt;

	@Column(name = "DE009_STL_CONV_RT")
	private String de009StlConvRt;

	@Column(name = "DE010_BIL_CONV_RT")
	private String de010BilConvRt;

	@Column(name = "DE011_TRACE_NO")
	private String de011TraceNo;

	@Column(name = "DE012_LOC_TRN_TIME")
	private String de012LocTrnTime;

	@Column(name = "DE013_LOC_TRN_DATE")
	private String de013locTrnDate;

	@Column(name = "DE015_STL_DATE")
	private String de015StlDate;

	@Column(name = "DE018_MER_CAT_CD")
	private String de018MerCatCd;

	@Column(name = "DE019_ACQ_CTRY_CD")
	private String de019AcqCtryCd;

	@Column(name = "DE022_POS_MODE")
	private String de022PosMode;

	@Column(name = "DE023_CRD_SEQ_NO")
	private String de023CrdSeqNo;

	@Column(name = "DE025_POS_COND_CD")
	private String de025PosCondCd;

	@Column(name = "DE026_PIN_CAP_CD")
	private String de026PinCapCd;

	@Column(name = "DE032_ACQ_CD")
	private String de032AcqCd;

	@Column(name = "DE035_TRK2_DAT")
	private String de035Trk2Dat;

	@Column(name = "DE036_TRK3_DAT")
	private String de036Trk3Dat;

	@Column(name = "DE037_REL_REF_NO")
	private String de037RelRefNo;

	@Column(name = "DE038_AUTH_ID_RES")
	private String de038AuthIdRes;

	@Column(name = "DE039_RES_CD")
	private String de039ResCd;

	@Column(name = "DE041_CRD_ACPT_TRM")
	private String de041CrdAcptTrm;

	@Column(name = "DE042_CRD_ACPT_ID")
	private String de042CrdAcptId;

	@Column(name = "DE043_CRD_ACPT_LOC")
	private String de043CrdAcptLoc;

	@Column(name = "DE045_TRK1_DAT")
	private String de045Trk1Dat;

	@Column(name = "DE048_ADD_PRV_INF")
	private String de048AddPrvInf;

	@Column(name = "DE049_TRN_CCY")
	private String de049TrnCcy;

	@Column(name = "DE050_STL_CCY")
	private String de050StlCcy;

	@Column(name = "DE051_BIL_CCY")
	private String de051BilCcy;

	@Column(name = "DE052_PIN")
	private String de052Pin;

	@Column(name = "DE054_ADD_AMT")
	private String de054AddAmt;

	@Column(name = "DE055_EMV_DAT")
	private String de055EmvDat;

	@Column(name = "DE060_CNL_TP")
	private String de060CnlTp;

	@Column(name = "DE062_NAP_SVC_CD")
	private String de062NapSvcCd;

	@Column(name = "DE063_TRN_REF_NO")
	private String de063TrnRefNo;

	@Column(name = "DE070_NET_MGT_CD")
	private String de070NetMgtCd;

	@Column(name = "DE090_ORG_TRN_KEY")
	private String de090OrgTrnKey;

	@Column(name = "DE100_BEN_CD")
	private String de100BenCd;

	@Column(name = "DE102_SND_ACC_INF")
	private String de102SndAccInf;

	@Column(name = "DE103_RCV_ACC_INF")
	private String de103RcvAccInf;

	@Column(name = "DE104_TRN_CONT")
	private String de104TrnCont;

	@Column(name = "DE105_NEW_PIN")
	private String de105NewPin;

	@Column(name = "DE120_BEN_INF")
	private String de120BenInf;

	@Column(name = "DE128_MAC_DAT")
	private String de128MacDat;

}
