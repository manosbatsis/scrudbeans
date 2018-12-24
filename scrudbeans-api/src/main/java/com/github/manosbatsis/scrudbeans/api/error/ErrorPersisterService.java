package com.github.manosbatsis.scrudbeans.api.error;

/**
 * Implement this as a bean to enable persistence of system errors by
 * com.github.manosbatsis.scrudbeans.autoconfigure.error.resolver.RestExceptionHandler
 */
public interface ErrorPersisterService {
	void persist(Error error);
}
