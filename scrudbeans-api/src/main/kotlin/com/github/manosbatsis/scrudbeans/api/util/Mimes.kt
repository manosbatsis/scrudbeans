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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.api.util

import org.springframework.util.MimeType

object Mimes {

    /**
     * A String equivalent of [Mimes.APPLICATIOM_JSON].
     */
    const val APPLICATIOM_JSON_VALUE = "application/json"

    /**
     * A String equivalent of [Mimes.APPLICATION_VND_API_PLUS_JSON].
     */
    const val APPLICATION_VND_API_PLUS_JSON_VALUE = "application/vnd.api+json"

    /**
     * A String equivalent of [Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON].
     */
    const val MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE = "application/hal+json"

    /**
     * Public constant mime type for `application/json`.
     */
    val APPLICATIOM_JSON: MimeType = MimeType.valueOf(APPLICATIOM_JSON_VALUE)

    /**
     * Public constant mime type for `application/vnd.api+json`.
     */
    val MIME_APPLICATIOM_HAL_PLUS_JSON: MimeType = MimeType.valueOf(MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE)

    /**
     * Public constant mime type for `application/vnd.api+json`.
     */
    val APPLICATION_VND_API_PLUS_JSON: MimeType = MimeType.valueOf(APPLICATION_VND_API_PLUS_JSON_VALUE)
}
