package com.leadon.apigw.service.impl;

import com.leadon.apigw.constant.AppConstant;
import com.leadon.apigw.kafka.CustomKafkaMessage;
import com.leadon.apigw.repository.EventInfoRepository;
import com.leadon.apigw.service.KafkaProducerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


@Service("kafkaProducerService")
public class KafkaProducerServiceImpl implements KafkaProducerService {
	
	public static Logger logger = LoggerFactory.getLogger(KafkaProducerServiceImpl.class);

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;
	
	@Autowired
	private KafkaTemplate<String, CustomKafkaMessage> customKafkaTemplate;

	@Autowired
	private EventInfoRepository eventInfoRepository;

	public void sendMessage(String message, String topic) {
		this.kafkaTemplate.send(topic, message);
	}

	public void sendMessage(CustomKafkaMessage message, String topic) {
		this.customKafkaTemplate.send(topic, message);
	}
	
	@Override
	public void pushMsgLogReq(String transId, String msg, String fromSys, String toSys, String category) {
		try {
			CustomKafkaMessage kafkaMessReq = new CustomKafkaMessage();
			kafkaMessReq.setTransId(transId);
			kafkaMessReq.setMessage(msg);
			kafkaMessReq.setMsgType(AppConstant.LogConfig.REQUEST);
			kafkaMessReq.setFromSys(fromSys);
			kafkaMessReq.setToSys(toSys);
			kafkaMessReq.setCategory(category);
			this.sendMessage(kafkaMessReq, AppConstant.QueueConfig.TOPIC_LOG_MSG);
		} catch (Exception e) {
			logger.error("Exception when handle pushMsgLogReq:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushMsgLogReq" , e);
		}
	}

	@Override
	public void pushMsgLogRes(String transId, String msg, String fromSys, String toSys, String category) {
		try {
			CustomKafkaMessage kafkaMessRes = new CustomKafkaMessage();
			kafkaMessRes.setTransId(transId);
			kafkaMessRes.setMessage(msg);
			kafkaMessRes.setMsgType(AppConstant.LogConfig.RESPONSE);
			kafkaMessRes.setFromSys(fromSys);
			kafkaMessRes.setToSys(toSys);
			kafkaMessRes.setCategory(category);

			this.sendMessage(kafkaMessRes, AppConstant.QueueConfig.TOPIC_LOG_MSG);
		} catch (Exception e) {
			logger.error("Exception when handle pushMsgLogRes:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushMsgLogRes" , e);
		}
	}

	@Override
	public void pushPacs008(String transId, String msg) {
		try {
			CustomKafkaMessage kafkaMessRes = new CustomKafkaMessage();
			kafkaMessRes.setTransId(transId);
			kafkaMessRes.setMessage(msg);
			this.sendMessage(kafkaMessRes, AppConstant.QueueConfig.TOPIC_NRT_IN_PACS008);
		} catch (Exception e) {
			logger.error("Exception when handle pushPacs008:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushMsgLogRes" , e);
		}
		
	}

	@Override
	public void pushInvestNrtISO8583(String transId , String msg) {
		try {
			CustomKafkaMessage kafkaMessage = new CustomKafkaMessage();
			kafkaMessage.setTransId(transId);
			kafkaMessage.setMessage(msg);
			this.sendMessage(kafkaMessage , AppConstant.QueueConfig.TOPIC_INVEST_NRT_TO_CORE);
		} catch (Exception e) {
			logger.error("Exception when handle invest nrt to core:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushMsgLogRes" , e);
		}
	}

	@Override
	public void pushInvestInRecon(String transId, String msg, Long achReconResultId) {
		try {
			CustomKafkaMessage kafkaMessage = new CustomKafkaMessage();
			kafkaMessage.setTransId(transId);
			kafkaMessage.setMessage(msg);
			kafkaMessage.setAchReconResultId(achReconResultId);
			this.sendMessage(kafkaMessage , AppConstant.QueueConfig.TOPIC_INVEST_IN_RECON);
		} catch (Exception e) {
			logger.error("Exception when handle invest recon to core:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushInvestInRecon" , e);
		}
	}

	@Override
	public void pushIso8583Message(String transId, String orgSenderRefId,  String msgIso8583, String msgPacs008,  String msgType, String fromSys, String toSys, String category) {
		try {
			CustomKafkaMessage kafkaMessReq = new CustomKafkaMessage();
			kafkaMessReq.setTransId(transId);
			kafkaMessReq.setMessage(msgIso8583);
			kafkaMessReq.setMsgPacs008(msgPacs008);
			kafkaMessReq.setMsgType(msgType);
			kafkaMessReq.setFromSys(fromSys);
			kafkaMessReq.setToSys(toSys);
			kafkaMessReq.setCategory(category);
			kafkaMessReq.setOrgSenderRefId(orgSenderRefId);
			this.sendMessage(kafkaMessReq, AppConstant.QueueConfig.TOPIC_ACH_IN_NRT_RETURN);
		} catch (Exception e) {
			logger.error("Exception when handle pushIso8583Message:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushIso8583Message" , e);
		}
	}

	@Override
	public void pushAchCustomerInfo(String msgIso8583) {
		try {
			CustomKafkaMessage kafkaMessReq = new CustomKafkaMessage();
			kafkaMessReq.setMessage(msgIso8583);

			this.sendMessage(kafkaMessReq, AppConstant.QueueConfig.TOPIC_INQUIRY_SAVE_INFO);
		} catch (Exception e) {
			logger.error("Exception when handle pushAchCustomerInfo:" + e.getMessage());
			eventInfoRepository.insetLog("KafkaProducerServiceImpl.pushAchCustomerInfo" , e);
		}
	}
}

