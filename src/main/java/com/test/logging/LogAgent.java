package com.test.logging;

import net.logstash.logback.argument.StructuredArguments;

import org.apache.commons.lang.time.DurationFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class LogAgent {
	public static final String LOG_ENTRY_TYPE_INT = "INT";
	public static final String LOG_ENTRY_TYPE_PER = "PER";
	public static final String LOG_ENTRY_TYPE_ERR = "ERR";
	public static final String LOG_ENTRY_TYPE_EVT = "EVT";
	public static final String LOG_ENTRY_TYPE_INF = "INF";

	private Logger LOGGER;

	private LogAgent(Class<?> clazz) {
		this.LOGGER = (Logger) LoggerFactory.getLogger(clazz);
	}

	public static LogAgent getLogAgent(Class<?> clazz) {
		return new LogAgent(clazz);
	}

	public void info(String methodName, String url, long spendTime, String logMsg) {
		String msg = "[TimeDetails][" + logMsg + "][Time: " + DurationFormatUtils.formatDuration(spendTime, "HH:mm:ss,SSS") + "]";
		MDC.put("LOG-TYPE", LOG_ENTRY_TYPE_PER);
		LOGGER.info(msg, StructuredArguments.kv("LOG-SPEND", spendTime), StructuredArguments.kv("LOG-URL", url));
		MDC.remove("LOG-TYPE");
	}
}
