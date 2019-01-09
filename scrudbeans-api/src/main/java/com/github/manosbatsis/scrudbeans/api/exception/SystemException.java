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
package com.github.manosbatsis.scrudbeans.api.exception;

import java.util.Map;

import org.apache.commons.lang3.builder.ToStringBuilder;

import org.springframework.http.HttpStatus;

/**
 * Signals that an HTTP aware error has occurred.
 */
public abstract class SystemException extends RuntimeException {

	protected String message;

	protected HttpStatus status;

	/**
	 * Creates a new SystemException with a {@code null} message.
	 */
	protected SystemException() {
		super();
	}

	/**
	 * Creates a new SystemException with the specified message.
	 *
	 * @param message the exception message
	 */
	protected SystemException(final String message) {
		super(message);
		this.message = message;
	}

	/**
	 * Creates a new SystemException with the specified status.
	 *
	 * @param status the HTTP status
	 */
	public SystemException(final HttpStatus status) {
		this(status.getReasonPhrase());
		this.status = status;
	}

	/**
	 * Creates a new SystemException with the specified status and cause.
	 *
	 * @param status the HTTP status
	 * @param cause  the {@code Throwable} that caused this exception, or {@code null}
	 */
	public SystemException(final HttpStatus status, final Throwable cause) {
		this(status.getReasonPhrase(), status, cause);
	}

	/**
	 * Creates a new SystemException with the specified message and status.
	 *
	 * @param message the exception message
	 * @param status  the HTTP status
	 */
	public SystemException(final String message, final HttpStatus status) {
		this(message);
		this.status = status;
	}

	/**
	 * Creates a new SystemException with the specified message, status and cause.
	 *
	 * @param message the exception detail message
	 * @param status  the HTTP status
	 * @param cause   the {@code Throwable} that caused this exception, or {@code null}
	 *                if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public SystemException(final String message, final HttpStatus status, final Throwable cause) {
		this(message, status);
		initCause(cause);
	}

	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public HttpStatus getStatus() {
		return status;
	}

	public void setStatus(HttpStatus status) {
		this.status = status;
	}

	public Map<String, String> getResponseHeaders() {
		return null;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("message", this.getMessage())
				.append("status", this.getStatus())
				.append("cause", this.getCause())
				.toString();
	}
}
