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


import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Signals a validation failure
 */
public class BeanValidationException extends BadRequestException implements ConstraintViolationException {

	private Set<ConstraintViolation> constraintViolations;

	private String modelType;


	/**
	 * Creates a new instance with HTTP 400 status code and message.
	 * @param constraintViolations bean validation errors, if any
	 */
	public BeanValidationException(Set<ConstraintViolation> constraintViolations) {
		super();
		this.constraintViolations = constraintViolations;
	}

	/**
	 * Creates a new instance with the specified message and HTTP status 400.
	 *
	 * @param message the exception detail message
	 * @param constraintViolations bean validation errors, if any
	 */
	public BeanValidationException(final String message, Set<ConstraintViolation> constraintViolations) {
		super(message);
		this.constraintViolations = constraintViolations;
	}

	/**
	 * Creates a new instance with the specified cause and HTTP status 400.
	 *
	 * @param constraintViolations bean validation errors, if any
	 * @param cause the {@code Throwable} that caused this exception, or {@code null}
	 *              if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public BeanValidationException(Set<ConstraintViolation> constraintViolations, final Throwable cause) {
		super(cause);
		this.constraintViolations = constraintViolations;
	}

	/**
	 * Creates a new instance with the specified message, cause and HTTP status 400.
	 *
	 * @param message the exception detail message
	 * @param constraintViolations bean validation errors, if any
	 * @param cause   the {@code Throwable} that caused this exception, or {@code null}
	 *                if the cause is unavailable, unknown, or not a {@code Throwable}
	 */
	public BeanValidationException(final String message, Set<ConstraintViolation> constraintViolations, final Throwable cause) {
		super(message, cause);
		this.constraintViolations = constraintViolations;
	}

	@Override
	public Set<ConstraintViolation> getConstraintViolations() {
		return constraintViolations;
	}

	public void setConstraintViolations(Set<ConstraintViolation> constraintViolations) {
		this.constraintViolations = constraintViolations;
	}

	public String getModelType() {
		return modelType;
	}

	public void setModelType(String modelType) {
		this.modelType = modelType;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.appendSuper(super.toString())
				.append("modelType", this.getModelType())
				.append("constraintViolations", this.getConstraintViolations())
				.toString();
	}
}
