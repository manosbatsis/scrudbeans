package com.github.manosbatsis.scrudbeans.api.error;

/**
 *
 */
public interface Error {
	String getTitle();

	String getRemoteAddress();

	String getRequestMethod();

	String getRequestUrl();

	Integer getHttpStatusCode();

	String getUserAgent();

	java.util.Set<ConstraintViolationEntry> getValidationErrors();

	Throwable getThrowable();
}
