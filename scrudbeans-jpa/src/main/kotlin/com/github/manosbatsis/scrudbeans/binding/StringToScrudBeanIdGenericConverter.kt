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

import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistry
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter

class StringToScrudBeanIdGenericConverter(
    private val identifierAdapterRegistry: IdentifierAdapterRegistry
) : GenericConverter {

    private val convertibleTypesCache: MutableSet<GenericConverter.ConvertiblePair> by lazy {
        identifierAdapterRegistry.getServices()
            .filter { it.identifierAdapter.isCompositeId }
            .map {
                listOf(
                    GenericConverter.ConvertiblePair(String::class.java, it.identifierAdapter.entityIdType),
                    GenericConverter.ConvertiblePair(java.lang.String::class.java, it.identifierAdapter.entityIdType)
                )
            }.flatten().toMutableSet()
    }

    override fun getConvertibleTypes(): MutableSet<GenericConverter.ConvertiblePair> = convertibleTypesCache

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        return if (source == null || source.toString().isNullOrBlank()) null
        else identifierAdapterRegistry.getServiceFor(targetType.type)
            .identifierAdapter.buildIdFromString(source.toString())
    }
}