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

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.BeanProperty
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.deser.ContextualDeserializer
import com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier
import org.slf4j.LoggerFactory
import java.io.IOException
import java.util.concurrent.ConcurrentHashMap

/**
 * Deserializer for [EmbeddableCompositeIdentifier] instances
 * @param <T> the identifier implementation type
</T> */
class EmbeddableCompositeIdDeserializer<T : EmbeddableCompositeIdentifier>(
    val targetType: Class<*>
) : JsonDeserializer<T>(),ContextualDeserializer {

    override fun handledType(): Class<*> {
        return EmbeddableCompositeIdentifier::class.java
    }

    @Throws(IOException::class, JsonProcessingException::class)
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): T {
        val oc = p.codec
        val id = oc.readValue(p, String::class.java)
        return try {
            (targetType.newInstance() as T).also {
                it.init(id)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            throw RuntimeException(
                StringBuffer("Failed to deserialize with id: ").append(id).append(", class: ")
                    .append(targetType.toString()).toString(), e
            )
        }
    }

    @Throws(JsonMappingException::class)
    override fun createContextual(
        deserializationContext: DeserializationContext,
        beanProperty: BeanProperty
    ): JsonDeserializer<T> {

        // get deserializer for target type if available
        val targetClass = beanProperty.type.rawClass
        var deserializer: JsonDeserializer<T>? = typeSerializers[targetClass] as JsonDeserializer<T>?

        // otherwise create and configure a new instance for the target type
        if (deserializer == null) {
            deserializer = EmbeddableCompositeIdDeserializer(targetClass)
            typeSerializers[targetClass] = deserializer
        }
        return deserializer
    }

    companion object {
        private val LOGGER = LoggerFactory.getLogger(EmbeddableCompositeIdDeserializer::class.java)
        private val typeSerializers = ConcurrentHashMap<Class<*>, JsonDeserializer<*>>()
    }
}