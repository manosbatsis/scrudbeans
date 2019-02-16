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
package com.github.manosbatsis.scrudbeans.jpa.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudRelatedBean;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;
import com.github.manosbatsis.scrudbeans.common.util.ClassUtils;
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractModelServiceBackedController;
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractPersistableModelController;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.ManyToAny;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

/**
 * Adapter-ish context class for classes with {@link ScrudBean}
 * and {@link ScrudRelatedBean}
 * annotations.
 */
public final class ModelContext {

	private static final String AUDITABLE2 = "auditable";

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelContext.class);

	@Getter
	private ModelInfo modelInfo;


	private Class<?> parentClass;

	private String parentProperty;

	@Getter
	private String generatedClassNamePrefix;

	@Getter
	private Class<?> repositoryType, serviceInterfaceType, serviceImplType;

	@Getter @Setter
	private BeanDefinition repositoryDefinition, serviceDefinition;

	@Getter
	private BeanDefinition controllerDefinition;

	private Map<String, Object> apiAnnotationMembers;

	private ScrudBean scrudBean;

	@Getter
	private boolean auditable;


	private ModelContext() {

	}

	public ModelContext(@NonNull ModelInfo modelInfo) {
		this.modelInfo = modelInfo;
		Class<?> modelClass = modelInfo.getModelType();

		this.generatedClassNamePrefix = modelClass.getSimpleName().replace("Model", "").replace("Entity", "");
		this.scrudBean = modelClass.getAnnotation(ScrudBean.class);
		this.generatedClassNamePrefix = modelClass.getSimpleName().replace("Model", "").replace("Entity", "");
		if (this.scrudBean != null) {
			this.parentClass = null;
			this.parentProperty = null;
		}
	}

	public List<Class<?>> getGenericTypes() {
		List<Class<?>> genericTypes = new LinkedList<Class<?>>();
		genericTypes.add(this.getModelType());
		if (this.getModelIdField() != null) {
			genericTypes.add(this.getModelIdField().getFieldType());
		}
		return genericTypes;
	}


	public Class getControllerSuperClass() {
		Class sClass = ClassUtils.getClass(this.scrudBean.controllerSuperClass());
		if (sClass == null || Object.class.equals(sClass)) {
			sClass = this.modelInfo.isJpaEntity() ? AbstractPersistableModelController.class : AbstractModelServiceBackedController.class;
		}
		return sClass;
	}

	public void setServiceInterfaceType(Class<?> serviceInterfaceType) {
		this.serviceInterfaceType = serviceInterfaceType;
	}

	public void setServiceImplType(Class<?> serviceImplType) {
		this.serviceImplType = serviceImplType;
	}

	public void setRepositoryType(Class<?> repositoryType) {
		this.repositoryType = repositoryType;
	}

	public boolean isNested() {
		return parentClass != null;
	}

	public boolean isNestedCollection() {
		if (!isNested()) {
			return false;
		}
		Class<?> modelType = this.modelInfo.getModelType();
		ScrudRelatedBean anr = modelType.getAnnotation(ScrudRelatedBean.class);
		Assert.notNull(anr, "Not a nested resource");

		String parentProperty = anr.parentProperty();
		Field field = ReflectionUtils.findField(modelType, parentProperty);
		if (hasAnnotation(field, OneToOne.class, org.hibernate.mapping.OneToOne.class)) {
			return false;
		}
		else if (hasAnnotation(field, ManyToOne.class, org.hibernate.mapping.ManyToOne.class,
				ManyToMany.class, ManyToAny.class)) { // TODO handle more mappings here?
			return true;
		}

		throw new IllegalStateException("No known mapping found");

	}

	private boolean hasAnnotation(Field field, Class<?>... annotations) {

		for (Class<?> a : annotations) {
			if (field.isAnnotationPresent((Class<Annotation>) a)) {
				return true;
			}
		}
		return false;
	}


	public Map<String, Object> getApiAnnotationMembers() {
		// init if needed
		if (this.apiAnnotationMembers == null) {
			apiAnnotationMembers = new HashMap<>();

			Class<?> modelType = this.getModelType();
			ScrudBean resource = modelType.getAnnotation(ScrudBean.class);
			if (resource != null) {
				// auditable?
				apiAnnotationMembers.put(AUDITABLE2, resource.auditable());
				// get tags (grouping key, try API name)
				if (StringUtils.isNotBlank(resource.apiName())) {
					String[] tags = {resource.apiName()};
					apiAnnotationMembers.put("tags", tags);
				}
				// or pathFragment
				else if (StringUtils.isNotBlank(resource.pathFragment())) {

					String[] tags = {resource.pathFragment()};
					apiAnnotationMembers.put("tags", tags);
				}
				// or simple name
				else {
					String[] tags = {StringUtils.join(
							StringUtils.splitByCharacterTypeCamelCase(modelType.getSimpleName()),
							' '
					)};
					apiAnnotationMembers.put("tags", tags);
				}
				// add description
				if (StringUtils.isNotBlank(resource.apiDescription())) {
					apiAnnotationMembers.put("description", resource.apiDescription());
				}
			}
			else {
				throw new IllegalStateException("Not an entity");
			}
		}

		return apiAnnotationMembers.size() > 0 ? apiAnnotationMembers : null;
	}


	public FieldInfo getModelIdField() {
		return this.modelInfo.getIdField();
	}

	public Class<?> getModelType() {
		return this.modelInfo.getModelType();
	}

	public String getName() {
		return this.modelInfo.getPackageName();
	}

	public String getBeansBasePackage() {
		return this.modelInfo.getBeansBasePackage();
	}

	;

	public void setPredicateFactory(IPredicateFactory predicateFactory) {
		this.modelInfo.setPredicateFactory(predicateFactory);
	}

	public void setControllerDefinition(BeanDefinition controllerDefinition) {
		this.controllerDefinition = controllerDefinition;
		this.modelInfo.setModelControllerType(ClassUtils.getClass(controllerDefinition.getBeanClassName()));
	}

}
