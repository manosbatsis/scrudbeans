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

import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.SerializerProvider
import com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier
import java.io.IOException

/**
 * Serializer for [EmbeddableCompositeIdentifier] instances
 */
class EmbeddableCompositeIdSerializer : JsonSerializer<EmbeddableCompositeIdentifier>() {
    @Throws(IOException::class, JsonProcessingException::class)
    override fun serialize(id: EmbeddableCompositeIdentifier, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeString(id.toStringRepresentation())
    }

    override fun handledType(): Class<EmbeddableCompositeIdentifier>? {
        return EmbeddableCompositeIdentifier::class.java
    }
}