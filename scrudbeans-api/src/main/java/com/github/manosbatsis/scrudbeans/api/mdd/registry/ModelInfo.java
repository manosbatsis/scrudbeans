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
package com.github.manosbatsis.scrudbeans.api.mdd.registry;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;

import java.io.Serializable;
import java.util.Set;

/**
 * Contains metadata for a specific Model class.
 */
public interface ModelInfo<T, PK extends Serializable> {
    String getRequestMapping();

    void setRequestMapping(String pattern);

    String getParentPath(String defaultValue);

    String getBasePath(String defaultValue);

    FieldInfo getField(String fieldName);

	Boolean isLinkableResource();

	Class<T> getModelType();

	ScrudBean getScrudBean();

	String getPackageName();

	String getBeansBasePackage();

	String getUriComponent();

	String getParentApplicationPath();

	String getBasePath();

	boolean isJpaEntity();

	FieldInfo getIdField();

	Set<String> getAllFieldNames();

	Set<String> getSimpleFieldNames();

	Set<String> getToOneFieldNames();

	Set<String> getToManyFieldNames();

	Set<String> getInverseFieldNames();

	IPredicateFactory getPredicateFactory();

	Class<?> getModelControllerType();

	void setPredicateFactory(IPredicateFactory predicateFactory);

	void setModelControllerType(Class<?> modelControllerType);

}
