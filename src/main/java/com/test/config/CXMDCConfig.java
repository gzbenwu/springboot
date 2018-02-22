package com.test.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;

@Configuration
public class CXMDCConfig {

	@Bean
	public MDCInsertingServletFilter mdcInsertingServletFilter() {
		return new MDCInsertingServletFilter();
	}
}
