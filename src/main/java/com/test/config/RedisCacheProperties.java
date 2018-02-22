package com.test.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(value = "test.redisCache")
public class RedisCacheProperties {
	private Map<String, Long> timeout = new HashMap<>();

	public Map<String, Long> getTimeout() {
		return timeout;
	}

	public void setTimeout(Map<String, Long> timeout) {
		this.timeout = timeout;
	}
}
