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
package com.github.manosbatsis.scrudbeans.api.exception;


import org.springframework.http.HttpStatus;

/**
 * Signals the appropriate handler for the request is not implemented
 */
public class NotImplementedException extends SystemException {

	protected static final HttpStatus STATUS = HttpStatus.NOT_IMPLEMENTED;

	/**
	 * Creates a new NotImplementedException with HTTP 501 status code and message.
	 */
	public NotImplementedException() {
		super(STATUS);
	}

	/**
	 * Creates a new NotImplementedException with the specified message and HTTP status 501.
	 *
	 * @param message the exception detail message
	 */
	public NotImplementedException(final String message) {
		super(message, STATUS);
	}

	/**
	 * Creates a new NotImplementedException with the specified cause and HTTP status 501.
	 *
	 * @param cause the {@code Throwable} that caused this exception, or {@code null}
	 *              if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public NotImplementedException(final Throwable cause) {
		super(STATUS.getReasonPhrase(), STATUS, cause);
	}

	/**
	 * Creates a new NotImplementedException with the specified message, cause and HTTP status 501.
	 *
	 * @param message the exception detail message
	 * @param cause   the {@code Throwable} that caused this exception, or {@code null}
	 *                if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public NotImplementedException(final String message, final Throwable cause) {
		super(message, STATUS, cause);
	}

}
