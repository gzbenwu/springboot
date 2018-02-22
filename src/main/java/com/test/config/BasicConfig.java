package com.test.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConfigurationProperties(value = "test")
@PropertySource("config-basic.properties")
@ImportResource(value = "classpath:spring-config.xml")
public class BasicConfig {
	private Map<String, Integer> httpclientservice = new HashMap<>();
	private Map<String, String> audit = new HashMap<>();
	private Map<String, String> corsdomain = new HashMap<>();
	private Map<String, String> httprequest = new HashMap<>();
	private Map<String, String> aep = new HashMap<>();

	@Value("${test.httpsession.sessionCookieKey}")
	private String sessionCookieKey;

	@Value("${test.httpsession.maxInactiveIntervalInSeconds}")
	private Integer maxInactiveIntervalInSeconds;

	@Value("${test.httpsession.cookieHttponly}")
	private boolean cookieHttponly;

	@Value("${test.httpsession.cookieSecure}")
	private boolean cookieSecure;

	public Map<String, Integer> getHttpclientservice() {
		return httpclientservice;
	}

	public Map<String, String> getAudit() {
		return audit;
	}

	public Map<String, String> getCorsdomain() {
		return corsdomain;
	}

	public Map<String, String> getHttprequest() {
		return httprequest;
	}

	public Map<String, String> getAep() {
		return aep;
	}

	public String getSessionCookieKey() {
		return sessionCookieKey;
	}

	public Integer getMaxInactiveIntervalInSeconds() {
		return maxInactiveIntervalInSeconds;
	}

	public boolean isCookieHttponly() {
		return cookieHttponly;
	}

	public boolean isCookieSecure() {
		return cookieSecure;
	}
}
