package com.test.config;

import java.lang.reflect.Method;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import com.test.logging.LogAgent;
import com.test.util.httpclient.HttpClientService;

@Configuration
@EnableCaching
public class CacheConfig extends CachingConfigurerSupport {
	public final static LogAgent LOG_AGENT = LogAgent.getLogAgent(CacheConfig.class);
	private final static DefaultKeyGenerator defaultGenerator = new DefaultKeyGenerator();
	private final static Random random = new Random();

	public static final String CACHE_DEFAULT = "defaultCache";
	public static final String CACHE_PNRVIEWRESPONSE = "PNRViewResponseCache";

	@Autowired
	private RedisCacheProperties redisCacheProperties;

	@Bean
	@Override
	public KeyGenerator keyGenerator() {
		return defaultGenerator;
	}

	@Bean
	public CacheManager cacheManager(RedisTemplate<String, String> redisTemplate) {
		RedisCacheManager rcManager = new RedisCacheManager(redisTemplate);
		rcManager.setExpires(redisCacheProperties.getTimeout());
		rcManager.setDefaultExpiration(redisCacheProperties.getTimeout().get("defaultCache"));
		return rcManager;
	}

	@Bean
	public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory factory) {
		RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(factory);
		return redisTemplate;
	}

	static class DefaultKeyGenerator implements KeyGenerator {
		@Override
		public Object generate(Object target, Method method, Object... parameters) {
			String cacheKey = null;
			if (method.getAnnotationsByType(Cacheable.class) != null && method.getAnnotationsByType(Cacheable.class).length > 0) {
				Cacheable[] c = method.getAnnotationsByType(Cacheable.class);
				if (c[0].value() != null && c[0].value().length > 0) {
					cacheKey = c[0].value()[0];
				} else if (c[0].cacheNames() != null && c[0].cacheNames().length > 0) {
					cacheKey = c[0].cacheNames()[0];
				}
			}
			if (cacheKey == null || cacheKey.length() < 1) {
				if (method.getAnnotationsByType(CacheEvict.class) != null && method.getAnnotationsByType(CacheEvict.class).length > 0) {
					CacheEvict[] c = method.getAnnotationsByType(CacheEvict.class);
					if (c[0].value() != null && c[0].value().length > 0) {
						cacheKey = c[0].value()[0];
					} else if (c[0].cacheNames() != null && c[0].cacheNames().length > 0) {
						cacheKey = c[0].cacheNames()[0];
					}
				}
			}
			if (cacheKey == null || cacheKey.length() < 1) {
				if (method.getAnnotationsByType(CachePut.class) != null && method.getAnnotationsByType(CachePut.class).length > 0) {
					CachePut[] c = method.getAnnotationsByType(CachePut.class);
					if (c[0].value() != null && c[0].value().length > 0) {
						cacheKey = c[0].value()[0];
					} else if (c[0].cacheNames() != null && c[0].cacheNames().length > 0) {
						cacheKey = c[0].cacheNames()[0];
					}
				}
			}

			StringBuilder sb = new StringBuilder();
			if (parameters != null) {
				int idx = 1;
				for (Object p : parameters) {
					String json;
					if (p == null) {
						json = "[null]";
					} else {
						json = HttpClientService.convertObjectToJson(p);
						if (json == null) {
							json = "[RANDOM" + random.nextInt(Integer.MAX_VALUE) + "]";
						}
					}
					sb.append("P" + idx++ + ":" + json);
					sb.append(",");
				}
			}

			String str = sb.toString();
			String key = ((cacheKey == null || cacheKey.length() < 1) ? "DEFAULTCACHE" : cacheKey) + "@" + DigestUtils.md5Hex(str);
			// LOG_AGENT.debug("CacheKey:" + key + " ParamJSON:" + str);
			return key;
		}
	}
}
