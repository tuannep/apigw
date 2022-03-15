package com.leadon.apigw.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.leadon.apigw.model.DataObj;
import org.springframework.data.domain.Page;


public interface TransactionService {
	
	public DataObj handleAckNack(JsonNode root, String message);
	
//	public DataObj handleCamt025(JsonNode root, String message);
//
//	public DataObj handleAdmi002(JsonNode root, String message);

	public DataObj handlePacs002(JsonNode root, String message);

//	public DataObj responsePayment(ManualResponsePaymentDto resPayDto);
//
//	Page<Transaction> selectTranByServiceGroupAndServiceId(GetTranDto getTranDto);

}
