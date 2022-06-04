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
import org.springframework.core.GenericTypeResolver
import org.springframework.util.Assert
import java.beans.IntrospectionException
import java.beans.Introspector
import java.beans.PropertyDescriptor
import java.lang.reflect.Field
import java.util.*

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
    @JvmStatic
    fun getClass(className: String): Class<*> {
        val clazz: Class<*>
        clazz = try {
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

    @JvmStatic
    fun <T : Any?> newInstance(
        clazz: Class<T>
    ): T {
        Assert.notNull(clazz, "clazz cannot be null")
        return try {
            clazz.getConstructor()?.newInstance()
                ?: throw IllegalArgumentException("No empty constructor found for ${clazz.canonicalName}")
        } catch (e: InstantiationException) {
            throw RuntimeException("Failed instantiating new instance for class: " + clazz.canonicalName, e)
        } catch (e: IllegalAccessException) {
            throw RuntimeException("Failed instantiating new instance for class: " + clazz.canonicalName, e)
        }
    }

    fun getBeanPropertyType(clazz: Class<*>, fieldName: String, silent: Boolean): Class<*>? {
        Assert.notNull(clazz, "clazz parameter cannot be null")
        Assert.notNull(fieldName, "fieldName parameter cannot be null")
        var beanPropertyType: Class<*>? = null
        val steps = if (fieldName.contains(".")) fieldName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray() else arrayOf(fieldName)
        logger.debug(
            "getBeanPropertyType called for {}#{}, steps({}): {}",
            clazz.canonicalName,
            fieldName,
            steps.size,
            steps
        )
        try {
            val stepNames: Iterator<String> = Arrays.asList(*steps).listIterator()
            var tmpClass: Class<*>? = clazz
            var tmpFieldName: String?
            var propertyDescriptors: Array<PropertyDescriptor>?
            var i = 0
            while (stepNames.hasNext() && tmpClass != null) {
                tmpFieldName = stepNames.next()
                logger.debug(
                    "getBeanPropertyType for path {}, tmpClass: {} tmpFieldName: {}",
                    i,
                    tmpClass.name,
                    tmpFieldName
                )
                propertyDescriptors = Introspector.getBeanInfo(tmpClass).propertyDescriptors
                for (pd in propertyDescriptors) {
                    if (tmpFieldName == pd.name) {
                        val getter = pd.readMethod
                        if (getter != null) {
                            tmpClass = GenericTypeResolver.resolveReturnType(getter, tmpClass!!)
                            logger.debug(
                                "getBeanPropertyType for {} found getter for tmpFieldName: {}, return type: {}",
                                i,
                                tmpFieldName,
                                tmpClass
                            )
                        } else {
                            logger.warn(
                                "getBeanPropertyType for {} no getter exists for tmpFieldName: {}",
                                i,
                                tmpFieldName
                            )
                            tmpClass = null
                        }
                        break
                    }
                }
                i++
            }
            beanPropertyType = tmpClass
        } catch (e: IntrospectionException) {
            if (silent) {
                logger.error(
                    "getBeanPropertyType, failed getting type bean property: {}#{}",
                    clazz.canonicalName,
                    fieldName,
                    e
                )
            } else {
                throw RuntimeException("failed getting bean property type", e)
            }
        }
        logger.debug("getBeanPropertyType found for {}#{}: {}", clazz.canonicalName, fieldName, beanPropertyType)
        return beanPropertyType
    }

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