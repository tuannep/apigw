package com.leadon.apigw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.leadon.apigw.dto.InquiryTransactionDto;
import com.leadon.apigw.model.DAS;


public interface InquiryService {
	
	public String inquiryDAS(String iso8583Message);
	
	public InquiryTransactionDto inquiryTransactionNRT(String message);
	
//	public DataObj handlePacs028(JsonNode root, String message);

	public DAS inquiryDASInComing(String senderId, String senderRefId, String msgContent);

//	public DataObj inquiryTransactionNRTCore(String message);
}
