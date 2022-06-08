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

/**
 * Signals that authentication credentials required to respond to a authentication
 * challenge are invalid
 */
public class InvalidCredentialsException extends AuthenticationException {


	public static final String MESSAGE = "Invalid credentials";

	/**
	 * Creates a new InvalidCredentialsException with default message and HTTP status 401.
	 */
	public InvalidCredentialsException() {
		super(MESSAGE);
	}

	/**
	 * Creates a new InvalidCredentialsException with the specified message and HTTP status 401
	 *
	 * @param message the exception detail message
	 */
	public InvalidCredentialsException(final String message) {
		super(message);
	}

	/**
	 * Creates a new InvalidCredentialsException with the specified cause and HTTP status 401.
	 *
	 * @param cause the {@code Throwable} that caused this exception, or {@code null}
	 *              if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public InvalidCredentialsException(Throwable cause) {
		super(MESSAGE, cause);
	}

	/**
	 * Creates a new InvalidCredentialsException with the specified detail message, cause and HTTP status 401
	 *
	 * @param message the exception detail message
	 * @param cause   the {@code Throwable} that caused this exception, or {@code null}
	 *                if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public InvalidCredentialsException(final String message, final Throwable cause) {
		super(message, cause);
	}
}
