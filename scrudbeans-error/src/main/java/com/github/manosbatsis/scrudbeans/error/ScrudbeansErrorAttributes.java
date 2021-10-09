/**
 *
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.error;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.core.PriorityOrdered;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;


/**
 * Default {@code ErrorAttributes} implementation
 */
public class ScrudbeansErrorAttributes
		implements ErrorAttributes, HandlerExceptionResolver, PriorityOrdered {

	private static final Logger LOGGER = LoggerFactory.getLogger(ScrudbeansErrorAttributes.class);
	private static final String ERROR_ATTRIBUTE = DefaultErrorAttributes.class.getName() + ".ERROR";
	public static final int ORDER = new DefaultErrorAttributes().getOrder() - 10;
	// TODO use
	private String currentApiVersion;
	private String sendReportUri;
	private Boolean includeException;

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
										 Exception ex) {
		storeErrorAttributes(request, ex);
		return null;
	}

	private void storeErrorAttributes(HttpServletRequest request, Exception ex) {
		request.setAttribute(ERROR_ATTRIBUTE, ex);
	}

	@Override
	public Map<String, Object> getErrorAttributes(WebRequest request, ErrorAttributeOptions options) {
		Map<String, Object> errorAttributes = new HashMap<String, Object>();
		Throwable throwable = getError(request);
		printRequestProps(request);
		boolean includeStackTrace = options.isIncluded(ErrorAttributeOptions.Include.EXCEPTION);
		ScrudbeansErrorResponse errorResponse = getErrorResponse(request, throwable, includeStackTrace);
		errorAttributes.put("message", errorResponse.getMessage());
		errorAttributes.put("remoteAddress", errorResponse.getRemoteAddress());
		errorAttributes.put("requestMethod", errorResponse.getRequestMethod());
		errorAttributes.put("requestUrl", errorResponse.getRequestUrl());
		errorAttributes.put("httpStatusCode", errorResponse.getHttpStatusCode());
		errorAttributes.put("httpStatusMessage", errorResponse.getHttpStatusMessage());
		errorAttributes.put("user", errorResponse.getUser());
		errorAttributes.put("userAgent", errorResponse.getUserAgent());
		errorAttributes.put("validationErrors", errorResponse.getValidationErrors());
		errorAttributes.put("exceptionClass", errorResponse.getExceptionClass());
		if (includeStackTrace) {
			errorAttributes.put("exceptionStacktrace", errorResponse.getExceptionStacktrace());
		}
		return errorAttributes;
	}

	@Value("${scrudbeans.api.version:1.0}")
	public void setCurrentApiVersion(String currentApiVersion) {
		this.currentApiVersion = currentApiVersion;
	}

	@Value("${scrudbeans.sendreport.uri:none}")
	public void setSendReportUri(String sendReportUri) {
		this.sendReportUri = sendReportUri;
	}

	@Value("${server.error.include-exception:false}")
	public void setIncludeException(Boolean includeException) {
		this.includeException = includeException;
	}


	public void printRequestProps(WebRequest webRequest) {
		Util.printRequestProps(webRequest);
	}

	public ScrudbeansErrorResponse getErrorResponse(WebRequest webRequest, boolean includeStackTrace) {
		LOGGER.debug("getErrorResponse, includeStackTrace: {}", includeStackTrace);
		printRequestProps(webRequest);
		return getErrorResponse(webRequest, Util.getErrorAttribute(webRequest), includeStackTrace);
	}

	public ScrudbeansErrorResponse getErrorResponse(WebRequest webRequest, Throwable ex, boolean includeStackTrace) {
		LOGGER.warn("getErrorResponse, exception: {}", ex);
		printRequestProps(webRequest);
		return new ScrudbeansErrorResponse(
				(ServletWebRequest) webRequest, null, Util.getHttpStatus(ex), ex, includeStackTrace);
	}

	@Override
	public int getOrder() {
		return ORDER;
	}

	@Override
	public Throwable getError(WebRequest webRequest) {
		return Util.getErrorAttribute(webRequest);
	}
}