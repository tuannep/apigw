package com.leadon.apigw.service;

import com.leadon.apigw.kafka.CustomKafkaMessage;


public interface KafkaProducerService {

	public void sendMessage(String message, String topic);

	public void sendMessage(CustomKafkaMessage message, String topic);

	public void pushMsgLogReq(String transId, String msg, String fromSys, String toSys, String category);

	public void pushMsgLogRes(String transId, String msg, String fromSys, String toSys, String category);

	public void pushPacs008(String transId, String msg);

	void pushInvestNrtISO8583(String transId , String msg);

	void pushInvestInRecon(String transId , String msg , Long achReconResultId);

	public void pushIso8583Message(String transId, String orgSenderRefId,  String msgIso8583, String msgPacs008,  String msgType, String fromSys, String toSys, String category);

	public void pushAchCustomerInfo( String msgIso8583);
}
