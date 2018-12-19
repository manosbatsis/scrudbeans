/**
 *
 * Restdude
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
package com.github.manosbatsis.scrudbeans.jpa.metadata;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.manosbatsis.scrudbeans.api.domain.MetadatumModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.util.CollectionUtils;

public class MetadataMapDeserializer extends JsonDeserializer<Map<String, ?>>
		implements ContextualDeserializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MetadataMapDeserializer.class);

	private Class<?> targetType;

	private ObjectMapper mapper;

	public MetadataMapDeserializer() {
		super();
	}

	public MetadataMapDeserializer(Class<?> targetType, ObjectMapper mapper) {
		super();
		this.targetType = targetType;
		this.mapper = mapper;
		// LOGGER.info("constructor, targetType: " + targetType);
	}

	@Override
	public JsonDeserializer<Map<String, ?>> createContextual(
			DeserializationContext ctxt, BeanProperty property)
			throws JsonMappingException {
		return new MetadataMapDeserializer(property.getType().containedType(1)
				.getRawClass(), mapper);
	}

	@Override
	public Map<String, ?> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		// MetadatumModel metadatum = targetType.newInstance();
		// metadatum.setObject(jp.getText());
		// JsonNode node = jp.readValueAsTree();
		// if ("".equals(node.asText()))
		// return null;
		if (this.mapper == null) {
			this.mapper = new ObjectMapper();
		}
		TypeFactory typeFactory = mapper.getTypeFactory();
		MapType stringValueMapType = typeFactory.constructMapType(
				HashMap.class, String.class, String.class);
		HashMap<String, String> stringValueMap = mapper.readValue(jp,
				stringValueMapType);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("deserialize stringValueMap: " + stringValueMap);
		}
		Map<String, MetadatumModel> metadata = new HashMap<String, MetadatumModel>();
		if (!CollectionUtils.isEmpty(stringValueMap)) {
			for (String predicate : stringValueMap.keySet()) {
				try {
					MetadatumModel metadatum = (MetadatumModel) targetType.newInstance();
					metadatum.setPredicate(predicate);
					metadatum.setObject(stringValueMap.get(predicate));
					metadata.put(predicate, metadatum);
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("deserialize returning metadata: " + metadata);
		}
		return metadata;
	}
}