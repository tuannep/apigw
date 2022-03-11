package com.leadon.apigw.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class DAS {
	private String msgType;
	private String processingCode;
	private String transAmount;
	private String transmissionDateTime;
	private String systemTraceAuditNum;
	private String localTime;
	private String localDate;
	private String settlementDate;
	private String retRefNumber;
	private String authIdResponse;
	private String responseCode;
	private String cardAcceptorTerminalId;
//	private String cardAcceptorId;
//	private String cardAcceptorNameLocation;
	private String additionalDataPrivate;
	private String transCurrencyCode;
	private String usrDefinedField;
	private String serviceCode;
	private String transRefNumber;
	private String senderAcc;
	private String receiverAcc;
	private String contentTransfers;
	private String accountHolderName;
	@JsonProperty("PAN")
	private String PAN;
	@JsonProperty("MAC")
	private String MAC;
	private String sendingMember;
//	private String forwardingmember;
	private String receivingMember;

//	public String getMsgType() {
//		return msgType;
//	}
//	public void setMsgType(String msgType) {
//		this.msgType = msgType;
//	}
//	public String getProcessingCode() {
//		return processingCode;
//	}
//	public void setProcessingCode(String processingCode) {
//		this.processingCode = processingCode;
//	}
//	public String getTransAmount() {
//		return transAmount;
//	}
//	public void setTransAmount(String transAmount) {
//		this.transAmount = transAmount;
//	}
//	public String getTransmissionDateTime() {
//		return transmissionDateTime;
//	}
//	public void setTransmissionDateTime(String transmissionDateTime) {
//		this.transmissionDateTime = transmissionDateTime;
//	}
//	public String getSystemTraceAuditNum() {
//		return systemTraceAuditNum;
//	}
//	public void setSystemTraceAuditNum(String systemTraceAuditNum) {
//		this.systemTraceAuditNum = systemTraceAuditNum;
//	}
//	public String getLocalTime() {
//		return localTime;
//	}
//	public void setLocalTime(String localTime) {
//		this.localTime = localTime;
//	}
//	public String getLocalDate() {
//		return localDate;
//	}
//	public void setLocalDate(String localDate) {
//		this.localDate = localDate;
//	}
//	public String getSettlementDate() {
//		return settlementDate;
//	}
//	public void setSettlementDate(String settlementDate) {
//		this.settlementDate = settlementDate;
//	}
//	public String getRetRefNumber() {
//		return retRefNumber;
//	}
//	public void setRetRefNumber(String retRefNumber) {
//		this.retRefNumber = retRefNumber;
//	}
//	public String getAuthIdResponse() {
//		return authIdResponse;
//	}
//	public void setAuthIdResponse(String authIdResponse) {
//		this.authIdResponse = authIdResponse;
//	}
//	public String getResponseCode() {
//		return responseCode;
//	}
//	public void setResponseCode(String responseCode) {
//		this.responseCode = responseCode;
//	}
//	public String getCardAcceptorTerminalId() {
//		return cardAcceptorTerminalId;
//	}
//	public void setCardAcceptorTerminalId(String cardAcceptorTerminalId) {
//		this.cardAcceptorTerminalId = cardAcceptorTerminalId;
//	}
////	public String getCardAcceptorId() {
////		return cardAcceptorId;
////	}
////	public void setCardAcceptorId(String cardAcceptorId) {
////		this.cardAcceptorId = cardAcceptorId;
////	}
////	public String getCardAcceptorNameLocation() {
////		return cardAcceptorNameLocation;
////	}
////	public void setCardAcceptorNameLocation(String cardAcceptorNameLocation) {
////		this.cardAcceptorNameLocation = cardAcceptorNameLocation;
////	}
//	public String getAdditionalDataPrivate() {
//		return additionalDataPrivate;
//	}
//	public void setAdditionalDataPrivate(String additionalDataPrivate) {
//		this.additionalDataPrivate = additionalDataPrivate;
//	}
//	public String getTransCurrencyCode() {
//		return transCurrencyCode;
//	}
//	public void setTransCurrencyCode(String transCurrencyCode) {
//		this.transCurrencyCode = transCurrencyCode;
//	}
//	public String getUsrDefinedField() {
//		return usrDefinedField;
//	}
//	public void setUsrDefinedField(String usrDefinedField) {
//		this.usrDefinedField = usrDefinedField;
//	}
//	public String getServiceCode() {
//		return serviceCode;
//	}
//	public void setServiceCode(String serviceCode) {
//		this.serviceCode = serviceCode;
//	}
//	public String getTransRefNumber() {
//		return transRefNumber;
//	}
//	public void setTransRefNumber(String transRefNumber) {
//		this.transRefNumber = transRefNumber;
//	}
//	public String getSenderAcc() {
//		return senderAcc;
//	}
//	public void setSenderAcc(String senderAcc) {
//		this.senderAcc = senderAcc;
//	}
//	public String getReceiverAcc() {
//		return receiverAcc;
//	}
//	public void setReceiverAcc(String receiverAcc) {
//		this.receiverAcc = receiverAcc;
//	}
//	public String getContentTransfers() {
//		return contentTransfers;
//	}
//	public void setContentTransfers(String contentTransfers) {
//		this.contentTransfers = contentTransfers;
//	}
//	public String getAccountHolderName() {
//		return accountHolderName;
//	}
//	public void setAccountHolderName(String accountHolderName) {
//		this.accountHolderName = accountHolderName;
//	}
//	public String getPAN() {
//		return PAN;
//	}
//	public void setPAN(String pAN) {
//		PAN = pAN;
//	}
//	public String getMAC() {
//		return MAC;
//	}
//	public void setMAC(String mAC) {
//		MAC = mAC;
//	}
//	public String getSendingMember() {
//		return sendingMember;
//	}
//	public void setSendingMember(String sendingMember) {
//		this.sendingMember = sendingMember;
//	}
////	public String getForwardingmember() {
////		return forwardingmember;
////	}
////	public void setForwardingmember(String forwardingmember) {
////		this.forwardingmember = forwardingmember;
////	}
//
//	public String getReceivingMember() {
//		return receivingMember;
//	}
//	public void setReceivingMember(String receivingMember) {
//		this.receivingMember = receivingMember;
//	}

}
