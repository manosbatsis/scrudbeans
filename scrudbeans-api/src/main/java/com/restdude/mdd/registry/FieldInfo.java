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
package com.restdude.mdd.registry;

import java.util.Optional;

import com.restdude.domain.Model;
import org.apache.commons.lang3.StringUtils;

/**
 * Contains metadata for a specific model field
 */
public interface FieldInfo {

	/**
	 * Get the member name
	 */
	String getFieldName();

	/**
	 * Get the member class
	 */
	Class<?> getFieldType();

	/**
	 * Get the member mapping type
	 */
	FieldMappingType getFieldMappingType();

	/**
	 * Get the member model type for example <code>Book</code> for a book entity or entity collection
	 */
	Class<? extends Model> getFieldModelType();

	/**
	 * Get the reverse relationship path if any
	 */
	Optional<String> getReverseFieldName();

	/**
	 * Set the reverse relationship path (internal use only)
	 */
	void setReverseFieldName(String reverseFieldName);

	/**
	 * Get the cascade type if any
	 */
	javax.persistence.CascadeType[] getCascadeTypes();

	/**
	 * Get the read method (i.e. getter) if any
	 */
	java.lang.reflect.Method getGetterMethod();

	/**
	 * Get the write method (i.e. setter) if any
	 */
	java.lang.reflect.Method getSetterMethod();

	/**
	 * Get the member has a read method (i.e. getter)
	 */
	boolean isGetter();

	/**
	 * Get the member has a write method (i.e. setter)
	 */
	boolean isSetter();

	/**
	 * Whether the member is lazily loaded
	 */
	boolean isLazy();

	/**
	 * Get the related model type info, if any
	 */
	ModelInfo getRelatedModelInfo();

	/**
	 * Set the related model type info (internal use only)
	 */
	void setRelatedModelInfo(ModelInfo related);

	/**
	 * Whether a link can be generated for the member
	 */
	default boolean isLinkableResource() {
		return this.getRelatedModelInfo() != null
				&& this.getRelatedModelInfo().isLinkableResource()
				&& (this.isToOne() || (this.isOneToMany() && StringUtils.isNotBlank(this.getReverseFieldName().orElse(null))));
	}

	/**
	 * Whether the member is the inverse part of a (JPA) relationship
	 */
	boolean isInverse();

	/**
	 * Whether the member is OneToOne
	 */
	default boolean isOneToOne() {
		return FieldMappingType.ONE_TO_ONE.equals(this.getFieldMappingType());
	}

	/**
	 * Whether the member is ManyToOne
	 */
	default boolean isManyToOne() {
		return FieldMappingType.MANY_TO_ONE.equals(this.getFieldMappingType());
	}

	/**
	 * Whether the member is ManyToOne or OneToOne
	 */
	default boolean isToOne() {
		return this.isOneToOne() || this.isManyToOne();
	}

	/**
	 * Whether the member is OneToMany
	 */
	default boolean isOneToMany() {
		return FieldMappingType.ONE_TO_MANY.equals(this.getFieldMappingType());
	}

	/**
	 * Whether the member is ManyToMany
	 */
	default boolean isManyToMany() {
		return FieldMappingType.MANY_TO_MANY.equals(this.getFieldMappingType());
	}

	/**
	 * Whether the member is ManyToMany or OneToMany
	 */
	default boolean isToMany() {
		return this.isManyToMany() || this.isOneToMany();
	}
}
