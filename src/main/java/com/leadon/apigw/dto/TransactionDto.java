package com.leadon.apigw.dto;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class TransactionDto extends AbstractDto{
	
	private static final long serialVersionUID = 38177486274809987L;
	
	private Long transId;
	private Long orgTransId;
	private String xrefId;
	private String clientId;
	private String channelId;
	private String serviceGroupId;
	private String serviceId;
	private String transCate;
	private String transInOut;
	private String transDesc;
	private String amount;
	private String ccy;
	private String transDt;
	private String transStat;
	private String transStatDesc;
    private String createdOn;
    private String createdBy;
    private String modifiedOn;
    private String modifiedBy;
    private String dbtrBrnl;
    private String dbtrAcctNo;
    private String cdtrAcctNo;
    private String trnRefNo;
    private String transType;
    private String orgSenderRefId;
    private String senderRefId;
    private String messageIdentifier;
    private String msgType;
    private String msgContent;
    private String msgDt;
    private String errorCode;
    private String errorDesc;
    private String refSenderRefId;
    private String vatAmount;
    private String feeAmount;
    private String settleDt;
    private String instrId;
    private String endtoendId;
    private String txId;
    private String chargeBr;
    private String transStep;
    private String transStepStat;
    private String groupStatus;
    private String sessionNo;
    private String cdtrBrn;
}
