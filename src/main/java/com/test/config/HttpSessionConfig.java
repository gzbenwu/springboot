package com.test.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.session.ExpiringSession;
import org.springframework.session.SessionRepository;
import org.springframework.session.data.redis.RedisOperationsSessionRepository;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieHttpSessionStrategy;
import org.springframework.session.web.http.DefaultCookieSerializer;
import org.springframework.session.web.http.HeaderHttpSessionStrategy;
import org.springframework.session.web.http.HttpSessionStrategy;
import org.springframework.session.web.http.SessionRepositoryFilter;

@Configuration
@EnableRedisHttpSession
public class HttpSessionConfig {
	private HttpSessionStrategy httpSessionStrategy;

	@Autowired
	private BasicConfig basicConfig;

	@Bean
	public CookieHttpSessionStrategy httpSessionStrategy() {
		CookieHttpSessionStrategy cookieHttpSessionStrategy = new CookieHttpSessionStrategy();
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		serializer.setCookieName(basicConfig.getSessionCookieKey());
		serializer.setDomainNamePattern("^.+?\\.(\\w+\\.[a-z]+)$");
		serializer.setCookiePath("/");
		serializer.setUseHttpOnlyCookie(basicConfig.isCookieHttponly());
		serializer.setUseSecureCookie(basicConfig.isCookieSecure());
		cookieHttpSessionStrategy.setCookieSerializer(serializer);
		return cookieHttpSessionStrategy;
	}

	@Bean
	public RedisOperationsSessionRepository sessionRepository(RedisConnectionFactory redisConnectionFactory) {
		RedisOperationsSessionRepository sessionRepository = new RedisOperationsSessionRepository(redisConnectionFactory);
		sessionRepository.setDefaultMaxInactiveInterval(basicConfig.getMaxInactiveIntervalInSeconds());
		return sessionRepository;
	}

	@Bean
	public <S extends ExpiringSession> SessionRepositoryFilter<S> springSessionRepositoryFilter(SessionRepository<S> sessionRepository) {
		SessionRepositoryFilter<S> sessionRepositoryFilter = new SessionRepositoryFilter<>(sessionRepository);
		if (httpSessionStrategy != null) {
			sessionRepositoryFilter.setHttpSessionStrategy(httpSessionStrategy);
		}
		return sessionRepositoryFilter;
	}

	@Bean
	public static ConfigureRedisAction configureRedisAction() {
		return ConfigureRedisAction.NO_OP;
	}

	@Bean
	public HttpSessionStrategy headerHttpSessionStrategy() {
		return new HeaderHttpSessionStrategy();
	}

	@Autowired(required = false)
	public void setHttpSessionStrategy(HttpSessionStrategy httpSessionStrategy) {
		this.httpSessionStrategy = httpSessionStrategy;
	}
}