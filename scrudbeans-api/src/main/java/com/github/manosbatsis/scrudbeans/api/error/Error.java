package com.github.manosbatsis.scrudbeans.api.error;

/**
 *
 */
public interface Error {

	String getMessage();

	String getRemoteAddress();

	String getRequestMethod();

	String getRequestUrl();

	Integer getHttpStatusCode();

	String getHttpStatusMessage();

	String getUserAgent();

	java.util.Set<ConstraintViolationEntry> getValidationErrors();

}
