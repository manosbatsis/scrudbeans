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
package com.github.manosbatsis.scrudbeans.api.error;

import java.io.Serializable;

import javax.validation.ConstraintViolation;

import io.swagger.annotations.ApiModel;

/**
 * DTO class for {@link ConstraintViolation} instances
 */
@ApiModel(value = "ConstraintViolationEntry", description = "DTO class for serialization of bean validation violations. ")
public class ConstraintViolationEntry implements Serializable {

	private String message;

	private String propertyPath;

	public ConstraintViolationEntry() {

	}

	public ConstraintViolationEntry(String message, String propertyPath) {
		this.message = message;
		this.propertyPath = propertyPath;
	}

	public ConstraintViolationEntry(ConstraintViolation constraintViolation) {
		if (constraintViolation != null) {
			this.message = constraintViolation.getMessage();
			this.propertyPath = constraintViolation.getPropertyPath().toString();
		}
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getPropertyPath() {
		return propertyPath;
	}

	public void setPropertyPath(String propertyPath) {
		this.propertyPath = propertyPath;
	}

}
