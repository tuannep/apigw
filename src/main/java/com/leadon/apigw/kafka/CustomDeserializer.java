package com.leadon.apigw.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CustomDeserializer implements Deserializer<CustomKafkaMessage> {

	public static Logger logger = LoggerFactory.getLogger(CustomDeserializer.class);

	@Override
	public CustomKafkaMessage deserialize(String arg0, byte[] arg1) {
		ObjectMapper mapper = new ObjectMapper();
		CustomKafkaMessage message = null;
		try {
			message = mapper.readValue(arg1, CustomKafkaMessage.class);
		} catch (Exception e) {
			logger.error("Exception when handle deserialize:" + e.getMessage());
		}
		return message;
	}

}
