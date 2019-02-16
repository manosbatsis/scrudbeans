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
package com.github.manosbatsis.scrudbeans.api.util;

import org.springframework.util.MimeType;

public class Mimes {

	/**
	 * Public constant mime type for {@code application/json}.
	 */
	public final static MimeType APPLICATIOM_JSON;

	/**
	 * A String equivalent of {@link Mimes#APPLICATIOM_JSON}.
	 */
	public final static String APPLICATIOM_JSON_VALUE = "application/json";

	/**
	 * Public constant mime type for {@code application/vnd.api+json}.
	 */
	public final static MimeType APPLICATION_VND_API_PLUS_JSON;

	/**
	 * A String equivalent of {@link Mimes#APPLICATION_VND_API_PLUS_JSON}.
	 */
	public final static String APPLICATION_VND_API_PLUS_JSON_VALUE = "application/vnd.api+json";

	/**
	 * Public constant mime type for {@code application/vnd.api+json}.
	 */
	public final static MimeType MIME_APPLICATIOM_HAL_PLUS_JSON;

	/**
	 * A String equivalent of {@link Mimes#MIME_APPLICATIOM_HAL_PLUS_JSON}.
	 */
	public static final String MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE = "application/hal+json";

	static {
		APPLICATIOM_JSON = MimeType.valueOf(APPLICATIOM_JSON_VALUE);
		MIME_APPLICATIOM_HAL_PLUS_JSON = MimeType.valueOf(MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE);
		APPLICATION_VND_API_PLUS_JSON = MimeType.valueOf(APPLICATION_VND_API_PLUS_JSON_VALUE);
	}

}
