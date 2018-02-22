package com.test.util.httpclient;

import java.io.IOException;
import java.net.URI;
import java.net.URLDecoder;
import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.test.config.BasicConfig;
import com.test.logging.LogAgent;
import com.test.util.httpclient.HttpResponse.ResponseStatus;

@Component
public class HttpClientService {
	private static final LogAgent LOG_AGENT = LogAgent.getLogAgent(HttpClientService.class);
	private static final String emptyStrParam = "";
	private static final Object[] emptyListParam = new Object[] {};
	private static RestTemplate restTemplate;
	private static ObjectMapper objectMapper;

	@Autowired
	private BasicConfig basicConfig;

	@PostConstruct
	private void init() {
		// ------------------------- ObjectMapper init --------------------------------
		/**
		 * date formater for default jsonstring convert, support most date,datetime format.
		 * <P>
		 * this is for default settings, please use @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss") in DTO for individual.
		 */
		objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES, false);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_INVALID_SUBTYPE, false);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_MISSING_EXTERNAL_TYPE_ID_PROPERTY, false);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS, false);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY, false);
		// objectMapper.configure(DeserializationFeature.FAIL_ON_UNRESOLVED_OBJECT_IDS, false);
		objectMapper.setDateFormat(new DateFormat() {
			private static final long serialVersionUID = 1L;
			private SimpleDateFormat DefaultDateFormat = new SimpleDateFormat("yyyy-MM-dd");
			private LogAgent LOG_AGENT = LogAgent.getLogAgent(this.getClass());
			private List<SimpleDateFormat> formater = new ArrayList<SimpleDateFormat>();
			{
				formater.add(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
				formater.add(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"));
				formater.add(new SimpleDateFormat("yyyyMMdd HH:mm:ss"));
				formater.add(new SimpleDateFormat("yyyy-MM-dd HH:mm"));
				formater.add(new SimpleDateFormat("yyyy/MM/dd HH:mm"));
				formater.add(new SimpleDateFormat("yyyyMMdd HH:mm"));
				formater.add(DefaultDateFormat);
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
			public StringBuffer format(Date arg0, StringBuffer arg1, FieldPosition arg2) {
				return ((SimpleDateFormat) DefaultDateFormat.clone()).format(arg0, arg1, arg2);
			}

			@Override
			public Date parse(String arg0, ParsePosition arg1) {
				Date d = null;
				for (SimpleDateFormat f : formater) {
					try {
						d = ((SimpleDateFormat) f.clone()).parse(arg0, arg1);
					} catch (Exception e) {
						continue;
					}
					if (d == null) {
						continue;
					}
					break;
				}
				if (d == null) {
					// LOG_AGENT.error("Can't convert JsonString to Date: " + arg0);
				}
				return d;
			}

			@Override
			public DateFormat clone() {
				return this;
			}
		});

		// ------------------------- RestTemplate init --------------------------------
		HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
		if (basicConfig.getHttpclientservice().get("poolMaxConn") != null && basicConfig.getHttpclientservice().get("poolMaxConn") > 0) {
			httpClientBuilder.setMaxConnTotal(basicConfig.getHttpclientservice().get("poolMaxConn"));
		}
		if (basicConfig.getHttpclientservice().get("maxConnPerRoute") != null && basicConfig.getHttpclientservice().get("maxConnPerRoute") > 0) {
			httpClientBuilder.setMaxConnPerRoute(basicConfig.getHttpclientservice().get("maxConnPerRoute"));
		}
		CloseableHttpClient client = httpClientBuilder.build();
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(client);
		if (basicConfig.getHttpclientservice().get("requestTimeout") != null && basicConfig.getHttpclientservice().get("requestTimeout") > 0) {
			clientHttpRequestFactory.setConnectionRequestTimeout(basicConfig.getHttpclientservice().get("requestTimeout"));
		}
		if (basicConfig.getHttpclientservice().get("connectTimeout") != null && basicConfig.getHttpclientservice().get("connectTimeout") > 0) {
			clientHttpRequestFactory.setConnectTimeout(basicConfig.getHttpclientservice().get("connectTimeout"));
		}
		if (basicConfig.getHttpclientservice().get("readTimeout") != null && basicConfig.getHttpclientservice().get("readTimeout") > 0) {
			clientHttpRequestFactory.setReadTimeout(basicConfig.getHttpclientservice().get("readTimeout"));
		}

		restTemplate = new RestTemplate(clientHttpRequestFactory);
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
			}
		});
	}

	/**
	 * one instance.
	 * <P>
	 * not exception throw when connect error. handle by http status code
	 * 
	 * @return
	 */
	public static RestTemplate getRestTemplate() {
		return restTemplate;
	}

	private static HttpEntity<LinkedMultiValueMap<String, String>> prepareHeader(Map<String, String> paramMappings) {
		LinkedMultiValueMap<String, String> param = new LinkedMultiValueMap<String, String>();
		if (paramMappings != null && paramMappings.size() > 0) {
			for (Entry<String, String> e : paramMappings.entrySet()) {
				param.add(e.getKey(), e.getValue());
			}
		}
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		HttpEntity<LinkedMultiValueMap<String, String>> httpEntity = new HttpEntity<LinkedMultiValueMap<String, String>>(param, httpHeaders);
		return httpEntity;
	}

	private static HttpEntity<String> prepareHeader(String jsonString) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<String> httpEntity = new HttpEntity<String>(jsonString, httpHeaders);
		return httpEntity;
	}

	private static <T> HttpEntity<T> prepareHeader(T Object) {
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON_UTF8);
		HttpEntity<T> httpEntity = new HttpEntity<T>(Object, httpHeaders);
		return httpEntity;
	}

	private static <T> HttpResponse<T> doRequest(Class<T> clazz, String url, HttpEntity<?> header, HttpMethod method, Map<String, String> paramMappings, Object... paramList) {
		HttpResponse<T> res = new HttpResponse<T>();
		try {
			Long startTime = System.currentTimeMillis();
			ResponseEntity<String> response = null;
			if (paramMappings == null) {
				URI uri = getRestTemplate().getUriTemplateHandler().expand(url, paramList);
				res.setRequestUrl(uri.toString());
				response = getRestTemplate().exchange(url, method, header, String.class, paramList);
			} else {
				URI uri = getRestTemplate().getUriTemplateHandler().expand(url, paramMappings);
				res.setRequestUrl(uri.toString());
				response = getRestTemplate().exchange(url, method, header, String.class, paramMappings);
			}
			res.setResponseEntity(response);
			Long endTime = System.currentTimeMillis();
			int index = res.getRequestUrl().indexOf("/", 10);
			LOG_AGENT.info("HttpClientService.doRequest", res.getRequestUrl().substring(0, index), endTime - startTime, "Http call for: " + res.getRequestUrl().substring(0, index));

			if (response.getStatusCode().is2xxSuccessful()) {
				res.setStatus(ResponseStatus.SUCCESS_200);
				res.setOriginalBody(response.hasBody() ? response.getBody() : "");
			} else if (response.getStatusCode().is4xxClientError()) {
				if (HttpStatus.NOT_FOUND.equals(response.getStatusCode())) {
					res.setStatus(ResponseStatus.URL_ERROR_404);
				} else {
					res.setStatus(ResponseStatus.CLIENT_ERROR_400_405);
				}
				res.setErrorMsg(response.getBody());
			} else if (response.getStatusCode().is5xxServerError()) {
				res.setStatus(ResponseStatus.SERVER_ERROR_500_501_504);
				res.setErrorMsg(response.getBody());
			}
		} catch (ResourceAccessException e) {
			res.setStatus(ResponseStatus.CONNECT_ERROR);
			res.setErrorMsg("[" + e.getClass().getName() + "]" + e.getMessage());
		} catch (Throwable t) {
			res.setStatus(ResponseStatus.SYSTEM_ERROR);
			res.setErrorMsg("[" + t.getClass().getName() + "]" + t.getMessage());
		}

		if (!ResponseStatus.SUCCESS_200.equals(res.getStatus())) {
			// LOG_AGENT.error("Http request error! URL:" + res.getRequestUrl() + " Msg:" + res.getErrorMsg(), res.getStatus().toString());
		}

		return res;
	}

	@SuppressWarnings("unchecked")
	private static <T> void convertToObject(HttpResponse<T> res, Class<T> clazz) {
		if (ResponseStatus.SUCCESS_200.equals(res.getStatus())) {
			if (String.class.equals(clazz)) {
				res.setValue((T) res.getOriginalBody());
			} else {
				T obj = convertJsonToObject(res.getOriginalBody(), clazz, true);
				if (obj == null) {
					res.setStatus(ResponseStatus.CONVERT_JSON_ERROR);
					res.setErrorMsg("Json convert error for response:" + res.getOriginalBody());
				} else {
					res.setValue(obj);
				}
			}
		}
	}

	private static <T> T convertJsonToObject(String jsonString, Class<T> clazz, boolean returnNull) {
		T obj = null;
		try {
			obj = clazz.newInstance();
		} catch (Exception e) {
			// LOG_AGENT.error("Can't init a new instance for Class: " + clazz.getName(), ResponseStatus.CONVERT_JSON_ERROR.toString(), "convertJsonToObject", e);
			return null;
		}

		if (jsonString == null || jsonString.length() < 1) {
			return returnNull ? null : obj;
		}

		try {
			obj = objectMapper.readValue(jsonString, clazz);
		} catch (Exception e) {
			try {
				Gson gson = new Gson();
				obj = gson.fromJson(jsonString, clazz);
			} catch (Exception t) {
				// LOG_AGENT.error("Can't convert json to Object: " + jsonString, ResponseStatus.CONVERT_JSON_ERROR.toString(), "convertJsonToObject", e);
				return returnNull ? null : obj;
			}
		}
		return obj;
	}

	/**
	 * if convert failed, return an new instance, not a null
	 * 
	 * @param jsonString
	 * @param clazz
	 * @param characterEncoding
	 * @return
	 */
	public static <T> T convertJsonToObject(String jsonString, Class<T> clazz) {
		return convertJsonToObject(jsonString, clazz, false);
	}

	/**
	 * if convert failed, return an new instance, not a null
	 * 
	 * @param httpRequest
	 * @param clazz
	 * @return
	 */
	public static <T> T convertJsonToObject(HttpServletRequest httpRequest, Class<T> clazz) {
		String jsonString = httpRequest.getQueryString();
		if (jsonString == null || jsonString.length() < 1) {
			jsonString = "";
		} else {
			try {
				jsonString = URLDecoder.decode(jsonString, httpRequest.getCharacterEncoding());
			} catch (Exception e) {
				// LOG_AGENT.error("Can't convert json to Object when URLDecode[" + httpRequest.getCharacterEncoding() + "]: " + jsonString, e);
			}
		}
		return convertJsonToObject(jsonString, clazz);
	}

	/**
	 * @param obj
	 * @return
	 */
	public static String convertObjectToJson(Object obj) {
		if (obj == null) {
			return null;
		}
		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(obj);
		} catch (Exception e) {
			try {
				Gson gson = new Gson();
				jsonString = gson.toJson(obj);
			} catch (Exception t) {
				// LOG_AGENT.error("Can't convert Object to JsonString: " + obj, ResponseStatus.CONVERT_JSON_ERROR.toString(), "convertObjectToJson", e);
			}
		}
		return jsonString;
	}

	/**
	 * PostMethod to get a String body by Map params
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param paramMappings:
	 *            Map<String,String> entry, for key and value
	 * @return
	 */
	public static HttpResponse<String> postForString(String url, Map<String, String> paramMappings) {
		return postForObject(String.class, url, paramMappings);
	}

	/**
	 * PostMethod to get a String body by json string
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param jsonString:
	 *            {"f1":"ff","f2":"2018-01-01"}
	 * @return
	 */
	public static HttpResponse<String> postForString(String url, String jsonString) {
		return postForObject(String.class, url, jsonString);
	}

	/**
	 * PostMethod to get a String body
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @return
	 */
	public static HttpResponse<String> postForString(String url) {
		return postForString(url, emptyStrParam);
	}

	/**
	 * GetMethod to get a String body by Map params
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param paramMappings:
	 *            Map<String,String> entry, for key and value
	 * @return
	 */
	public static HttpResponse<String> getForString(String url, Map<String, String> paramMappings) {
		return getForObject(String.class, url, paramMappings);
	}

	/**
	 * GetMethod to get a String body by List params
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01?f1={a}&f2={b}
	 * @param params:
	 *            new Object[]{"ff","2018-01-01"}
	 * @return
	 */
	public static HttpResponse<String> getForString(String url, Object[] params) {
		return getForObject(String.class, url, params);
	}

	/**
	 * GetMethod to get a String body by json string
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param jsonString:
	 *            {"f1":"ff","f2":"2018-01-01"}
	 * @return
	 */
	public static HttpResponse<String> getForString(String url, String jsonString) {
		return getForObject(String.class, url, jsonString);
	}

	/**
	 * GetMethod to get a String body
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @return
	 */
	public static HttpResponse<String> getForString(String url) {
		return getForString(url, emptyListParam);
	}

	/**
	 * PostMethod to get a DTO object by Map params
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param paramMappings:
	 *            Map<String,String> entry, for key and value
	 * @return
	 */
	public static <T> HttpResponse<T> postForObject(Class<T> clazz, String url, Map<String, String> paramMappings) {
		HttpEntity<LinkedMultiValueMap<String, String>> header = prepareHeader(paramMappings);
		HttpResponse<T> body = doRequest(clazz, url, header, HttpMethod.POST, null);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * PostMethod to get a DTO object by Object
	 * 
	 * @param <N>
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param paramMappings:
	 *            Map<String,String> entry, for key and value
	 * @return
	 */
	public static <T, N> HttpResponse<T> postForObject(Class<T> clazz, String url, N Object) {
		HttpEntity<N> header = prepareHeader(Object);
		HttpResponse<T> body = doRequest(clazz, url, header, HttpMethod.POST, null);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * PostMethod to get a DTO object by json string
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param jsonString:
	 *            {"f1":"ff","f2":"2018-01-01"}
	 * @return
	 */
	public static <T> HttpResponse<T> postForObject(Class<T> clazz, String url, String jsonString) {
		HttpEntity<String> header = prepareHeader(jsonString);
		HttpResponse<T> body = doRequest(clazz, url, header, HttpMethod.POST, null);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * PostMethod to get a DTO object
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @return
	 */
	public static <T> HttpResponse<T> postForObject(Class<T> clazz, String url) {
		return postForObject(clazz, url, emptyStrParam);
	}

	/**
	 * GetMethod to get a DTO object by Map params
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param paramMappings:
	 *            Map<String,String> entry, for key and value
	 * @return
	 */
	public static <T> HttpResponse<T> getForObject(Class<T> clazz, String url, Map<String, String> paramMappings) {
		if (paramMappings != null && paramMappings.size() > 0) {
			url += "?";
			for (Entry<String, String> e : paramMappings.entrySet()) {
				url += e.getKey() + "={" + e.getKey() + "}&";
			}
		}
		HttpResponse<T> body = doRequest(clazz, url, null, HttpMethod.GET, paramMappings);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * GetMethod to get a DTO object by List params
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01?f1={a}&f2={b}
	 * @param params:
	 *            new Object[]{"ff","2018-01-01"}
	 * @return
	 */
	public static <T> HttpResponse<T> getForObject(Class<T> clazz, String url, Object[] params) {
		HttpResponse<T> body = doRequest(clazz, url, null, HttpMethod.GET, null, params);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * GetMethod to get a DTO object by json string
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @param jsonString:
	 *            {"f1":"ff","f2":"2018-01-01"}
	 * @return
	 */
	public static <T> HttpResponse<T> getForObject(Class<T> clazz, String url, String jsonString) {
		HttpEntity<String> header = prepareHeader(jsonString);
		HttpResponse<T> body = doRequest(clazz, url + "?" + jsonString, header, HttpMethod.GET, null, jsonString);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * PutMethod to get a DTO object by json string
	 * 
	 * @param url:
	 *            http://localhost:8080/test{memberId}/action01
	 * @param urlParams:["123456789"]
	 * @param jsonString:
	 *            {"f1":"ff","f2":"2018-01-01"}
	 * @return
	 */
	public static <T> HttpResponse<T> putForObject(Class<T> clazz, String url, Object[] urlParams, String jsonString) {
		HttpEntity<String> header = prepareHeader(jsonString);
		HttpResponse<T> body = doRequest(clazz, url, header, HttpMethod.PUT, null, urlParams);
		convertToObject(body, clazz);
		return body;
	}

	/**
	 * GetMethod to get a DTO object
	 * 
	 * @param url:
	 *            http://localhost:8080/test/action01
	 * @return
	 */
	public static <T> HttpResponse<T> getForObject(Class<T> clazz, String url) {
		return getForObject(clazz, url, emptyListParam);
	}
}
