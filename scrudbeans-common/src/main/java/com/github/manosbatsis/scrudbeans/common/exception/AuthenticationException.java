/**
 *
 * Restdude
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
package com.github.manosbatsis.scrudbeans.common.exception;


import java.util.HashMap;
import java.util.Map;

import com.github.manosbatsis.scrudbeans.api.exception.SystemException;

import org.springframework.http.HttpStatus;

/**
 * Signals a failure in authentication process
 */
public class AuthenticationException extends SystemException {

	protected static final HttpStatus STATUS = HttpStatus.UNAUTHORIZED;

	/**
	 * Creates a new instance with HTTP 401 status code and message.
	 */
	protected AuthenticationException() {
		super(STATUS);
	}

	/**
	 * Creates a new instance with the specified message and HTTP status 401.
	 *
	 * @param message the exception detail message
	 */
	protected AuthenticationException(final String message) {
		super(message, STATUS);
	}

	/**
	 * Creates a new instance with the specified cause and HTTP status 401.
	 *
	 * @param cause the {@code Throwable} that caused this exception, or {@code null}
	 *              if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	protected AuthenticationException(final Throwable cause) {
		super(STATUS.getReasonPhrase(), STATUS, cause);
	}

	/**
	 * Creates a new instance with the specified message, cause and HTTP status 401.
	 *
	 * @param message the exception detail message
	 * @param cause   the {@code Throwable} that caused this exception, or {@code null}
	 *                if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	protected AuthenticationException(final String message, final Throwable cause) {
		super(message, STATUS, cause);
	}

	@Override
	public Map<String, String> getResponseHeaders() {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put("WWW-Authenticate", "X-Calipso-Token header or calipso-sso token cookie");
		return headers;
	}
}
