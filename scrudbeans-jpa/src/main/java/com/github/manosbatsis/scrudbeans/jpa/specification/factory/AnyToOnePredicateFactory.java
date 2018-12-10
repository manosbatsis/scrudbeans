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
package com.github.manosbatsis.scrudbeans.jpa.specification.factory;

import java.io.Serializable;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import com.github.manosbatsis.scrudbeans.api.specification.PredicateOperator;
import com.github.manosbatsis.scrudbeans.common.util.ClassUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.convert.ConversionService;

/**
 * A predicate for members that are Many2one/OneToOne.
 */
@Slf4j
public class AnyToOnePredicateFactory<T extends PersistableModel<PK>, PK extends Serializable> extends AbstractPredicateFactory<T> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AnyToOnePredicateFactory.class);

	private Class<PK> idType;

	public AnyToOnePredicateFactory() {
	}

	@Override
	public Class<?> getValueType() {
		return this.idType;
	}

	@Override
	public Predicate buildPredicate(Root<?> root, CriteriaBuilder cb, String propertyName, Class<T> fieldType, ConversionService conversionService, PredicateOperator operator, List<String> propertyValues) {
		// TODO: move to required constructor
		if (this.idType == null) {
			this.idType = (Class<PK>) ClassUtils.getBeanPropertyType(fieldType, "id", false);
		}
		List<PK> convertedValues = this.convertValues(propertyValues, conversionService, this.idType);
		Path<T> basePath = this.<T>getPath(root, propertyName, fieldType);
		Path<PK> path = basePath.<PK>get("id");
		return buildPredicate(root, cb, path, operator, convertedValues);
	}

}