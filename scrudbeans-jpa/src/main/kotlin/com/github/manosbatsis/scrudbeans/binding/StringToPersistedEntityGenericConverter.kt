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
package com.github.manosbatsis.scrudbeans.binding

import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter

/**
 * Convert string-serialized identifiers (or fragments thereof) to entity instances
 * by loading them using the corresponding [JpaPersistableModelService]
 */
class StringToPersistedEntityGenericConverter(
    private val entityServices: List<JpaPersistableModelService<*,*>>
): GenericConverter {

    private val convertibleTypesCache: MutableSet<GenericConverter.ConvertiblePair> by lazy {
        val cache = entityServices.map {
            GenericConverter.ConvertiblePair(String::class.java, it.identifierAdapter.entityType)
        }.toMutableSet()
        entityServices.mapTo(cache){
            GenericConverter.ConvertiblePair(java.lang.String::class.java, it.identifierAdapter.entityType)
        }
        cache
    }

    override fun getConvertibleTypes(): MutableSet<GenericConverter.ConvertiblePair> = convertibleTypesCache

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        if(source == null || source.toString().isNullOrBlank()) return null

        val targetTypeActual = targetType.type
        return entityServices.find { it.identifierAdapter.entityType.isAssignableFrom(targetTypeActual) }
            ?.getByIdAsString(source.toString())
    }

}