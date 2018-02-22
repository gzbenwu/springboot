package com.test.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.MDC;
import org.springframework.stereotype.Component;

@Component
public class CXMDCServletFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		insertIntoMDC(request, response);
		try {
			chain.doFilter(request, response);
		} finally {
			clearMDC();
		}
	}

	void insertIntoMDC(ServletRequest request, ServletResponse response) {
		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpServletRequest = (HttpServletRequest) request;
			HttpSession session = httpServletRequest.getSession();
			if (session != null) {
				MDC.put("LOG-SESSION-ID", session.getId());
			}

			MDC.put("LOG-OTHERS", "***");
		}
	}

	void clearMDC() {
		MDC.remove("LOG-SESSION-ID");
		MDC.remove("LOG-OTHERS");
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}
