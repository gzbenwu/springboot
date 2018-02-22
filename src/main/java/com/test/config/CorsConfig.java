package com.test.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {
	@Autowired
	private BasicConfig basicConfig;

	private CorsConfiguration buildConfig() {
		CorsConfiguration corsConfiguration = new CorsConfiguration();
		for (String allowedOrigin : getAllowedOrigins()) {
			corsConfiguration.addAllowedOrigin(allowedOrigin);
		}
		corsConfiguration.setAllowCredentials(true);
		corsConfiguration.addAllowedMethod("*");

		Stream.of(basicConfig.getCorsdomain().get("allowedheaders")).filter(h -> !h.isEmpty()).map(s -> s.split(",")).flatMap(Arrays::stream).forEach(header -> corsConfiguration.addAllowedHeader(header));

		return corsConfiguration;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig());
		return new CorsFilter(source);
	}

	public List<String> getAllowedOrigins() {
		if (!StringUtils.isEmpty(basicConfig.getCorsdomain().get("allowedorigins"))) {
			return Arrays.asList(basicConfig.getCorsdomain().get("allowedorigins").split(","));
		}
		return Collections.emptyList();
	}
}