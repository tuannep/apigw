package com.leadon.apigw.kafka;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@EqualsAndHashCode(callSuper = false)
public class CustomKafkaMessage {

	private String transId;
	private String fromSys;
	private String toSys;
	private String category;
	private String msgType;
	private String errCode;
	private String errDesc;
	private String message;
	private String senderRefId;
	private String orgSenderRefId;
	private String actStep;
	private String actDesc;
	private String msgIdr;
	private String authIdRes;
	private String orgTxId;
	private String orgSettleDate;
	private String orgSenderCode;
	private String orgMessage;
	private String msgPacs008;
	private String transDt;
	private String amount;
	private String ccy;
	private String description;
	private String traceNumber;
	private String orgXrefId;
	private String functionCode;
	private String checkInvest;
	private String chanelId;
	private String errException;
	private String creDt;
	private String newTransId; // for return pacs004
	private Long achReconResultId;
	private int maxRequest;
	private String caseId;

}
