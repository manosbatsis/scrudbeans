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
package com.github.manosbatsis.scrudbeans.jpa.mdd.registry;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Entity;

import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudResource;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;
import com.github.manosbatsis.scrudbeans.jpa.mdd.util.EntityUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Contains metadata for a specific Model class.
 */
@Slf4j
public class ModelInfoImpl<T extends Model<PK>, PK extends Serializable> implements ModelInfo<T, PK> {

	@Getter private final Class<T> modelType;

	@Getter private final ScrudResource scrudResource;

	@Getter private final String packageName;

	@Getter private final String beansBasePackage;

	@Getter private final String uriComponent;

	@Getter private final String parentApplicationPath;

	@Getter private final String basePath;

	@Getter private final boolean jpaEntity;

	@Getter private FieldInfo idField;

	@Getter private final Set<String> allFieldNames = new HashSet<>();

	@Getter private final Set<String> simpleFieldNames = new HashSet<>();

	@Getter private final Set<String> toOneFieldNames = new HashSet<>();

	@Getter private final Set<String> toManyFieldNames = new HashSet<>();

	@Getter private final Set<String> inverseFieldNames = new HashSet<>();

	private final ConcurrentHashMap<String, FieldInfo> fields = new ConcurrentHashMap<>();

	@Getter @Setter
	private IPredicateFactory predicateFactory;

	@Getter @Setter
	private Class<?> modelControllerType;

	private Boolean linkableResource = false;

	@Getter @Setter
	private String requestMapping;


	public ModelInfoImpl(@NonNull Class<T> modelType) {

		// add basic info
		this.modelType = modelType;
		this.packageName = modelType.getPackage().getName();
		this.beansBasePackage = packageName.endsWith(".model") ? packageName.substring(0, packageName.indexOf(".model")) : packageName;
		this.jpaEntity = modelType.isAnnotationPresent(Entity.class);


		scrudResource = modelType.getAnnotation(ScrudResource.class);

		// add controller info
		this.uriComponent = buildUriComponent();
		if (scrudResource != null) {

			this.linkableResource = scrudResource.linkable();
			this.basePath = scrudResource.basePath();
			this.parentApplicationPath = scrudResource.parentPath();
		}
		else {
			this.basePath = "";
			this.parentApplicationPath = "/api/rest";
		}

		this.requestMapping = new StringBuffer("/")
				.append(this.getBasePath(this.basePath))
				.append("/")
				.append(this.parentApplicationPath)
				.append("/")
				.append(this.uriComponent).toString().replaceAll("/{2,}", "/");

		// add fields info
		BeanInfo componentBeanInfo = EntityUtil.getBeanInfo(modelType);
		PropertyDescriptor[] properties = componentBeanInfo.getPropertyDescriptors();
		for (int p = 0; p < properties.length; p++) {
			log.debug("ModelInfo, property: '{}'", properties[p]);
			if (!"class".equals(properties[p].getName())) {
				FieldInfo fieldInfo = FieldInfoImpl.create(modelType, properties[p]);
				if (fieldInfo != null) {
					this.fields.put(fieldInfo.getFieldName(), fieldInfo);
					if (fieldInfo.getFieldMappingType().isId()) {
						this.idField = fieldInfo;
					}
					else if (fieldInfo.getFieldMappingType().isSimple()) {
						this.simpleFieldNames.add(fieldInfo.getFieldName());
					}
					else if (fieldInfo.getFieldMappingType().isToOne()) {
						this.toOneFieldNames.add(fieldInfo.getFieldName());
					}
					else if (fieldInfo.getFieldMappingType().isToMany()) {
						this.toManyFieldNames.add(fieldInfo.getFieldName());
					}

					// note inverse fields
					if (fieldInfo.isInverse()) {
						this.inverseFieldNames.add(fieldInfo.getFieldName());
					}
				}
			}
		}

		log.debug("ModelInfo, domainClass: {}, idField: {}", modelType, this.idField);
	}

	protected String buildUriComponent() {
		ScrudResource meta = this.getModelType().getAnnotation(ScrudResource.class);
		String endpointPathName = meta != null ? meta.pathFragment() : null;
		if (StringUtils.isBlank(endpointPathName)) {
			endpointPathName = this.getModelType().getSimpleName();
			endpointPathName = endpointPathName.toLowerCase().charAt(0) + endpointPathName.substring(1) + "s";
		}
		return endpointPathName;
	}


	@Override
	public String getParentPath(String defaultValue) {
		return StringUtils.isNotEmpty(this.parentApplicationPath) ? parentApplicationPath : defaultValue;
	}

	@Override
	public String getBasePath(String defaultValue) {
		return StringUtils.isNotEmpty(this.basePath) ? basePath : defaultValue;
	}

	@Override
	public FieldInfo getField(String fieldName) {
		return this.fields.get(fieldName);
	}

	@Override
	public Boolean isLinkableResource() {
		return this.linkableResource && this.modelControllerType != null;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("requestMapping", this.getRequestMapping()).append("modelType", this.getModelType()).append("linkable", this.isLinkableResource()).toString();
	}

}
