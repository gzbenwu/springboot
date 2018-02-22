package com.test.config;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.validation.Validator;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.test.logging.ApiPerformanceLogInterceptor;
import com.test.logging.LogAgent;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	@Override
	public void addFormatters(FormatterRegistry registry) {
		super.addFormatters(registry);
		registry.addConverter(new Converter<String, Date>() {
			private final LogAgent LOG_AGENT = LogAgent.getLogAgent(this.getClass());

			List<SimpleDateFormat> formater = new ArrayList<SimpleDateFormat>();
			{
				formater.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				formater.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
				formater.add(new SimpleDateFormat("yyyyMMdd HH:mm:ss"));
				formater.add(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
				formater.add(new SimpleDateFormat("yyyy/MM/dd HH:mm"));
				formater.add(new SimpleDateFormat("yyyyMMdd HH:mm"));
				formater.add(new SimpleDateFormat("yyyy-MM-dd"));
				formater.add(new SimpleDateFormat("yyyy/MM/dd"));
				formater.add(new SimpleDateFormat("yyyyMMdd"));
				formater.add(new SimpleDateFormat("dd-MMM-yy"));
				formater.add(new SimpleDateFormat("dd/MMM/yy"));
				formater.add(new SimpleDateFormat("ddMMMyy"));
				formater.add(new SimpleDateFormat("dd-MMM-yyyy"));
				formater.add(new SimpleDateFormat("dd/MMM/yyyy"));
				formater.add(new SimpleDateFormat("ddMMMyyyy"));
			}

			@Override
			public Date convert(String source) {
				Date d = null;
				for (SimpleDateFormat f : formater) {
					try {
						d = ((SimpleDateFormat) f.clone()).parse(source);
					} catch (Exception e) {
						continue;
					}
					if (d == null) {
						continue;
					}
					break;
				}
				if (d == null) {
					// LOG_AGENT.warn("Can't convert param String to Date: " + source);
				}
				return d;
			}
		});
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new ApiPerformanceLogInterceptor());
	}

	@Bean
	@Primary
	public Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) throws KeyManagementException, NoSuchAlgorithmException, KeyStoreException {
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		CloseableHttpClient client = httpClientBuilder.build();
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
		builder.requestFactory(clientHttpRequestFactory);
		RestTemplate restTemplate = builder.build();
		return restTemplate;
	}
}
