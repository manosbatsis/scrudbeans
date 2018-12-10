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
package com.github.manosbatsis.scrudbeans.api.message;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Generic interface for subject and object parts of a {@link IActivityNotificationMessage}
 *
 *
 * @param <PK> the identifier type
 *
 */
@JsonTypeInfo(
		use = JsonTypeInfo.Id.MINIMAL_CLASS,
		include = JsonTypeInfo.As.PROPERTY,
		property = IMessageResource.CLASS_ATTRIBUTE_NAME)
public interface IMessageResource<PK extends Serializable> extends Serializable {

	public static final String CLASS_ATTRIBUTE_NAME = "@class";

	public PK getId();

	public void setId(PK id);

	public String getName();

	public void setName(String name);
}