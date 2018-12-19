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
package com.github.manosbatsis.scrudbeans.jpa.registry;

import java.beans.PropertyDescriptor;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ComputedRelationship;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldMappingType;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.common.util.ClassUtils;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.hibernate.annotations.Formula;

import org.springframework.core.GenericTypeResolver;

/**
 * Simple implementation of {@link FieldInfo}
 */
@Slf4j
public class FieldInfoImpl implements FieldInfo {


	public static FieldInfo create(@NonNull Class<? extends Model> modelType, PropertyDescriptor property) {
		FieldInfo fieldInfo = null;
		Field field = FieldUtils.getField(modelType, property.getName(), true);

		// ensure proper bean property, i.e. read/write
		Method getter = property.getReadMethod();
		Method setter = property.getWriteMethod();
		if (field != null && getter != null && setter != null) {
			//log.debug("create, modelType: {}, property: {}", modelType, property);
			fieldInfo = new FieldInfoImpl(modelType, property, field, getter, setter);
		}
		else {
			// log.debug("create, ignoring modelType: {}, property: {}", modelType, property);
		}
		return fieldInfo;

	}

	@Getter private boolean relationship;

	@Getter private String fieldName;

	@Getter private Class<?> fieldType;

	@Getter private FieldMappingType fieldMappingType;

	@Getter private Class<? extends Model> fieldModelType;

	@Setter private String reverseFieldName = null;

	@Getter private boolean inverse = false;

	@Getter private CascadeType[] cascadeTypes;

	@Getter private Method getterMethod;

	@Getter private Method setterMethod;

	@Getter private boolean getter;

	@Getter private boolean setter;

	@Getter private boolean lazy = false;

	@Getter private ModelInfo relatedModelInfo;


