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

import com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier
import org.springframework.core.convert.converter.Converter
import org.springframework.core.convert.converter.ConverterFactory

/**
 * Converter factory targeting [EmbeddableCompositeIdentifier] instances
 */
class StringToEmbeddableCompositeIdConverterFactory : ConverterFactory<String, EmbeddableCompositeIdentifier> {
    override fun <T : EmbeddableCompositeIdentifier> getConverter(targetType: Class<T>): Converter<String, T> {
        return StringToEmbeddableManyToManyIdConverter(targetType)
    }

    /**
     * Converter for [EmbeddableCompositeIdentifier] identifier instances
     * @param <T> the identifier implementation type
    </T> */
    private inner class StringToEmbeddableManyToManyIdConverter<T : EmbeddableCompositeIdentifier>(private val targetType: Class<*>) :
        Converter<String, T> {
        override fun convert(id: String): T? {
            val `object`: T
            try {
                `object` = targetType.getConstructor().newInstance() as T
                `object`!!.init(id)
            } catch (e: InstantiationException) {
                throw RuntimeException(
                    StringBuffer("Failed to deserialize with id: ").append(id).append(", class: ")
                        .append(targetType.toString()).toString(), e
                )
            } catch (e: IllegalAccessException) {
                throw RuntimeException(
                    StringBuffer("Failed to deserialize with id: ").append(id).append(", class: ")
                        .append(targetType.toString()).toString(), e
                )
            }
            return `object`
        }

    }
}