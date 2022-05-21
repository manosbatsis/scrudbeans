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

object ClassUtils {
    private val logger = loggerFor<ClassUtils>()
    private const val DOT = "."

    /**
     * Returns the (initialized) class represented by `className`
     * using the current thread's context class loader and wraps any exceptions
     * in a RuntimeException.
     *
     * This implementation
     * supports the syntaxes "`java.util.Map.Entry[]`",
     * "`java.util.Map$Entry[]`", "`[Ljava.util.Map.Entry;`",
     * and "`[Ljava.util.Map$Entry;`".
     *
     * @param className  the class name
     * @return the class represented by `className` using the current thread's context class loader
     * @throws ClassNotFoundException if the class is not found
     */
    fun getClass(className: String): Class<*> {
        val clazz: Class<*> = try {
            org.apache.commons.lang3.ClassUtils.getClass(className)
        } catch (e: ClassNotFoundException) {
            throw RuntimeException(e)
        }
        return clazz
    }

    /**
     * Break input into a pair consisting of the package and classname
     */
	@JvmStatic
	fun getPackageAndSimpleName(className: String): Pair<String, String> {
        val lastDotIndex = className.lastIndexOf(DOT)
        val simpleName = className.substring(lastDotIndex + 1)
        val packageName = className.substring(0, className.length - (simpleName.length + 1))
        return Pair(packageName, simpleName)
    }

}