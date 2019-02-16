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
package com.github.manosbatsis.scrudbeans.jpa.specification.factory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.specification.PredicateOperator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;

@Slf4j
public abstract class AbstractPredicateFactory<T extends Serializable> implements IPredicateFactory<T> {


	public AbstractPredicateFactory() {
	}

	@Override
	public Predicate buildPredicate(Root<?> root, CriteriaBuilder cb, String propertyName, Class<T> fieldType, ConversionService conversionService, PredicateOperator operator, List<String> propertyValues) {
		List<T> converted = this.convertValues(propertyValues, conversionService, fieldType);
		Path<T> path = this.getPath(root, propertyName, fieldType);
		return this.buildPredicate(root, cb, path, operator, converted);
	}

	protected <AV extends Serializable> Predicate buildPredicate(Root<?> root, CriteriaBuilder cb, Path path, PredicateOperator operator, List<AV> propertyValues) {
		Predicate predicate = null;


		AV argument = propertyValues.get(0);
		switch (operator) {
			case NOT_EQUAL: {
				if (argument == null) {
					predicate = path.isNotNull();
				}
				else {
					predicate = cb.notEqual(path, argument);
				}
				break;
			}
			case EQUAL: {
				if (argument == null) {
					predicate = cb.isNull(path);
				}
				else {
					predicate = cb.equal(path, argument);
				}
				break;
			}
			case GREATER_THAN: {
				predicate = cb.greaterThan(path, (Comparable) argument);
				break;
			}
			case GREATER_THAN_OR_EQUAL: {
				predicate = cb.greaterThanOrEqualTo(path, (Comparable) argument);
				break;
			}
			case LESS_THAN: {
				predicate = cb.lessThan(path, (Comparable) argument);
				break;
			}
			case LESS_THAN_OR_EQUAL: {
				predicate = cb.lessThanOrEqualTo(path, (Comparable) argument);
				break;
			}
			case IN: {
				predicate = path.in(propertyValues);
				break;
			}
			case NOT_IN: {
				predicate = cb.not(path.in(propertyValues));
				break;
			}
			default: {
				throw new IllegalArgumentException("Unknown predicate operator: " + operator);
			}
		}


		return predicate;
	}

	public <AV extends Serializable> List<AV> convertValues(List<String> propertyValues, ConversionService conversionService, Class<AV> valueType) {
		List<AV> converted = null;
		if (propertyValues != null) {
			converted = new ArrayList<>(propertyValues.size());
			for (String value : propertyValues) {
				converted.add(value != null ? conversionService.convert(value, valueType) : null);
			}
		}
		return converted;
	}

	public <AV extends Serializable> Path<AV> getPath(Root<?> root, String propertyName, Class<AV> fieldType) {
		Path<AV> path = null;
		if (propertyName.contains(".")) {
			String[] pathSteps = propertyName.split("\\.");

			String step = pathSteps[0];
			path = pathSteps.length == 1
					? root.<AV>get(step)
					: root.get(step);

			for (int i = 1; i < pathSteps.length - 1; i++) {
				step = pathSteps[i];
				path = path.get(step);
			}

			step = pathSteps[pathSteps.length - 1];
			path = path.<AV>get(step);

		}
		else {
			path = root.<AV>get(propertyName);
		}
		return path;
	}
}