package com.leadon.apigw.model;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
@Entity
@Table(name = "TRANS_ACH_DETAIL")
public class TransAchDetail extends AbstractModel {
	
	private static final long serialVersionUID = 8815691838465754493L;
	
	@Id
	@Column(name = "TRANS_ID")
    private Long transId;
	
	@Column(name = "ORG_TRANS_ID")
    private Long orgTransId;
	
	@Column(name = "SENDER_REF_ID")
	private String senderRefId;
	
	@Column(name = "ORG_SENDER_REF_ID")
	private String orgSenderRefId;
	
	@Column(name = "REF_SENDER_REF_ID")
	private String refSenderRefId;
	
	@Column(name = "MSG_IDENTIFIER")
	private String msgIdentifier;
	
	@Column(name = "CCY")
	private String ccy;
	
	@Column(name = "AMOUNT")
	private BigDecimal amount;
	
	@Column(name = "FEE_AMOUNT")
	private BigDecimal feeAmount;
	
	@Column(name = "VAT_AMOUNT")
	private BigDecimal vatAmount;
	
	@Column(name = "SETTLE_DT")
    private Date settleDt;
	
	@Column(name = "SETTLE_MTD")
	private String settleMtd;
	
	@Column(name = "INSTR_ID")
	private String instrId;
	
	@Column(name = "ENDTOEND_ID")
	private String endtoendId;
	
	@Column(name = "TX_ID")
	private String txId;
	
	@Column(name = "CHARGE_BR")
	private String chargeBr;
	
	@Column(name = "DBTR_BRN")
	private String dbtrBrn;
	
	@Column(name = "DBTR_NAME")
	private String dbtrName;
	
	@Column(name = "DBTR_ADDRESS")
	private String dbtrAddress;
	
	@Column(name = "DBTR_ACCT_TYPE")
	private String dbtrAcctType;
	
	@Column(name = "DBTR_ACCT_NO")
	private String dbtrAcctNo;
	
	@Column(name = "DBTR_MEM_ID")
	private String dbtrMemId;
	
	@Column(name = "DBTR_MEM_CODE")
	private String dbtrMemCode;
	
	@Column(name = "CDTR_BRN")
	private String cdtrBrn;
	
	@Column(name = "CDTR_NAME")
	private String cdtrName;
	
	@Column(name = "CDTR_ADDRESS")
	private String cdtrAddress;
	
	@Column(name = "CDTR_ACCT_TYPE")
	private String cdtrAcctType;
	
	@Column(name = "CDTR_ACCT_NO")
	private String cdtrAcctNo;
	
	@Column(name = "CDTR_MEM_ID")
	private String cdtrMemId;
	
	@Column(name = "CDTR_MEM_CODE")
	private String cdtrMemCode;
	
	@Column(name = "CORE_REF")
	private String coreRef;
	
	@Column(name = "TRN_REF_NO")
	private String trnRefNo;
	
	@Column(name = "TRANS_DETAIL")
	private String transDetail;
	
	@Column(name = "TRANS_STEP")
	private String transStep;
	
	@Column(name = "TRANS_STEP_STAT")
	private String transStepStat;
	
	@Column(name = "ERR_CODE")
	private String errCode;
	
	@Column(name = "ERR_DESC")
	private String errDesc;
	
	@Column(name = "SESSION_NO")
	private String sessionNo;
	
	@Column(name = "GROUP_STATUS")
	private String groupStatus;
	
	@Column(name = "IS_COPY")
	private String isCopy;
	
	@Column(name = "TRANS_TYPE")
	private String transType;

	@Column(name = "NUMBER_OF_TXS")
	private Integer numberOfTxs;
	
	@Column(name = "MAKER")
	private String maker;
	
	@Column(name = "MODIFIER")
	private String modifier;
	
	@Column(name = "CHECKER")
	private String checker;
	
	@Column(name = "CREATED_ON")
    private Date createdOn;
    
    @Column(name = "CREATED_BY")
    private String createdBy;
	
    @Column(name = "MODIFIED_ON")
    private Date modifiedOn;
    
    @Column(name = "MODIFIED_BY")
    private String modifiedBy;

}
