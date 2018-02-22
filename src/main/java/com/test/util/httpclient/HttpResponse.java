package com.test.util.httpclient;

import org.springframework.http.ResponseEntity;

public class HttpResponse<T> {
	public enum ResponseStatus {
		/**
		 * success
		 */
		SUCCESS_200,

		/**
		 * wrong url
		 */
		URL_ERROR_404,

		/**
		 * wrong parameters
		 */
		CLIENT_ERROR_400_405,

		/**
		 * remote server internal error
		 */
		SERVER_ERROR_500_501_504,

		/**
		 * hostname error, connect error
		 */
		CONNECT_ERROR,

		/**
		 * json response convert to object error
		 */
		CONVERT_JSON_ERROR,

		/**
		 * other system error
		 */
		SYSTEM_ERROR
	}

	/**
	 * full url for request
	 */
	private String requestUrl;

	/**
	 * from remote server, original data body
	 */
	private String originalBody;

	/**
	 * converted to a request DTO. if required String, it will be an originalBody
	 */
	private T value;

	/**
	 * handled statue as above
	 */
	private ResponseStatus status = ResponseStatus.SYSTEM_ERROR;

	/**
	 * error message from http error
	 */
	private String errorMsg;

	private ResponseEntity<String> responseEntity;

	public String getOriginalBody() {
		return originalBody;
	}

	public void setOriginalBody(String originalBody) {
		this.originalBody = originalBody;
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public ResponseStatus getStatus() {
		return status;
	}

	public void setStatus(ResponseStatus status) {
		this.status = status;
	}

	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}

	public String getRequestUrl() {
		return requestUrl;
	}

	public void setRequestUrl(String requestUrl) {
		this.requestUrl = requestUrl;
	}

	public ResponseEntity<String> getResponseEntity() {
		return responseEntity;
	}

	public void setResponseEntity(ResponseEntity<String> responseEntity) {
		this.responseEntity = responseEntity;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HttpResponse [originalBody=");
		builder.append(originalBody);
		builder.append(", value=");
		builder.append(value);
		builder.append(", status=");
		builder.append(status);
		builder.append(", errorMsg=");
		builder.append(errorMsg);
		builder.append("]");
		return builder.toString();
	}
}
