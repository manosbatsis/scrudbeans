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

import com.github.manosbatsis.scrudbeans.api.exception.NotFoundException
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistry
import com.github.manosbatsis.scrudbeans.service.JpaEntityService
import org.springframework.core.convert.TypeDescriptor
import org.springframework.core.convert.converter.GenericConverter

class StringToScrudBeanGenericConverter(
    private val identifierAdapterRegistry: IdentifierAdapterRegistry
) : GenericConverter {

    private val convertibleTypesCache: MutableSet<GenericConverter.ConvertiblePair> by lazy {
        val services = identifierAdapterRegistry.getServices()
        services.map {
            listOf(
                GenericConverter.ConvertiblePair(String::class.java, it.identifierAdapter.entityType),
                GenericConverter.ConvertiblePair(java.lang.String::class.java, it.identifierAdapter.entityType)
            )
        }.flatten().toMutableSet()
    }

    override fun getConvertibleTypes(): MutableSet<GenericConverter.ConvertiblePair> = convertibleTypesCache

    override fun convert(source: Any?, sourceType: TypeDescriptor, targetType: TypeDescriptor): Any? {
        return if (source == null || source.toString().isNullOrBlank()) null
        else loadFromStringId(identifierAdapterRegistry.getServiceForEntityType(targetType.type), source.toString())
    }

    private fun <T : Any, S : Any> loadFromStringId(service: JpaEntityService<T, S>, source: String): T {
        val identifierAdapter: IdentifierAdapter<T, S> = service.identifierAdapter
        val id = identifierAdapter.buildIdFromString(source)
            ?: throw NotFoundException("No match found for identifier's string representation: $source")
        return service.getById(id)
    }
}
