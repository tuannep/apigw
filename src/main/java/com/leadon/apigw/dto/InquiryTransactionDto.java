package com.leadon.apigw.dto;

public class InquiryTransactionDto {
	private String transId;
	private String errorCode;
	private String errorDesc;
	private String orgXrefId;
	private String debitAccountNo;
	private String creditAccountNo;
	private String amount;
	private String transCode;
	private String transDesc;
	private String transCodeDetail;
	private String transDescDetail;
	private String transDate;
	private String cdtrMemId;
	
	
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getErrorDesc() {
		return errorDesc;
	}
	public void setErrorDesc(String errorDesc) {
		this.errorDesc = errorDesc;
	}
	
	public String getOrgXrefId() {
		return orgXrefId;
	}
	public void setOrgXrefId(String orgXrefId) {
		this.orgXrefId = orgXrefId;
	}
	
	public String getDebitAccountNo() {
		return debitAccountNo;
	}
	public void setDebitAccountNo(String debitAccountNo) {
		this.debitAccountNo = debitAccountNo;
	}
	public String getCreditAccountNo() {
		return creditAccountNo;
	}
	public void setCreditAccountNo(String creditAccountNo) {
		this.creditAccountNo = creditAccountNo;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getTransCode() {
		return transCode;
	}
	public void setTransCode(String transCode) {
		this.transCode = transCode;
	}
	public String getTransDesc() {
		return transDesc;
	}
	public void setTransDesc(String transDesc) {
		this.transDesc = transDesc;
	}
	public String getTransCodeDetail() {
		return transCodeDetail;
	}
	public void setTransCodeDetail(String transCodeDetail) {
		this.transCodeDetail = transCodeDetail;
	}
	public String getTransDescDetail() {
		return transDescDetail;
	}
	public void setTransDescDetail(String transDescDetail) {
		this.transDescDetail = transDescDetail;
	}
	public String getTransDate() {
		return transDate;
	}
	public void setTransDate(String transDate) {
		this.transDate = transDate;
	}
	public String getCdtrMemId() {
		return cdtrMemId;
	}
	public void setCdtrMemId(String cdtrMemId) {
		this.cdtrMemId = cdtrMemId;
	}
	
}
