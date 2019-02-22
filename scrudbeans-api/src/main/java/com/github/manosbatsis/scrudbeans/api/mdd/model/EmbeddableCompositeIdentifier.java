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
package com.github.manosbatsis.scrudbeans.api.mdd.model;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

/**
 * An interface for composite identifiers. Implementations are handled
 * transparently by ScrudBeans in a regular RESTful way, including request
 * mapping bindings of path or query parameters.
 */
public interface EmbeddableCompositeIdentifier extends Serializable {

	public static final String SPLIT_CHAR = "_";

	/**
	 * Initialise using the given string representation
	 * @param value the string representation of this composite ID
	 */
	void init(@NotNull String value);

	/**
	 * Get the string representation of this composite ID
	 */
	String toStringRepresentation();

}
