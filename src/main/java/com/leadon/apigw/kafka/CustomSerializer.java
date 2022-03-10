package com.leadon.apigw.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CustomSerializer implements Serializer<CustomKafkaMessage> {

	public static Logger logger = LoggerFactory.getLogger(CustomSerializer.class);

	@Override
	public byte[] serialize(String arg0, CustomKafkaMessage arg1) {
		byte[] retVal = null;
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			retVal = objectMapper.writeValueAsString(arg1).getBytes();
		} catch (Exception e) {
			logger.error("Exception when handle serialize:" + e.getMessage());
		}
		return retVal;
	}


}