	private FieldInfoImpl(@NonNull Class<? extends Model> modelType, @NonNull PropertyDescriptor property, @NonNull Field field, @NonNull Method getter, @NonNull Method setter) {
		// add basic info
		this.fieldType = property.getPropertyType();
		this.fieldName = property.getName();

		this.getterMethod = getter;
		this.setterMethod = setter;

		this.getter = getter != null;
		this.setter = setter != null;

		scanMappings(field, getter, setter);

		// set the Modelnfo for if a relationship
		if (this.isLinkableResource()) {//if(this.isRelationship()){
			if (Model.class.isAssignableFrom(this.fieldType)) {
				this.fieldModelType = (Class<? extends Model>) this.fieldType;
			}
			// if collection but not a Map
			else if (Collection.class.isAssignableFrom(this.fieldType) && !Map.class.isAssignableFrom(this.fieldType)) {
				ParameterizedType pType = (ParameterizedType) field.getGenericType();
				log.debug("FieldInfoImpl, fieldType: {}, pType: {}", fieldType, pType);
				Map<TypeVariable<?>, Type> types = TypeUtils.getTypeArguments(pType);
				for (TypeVariable<?> var : types.keySet()) {

					Type t = types.get(var);
					String tName = t.getTypeName();
					log.debug("FieldInfoImpl, var: {}, t.getTypeName: '{}', tName: '{}'", var, t.getTypeName(), tName);
					if (tName.contains(".")) {
						this.fieldModelType = (Class<? extends Model>) ClassUtils.getClass(tName);
					}
				}
				if (this.fieldModelType == null) {
					String tName = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0].getTypeName();
					log.debug("FieldInfoImpl, tName: '{}'", tName);
					if (tName.contains(".")) {
						this.fieldModelType = (Class<? extends Model>) ClassUtils.getClass(tName);
					}
				}
				Class<?> resolved = GenericTypeResolver.resolveTypeArgument(fieldType, pType.getClass());
				log.debug("FieldInfoImpl, resolved: {}", this.fieldModelType);

			}
			log.debug("FieldInfoImpl, resolved fieldName: {}, fieldType: {}, fieldModelType: {}", this.fieldName, this.fieldType, this.fieldModelType);
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setRelatedModelInfo(ModelInfo modelInfo) {
		this.relatedModelInfo = modelInfo;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return new ToStringBuilder(this)
				.append("fieldName", this.fieldName)
				.append("fieldType", this.fieldType)
				.append("fieldMappingType", this.fieldMappingType)
				.append("fieldModelType", this.fieldModelType)
				.append("reverseFieldName", this.reverseFieldName)
				.append("inverse", this.inverse)
				.append("getter", this.getter)
				.append("setter", this.setter)
				.append("lazy", this.lazy)
				.append("linkableResource", this.isLinkableResource())
				.toString();
	}

	protected void scanMappings(@NonNull AccessibleObject... fieldsOrMethods) {

		// scan for JPA annotations
		//
		Optional<Id> id = Optional.empty();
		Optional<EmbeddedId> embeddedId = Optional.empty();
		Optional<ManyToMany> manyToMany = Optional.empty();
		Optional<ManyToOne> manyToOne = Optional.empty();
		Optional<OneToMany> oneToMany = Optional.empty();
		Optional<OneToOne> oneToOne = Optional.empty();
		Optional<ComputedRelationship> computedRelationship = Optional.empty();
		Optional<Formula> formula = Optional.empty();
		Optional<Transient> tranzient = Optional.empty();

		for (AccessibleObject field : fieldsOrMethods) {
			// log.debug("scanMappings, field: {}", field);
			if (field.isAnnotationPresent(Id.class)) {
				id = Optional.ofNullable(field.getAnnotation(Id.class));
				// log.debug("scanMappings, found Id field: {}", field);
			}
			else if (field.isAnnotationPresent(EmbeddedId.class)) {
				embeddedId = Optional.ofNullable(field.getAnnotation(EmbeddedId.class));
				// log.debug("scanMappings, found Id field: {}", field);
			}
			else if (field.isAnnotationPresent(ManyToMany.class)) {
				manyToMany = Optional.ofNullable(field.getAnnotation(ManyToMany.class));
			}
			else if (field.isAnnotationPresent(ManyToOne.class)) {
				manyToOne = Optional.ofNullable(field.getAnnotation(ManyToOne.class));
			}
			else if (field.isAnnotationPresent(OneToMany.class)) {
				oneToMany = Optional.ofNullable(field.getAnnotation(OneToMany.class));
			}
			else if (field.isAnnotationPresent(OneToOne.class)) {
				oneToOne = Optional.ofNullable(field.getAnnotation(OneToOne.class));
			}
			else if (field.isAnnotationPresent(ComputedRelationship.class)) {
				computedRelationship = Optional.ofNullable(field.getAnnotation(ComputedRelationship.class));
			}
			else if (field.isAnnotationPresent(Transient.class)) {
				tranzient = Optional.ofNullable(field.getAnnotation(Transient.class));
			}
		}

		// process field
		this.relationship = Stream.of(manyToMany, manyToOne, oneToMany, oneToOne, computedRelationship).anyMatch(x -> x.isPresent());
		if (this.fieldName != null && !this.fieldName.equals("class")) {

			if (id.isPresent() || embeddedId.isPresent()) {
				fieldMappingType = FieldMappingType.ID;
			}
			else if (tranzient.isPresent()) {
				fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_NONE : FieldMappingType.NONE;
			}
			else if (this.relationship) {
				if (oneToMany.isPresent()) {
					this.fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_ONE_TO_MANY : FieldMappingType.ONE_TO_MANY;
					this.reverseFieldName = oneToMany.get().mappedBy();
					this.cascadeTypes = oneToMany.get().cascade();
					this.lazy = oneToMany.get().fetch().equals(FetchType.LAZY);
				}
				else if (oneToOne.isPresent()) {
					this.fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_ONE_TO_ONE : FieldMappingType.ONE_TO_ONE;
					this.reverseFieldName = oneToOne.get().mappedBy();
					this.cascadeTypes = oneToOne.get().cascade();
					this.lazy = oneToOne.get().fetch().equals(FetchType.LAZY);
				}
				else if (manyToMany.isPresent()) {
					this.fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_MANY_TO_MANY : FieldMappingType.MANY_TO_MANY;
					this.reverseFieldName = manyToMany.get().mappedBy();
					this.cascadeTypes = manyToMany.get().cascade();
					this.lazy = manyToMany.get().fetch().equals(FetchType.LAZY);
				}
				else if (manyToOne.isPresent()) {
					this.fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_MANY_TO_ONE : FieldMappingType.MANY_TO_ONE;
					this.reverseFieldName = "";
					this.cascadeTypes = manyToOne.get().cascade();
					this.lazy = manyToOne.get().fetch().equals(FetchType.LAZY);
				}
				else {
					this.fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_NONE : FieldMappingType.NONE;
					this.reverseFieldName = "";
					this.cascadeTypes = new CascadeType[0];
				}
				this.inverse = StringUtils.isNotEmpty(this.reverseFieldName);
			}
			else if (formula.isPresent()) {
				fieldMappingType = FieldMappingType.CALCULATED_SIMPLE;
			}
			else {
				// TODO: simple, formula, list of values
				fieldMappingType = computedRelationship.isPresent() ? FieldMappingType.CALCULATED_SIMPLE : FieldMappingType.SIMPLE;

			}
		}

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<String> getReverseFieldName() {
		return Optional.ofNullable(this.reverseFieldName);
	}

}
