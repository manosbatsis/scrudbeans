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
package com.github.manosbatsis.scrudbeans.api.domain;

import java.io.Serializable;

/**
 * Base interface for model objects
 * @param <PK> The primary key type, if any
 */
public interface Model<PK extends Serializable> extends Serializable {

	/**
	 * The primary key, field name.
	 */
	String PK_FIELD_NAME = "id";

	/**
	 * Get the entity's primary key.
	 */
	PK getId();

	/**
	 * Set the entity's primary key
	 *
	 * @param id the id to set
	 */
	void setId(PK id);

}