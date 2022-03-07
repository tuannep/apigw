package com.leadon.apigw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource("classpath:banks.properties")
@ConfigurationProperties
public class AppProperties {

	@Autowired
	private Environment env;
	
	public String getProperty(String pPropertyKey) {
		return env.getProperty(pPropertyKey); 
	}

}
