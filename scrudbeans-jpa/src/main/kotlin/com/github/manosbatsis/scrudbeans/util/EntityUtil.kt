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

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry
import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import java.io.Serializable
import java.util.concurrent.ConcurrentHashMap

object EntityUtil {
    private val LOGGER = LoggerFactory.getLogger(EntityUtil::class.java)
    private val scrudBeanTypes: MutableMap<Class<*>, Boolean> = ConcurrentHashMap()
    fun isScrudBean(domainType: Class<*>): Boolean =
        scrudBeanTypes.getOrPut(domainType){
            domainType.isAnnotationPresent(ScrudBean::class.java)
        }

    fun getNullPropertyNames(source: Any): List<String> {
        val beanWrapper = BeanWrapperImpl(source)
        return beanWrapper.propertyDescriptors
            .filter { isNullOrUnreadableProperty(beanWrapper, it.name) }
            .map { it.name }.distinct()
    }

    fun isNullOrUnreadableProperty(source: BeanWrapper, name: String?): Boolean {
        var nullOrUnreadable = !source.isReadableProperty(name!!)
        if (!nullOrUnreadable) {
            val descriptor = source.getPropertyDescriptor(name)
            val getter = descriptor.readMethod
            if (getter == null || getter.isAnnotationPresent(JsonIgnore::class.java) || source.getPropertyValue(
                    name
                ) == null
            ) {
                nullOrUnreadable = true
            }
        }
        LOGGER.debug("isNullOrUnreadableProperty {}: {}", name, nullOrUnreadable)
        return nullOrUnreadable
    }


    fun <PK : Serializable?> idOrNull(entity: Any?): PK? {
        return entity?.let {
                IdentifierAdaptersRegistry.getAdapterForClass(entity.javaClass)
                    .readId(entity) as PK
        }
    }

    fun idOrNEmpty(entity: Any?): String {
        return idOrNull(entity) ?: StringUtils.EMPTY
    }
}