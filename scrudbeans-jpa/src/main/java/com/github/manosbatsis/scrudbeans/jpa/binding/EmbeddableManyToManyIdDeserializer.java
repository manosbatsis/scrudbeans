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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.jpa.binding;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractEmbeddableManyToManyIdentifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmbeddableManyToManyIdDeserializer<T extends AbstractEmbeddableManyToManyIdentifier> extends JsonDeserializer<T> implements ContextualDeserializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddableManyToManyIdDeserializer.class);

	private static final ConcurrentHashMap<Class, JsonDeserializer> typeSerializers = new ConcurrentHashMap<>();

	private Class targetType;

	public EmbeddableManyToManyIdDeserializer() {
	}

	public EmbeddableManyToManyIdDeserializer(Class<?> targetType) {
		this.targetType = targetType;
	}

	@Override
	public Class handledType() {
		return AbstractEmbeddableManyToManyIdentifier.class;
	}

	@Override
	public T deserialize(JsonParser p, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		ObjectCodec oc = p.getCodec();
		String id = oc.readValue(p, String.class);
		T object;
		try {
			object = (T) targetType.newInstance();
			object.init(id);
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(new StringBuffer("Failed to deserialize with id: ").append(id).append(", class: ").append(targetType.toString()).toString(), e);
		}
		return object;
	}


	@Override
	public JsonDeserializer<T> createContextual(DeserializationContext deserializationContext, BeanProperty beanProperty) throws JsonMappingException {

		// get deserializer for target type if available
		Class<?> targetClass = beanProperty.getType().getRawClass();
		JsonDeserializer<T> deserializer = typeSerializers.get(targetClass);

		// otherwise create and configure a new instance for the target type
		if (deserializer == null) {
			deserializer = new EmbeddableManyToManyIdDeserializer<T>(targetClass);
			typeSerializers.put(targetClass, deserializer);
		}

		return deserializer;
	}
}