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
package com.github.manosbatsis.scrudbeans.jpa.uischema.serializer;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudResource;
import com.github.manosbatsis.scrudbeans.jpa.uischema.annotation.FormSchemaEntry;
import com.github.manosbatsis.scrudbeans.jpa.uischema.annotation.FormSchemas;
import com.github.manosbatsis.scrudbeans.jpa.uischema.model.UiSchema;
import org.apache.commons.beanutils.PropertyUtilsBean;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Deprecated
public class UiSchemaSerializer extends JsonSerializer<UiSchema> {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(UiSchemaSerializer.class);

	private static final char quote = '\"';

	private static final char space = ' ';

	private static final char colon = ':';

	private static final char comma = ',';

	private static Map<String, String> fieldTypes = new HashMap<String, String>();

	static {
//			  "fields" : {
//			    "aliases" : {
//			      "fieldType" : "Set"
//			    },
//			    "createdDate" : {
//			      "fieldType" : "DateTime"
//			    },
//			    "createdBy" : {
//			      "fieldType" : "User"
//			    },
//			    "lastModifiedDate" : {
//			      "fieldType" : "DateTime"
//			    },
//			    "domain" : {
//			      "fieldType" : "String"
//			    },
//			    "formSchema" : {
//			      "fieldType" : "FormSchema"
//			    },
//			    "lastModifiedBy" : {
//			      "fieldType" : "User"
//			    },
//			    "name" : {
//			      "fieldType" : "String"
//			    },
//			    "id" : {
//			      "fieldType" : "String"
//			    }
//			  }
//			}
		fieldTypes.put("String", "String");

		fieldTypes.put("Short", "Number");
		fieldTypes.put("Integer", "Number");

		fieldTypes.put("Float", "Decimal");
		fieldTypes.put("BigDecimal", "Decimal");
		fieldTypes.put("Double", "Decimal");

		fieldTypes.put("DateTime", "Datetime");
		fieldTypes.put("Datetime", "Datetime");
		fieldTypes.put("Date", "Date");
	}

	// datatypes

	private static final HashMap<String, String> CONFIG_CACHE = new HashMap<String, String>();

	private static List<String> ignoredFieldNames = new LinkedList<String>();

	static {
		ignoredFieldNames.add("new");
		ignoredFieldNames.add("class");
		ignoredFieldNames.add("metadataDomainClass");
	}

	@Override
	public void serialize(UiSchema schema, JsonGenerator jgen,
			SerializerProvider provider) throws IOException,
			JsonGenerationException {
		try {
			Class domainClass = schema.getDomainClass();

			if (null == domainClass) {
				throw new RuntimeException("formSchema has no domain class set");
			}
			else {
				// start json
				jgen.writeStartObject();

				// write superclass hint
				ScrudResource superResource = (ScrudResource) domainClass.getSuperclass().getAnnotation(ScrudResource.class);
				if (superResource != null) {
					jgen.writeFieldName("superPathFragment");
					jgen.writeString(superResource.pathFragment());
				}

				// write pathFragment
				ScrudResource scrudResource = (ScrudResource) domainClass.getAnnotation(ScrudResource.class);
				jgen.writeFieldName("pathFragment");
				jgen.writeString(scrudResource.pathFragment());

				// write simple class name
				jgen.writeFieldName("simpleClassName");
				jgen.writeString(domainClass.getSimpleName());


				// start fields
				jgen.writeFieldName("fields");
				jgen.writeStartObject();

				PropertyDescriptor[] descriptors = new PropertyUtilsBean()
						.getPropertyDescriptors(domainClass);

				for (int i = 0; i < descriptors.length; i++) {

					PropertyDescriptor descriptor = descriptors[i];
					String name = descriptor.getName();
					if (!ignoredFieldNames.contains(name)) {
						String fieldValue = this.getDataType(domainClass, descriptor, name);
						if (StringUtils.isNotBlank(fieldValue)) {
							jgen.writeFieldName(name);
							jgen.writeStartObject();
							jgen.writeFieldName("fieldType");
							jgen.writeString(fieldValue);
							jgen.writeEndObject();
						}
					}

				}
				// end fields
				jgen.writeEndObject();
				// end json
				jgen.writeEndObject();

			}

		}
		catch (Exception e) {
			new RuntimeException("Failed serializing form schema", e);
		}
	}

	private String getDataType(Class domainClass, PropertyDescriptor descriptor, String name) {
		return fieldTypes.get(descriptor.getPropertyType().getSimpleName());
	}

	private static String getFormFieldConfig(Class domainClass, PropertyDescriptor descriptor, String fieldName) {
		String formSchemaJson = null;
		Field field = null;
		StringBuffer formConfig = new StringBuffer();
		String key = domainClass.getName() + "#" + fieldName;
		String cached = CONFIG_CACHE.get(key);
		if (StringUtils.isNotBlank(cached)) {
			formConfig.append(cached);
		}
		else {
			Class tmpClass = domainClass;
			do {
				for (Field tmpField : tmpClass.getDeclaredFields()) {
					String candidateName = tmpField.getName();
					if (candidateName.equals(fieldName)) {
						field = tmpField;
						FormSchemas formSchemasAnnotation = null;
						if (field.isAnnotationPresent(FormSchemas.class)) {
							formSchemasAnnotation = field.getAnnotation(FormSchemas.class);
							FormSchemaEntry[] formSchemas = formSchemasAnnotation.value();
							LOGGER.info("getFormFieldConfig, formSchemas: " + formSchemas);
							if (formSchemas != null) {
								for (int i = 0; i < formSchemas.length; i++) {
									if (i > 0) {
										formConfig.append(comma);
									}
									FormSchemaEntry formSchemaAnnotation = formSchemas[i];
									LOGGER.info("getFormFieldConfig, formSchemaAnnotation: " + formSchemaAnnotation);
									appendFormFieldSchema(formConfig, formSchemaAnnotation.state(), formSchemaAnnotation.json());
								}
							}
							//formConfig = formSchemasAnnotation.json();
						}
						else {
							appendFormFieldSchema(formConfig, FormSchemaEntry.STATE_DEFAULT, FormSchemaEntry.TYPE_STRING);
						}
						break;
					}
				}
				tmpClass = tmpClass.getSuperclass();
			} while (tmpClass != null && field == null);
			formSchemaJson = formConfig.toString();
			CONFIG_CACHE.put(key, formSchemaJson);
		}

		return formSchemaJson;
	}

	private static void appendFormFieldSchema(
			StringBuffer formConfig, String state, String json) {
		formConfig.append(quote).append(state).append(quote).append(colon).append(json);
	}
}