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
package com.github.manosbatsis.scrudbeans.util

import com.github.manosbatsis.scrudbeans.logging.loggerFor
import java.lang.reflect.Field
import java.util.*

object ClassUtils {
    private val logger = loggerFor<ClassUtils>()
    private const val DOT = "."

    fun fieldByName(fieldName: String, container: Class<*>): Field? {
        var clazz: Class<*> = container
        val rootClasses = setOf(java.lang.Object::class.java, Any::class.java)
        while (!rootClasses.contains(clazz)) {
            clazz.declaredFields.find { it.name == fieldName }
                ?.also { return it }
            clazz = clazz.superclass
        }
        return null
    }
}
