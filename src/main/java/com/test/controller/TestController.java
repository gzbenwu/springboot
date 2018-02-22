package com.test.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.test.logging.LogAgent;

@RestController
@RequestMapping(value = "/test")
public class TestController {
	public static final LogAgent LOG_AGENT = LogAgent.getLogAgent(TestController.class);

	@RequestMapping(method = { RequestMethod.GET }, value = "/testAccess")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<?, ?> getTestDetails(HttpServletRequest request, HttpServletResponse response, String entitlementType, String entitlementID, String familyName, String givenName, String rloc, String eTicketNumber) {
		return null;
	}

	@RequestMapping(method = { RequestMethod.DELETE }, value = "/testAccess")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<?, ?> delTestDetails(HttpServletRequest request, HttpServletResponse response) {
		String json = request.getQueryString();
		return null;
	}

	@RequestMapping(method = { RequestMethod.POST }, value = { "/testAccess" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Map<?, ?> testAccess(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestBody Map<?, ?> claimLoungeAccessRequestDTO) {
		return null;
	}
}
