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

import com.github.manosbatsis.scrudbeans.api.specification.PredicateOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.convert.ConversionService;

public class StringPredicateFactory extends AbstractPredicateFactory<String> {

	private static final Logger LOGGER = LoggerFactory.getLogger(StringPredicateFactory.class);

	private static final String WILDCARD = "*";

	private static final String WILDCARD_JPA = "%";

	public StringPredicateFactory() {
	}

	@Override
	public Class<?> getValueType() {
		return String.class;
	}

	public List<Object> convertValues(List<String> propertyValues, ConversionService conversionService) {
		List<Object> converted = null;
		if (propertyValues != null) {
			converted = new ArrayList<>(propertyValues);
		}
		return converted;
	}

	@Override
	protected <AV extends Serializable> Predicate buildPredicate(Root<?> root, CriteriaBuilder cb, Path path, PredicateOperator operator, List<AV> propertyValues) {
		Predicate predicate = null;


		String argument = (String) propertyValues.get(0);
		switch (operator) {
			case NOT_EQUAL: {
				if (argument == null) {
					predicate = path.isNotNull();
				}
				else {
					if (argument.startsWith(WILDCARD)) {
						argument = WILDCARD_JPA + argument.substring(1);
					}
					if (argument.endsWith(WILDCARD)) {
						argument = argument.substring(0, argument.length() - 1) + WILDCARD_JPA;
					}
					if (argument.startsWith(WILDCARD_JPA) || argument.endsWith(WILDCARD_JPA)) {
						predicate = cb.notLike(path, argument);
					}
					else {
						predicate = cb.notEqual(path, argument);
					}
				}
				break;
			}
			case EQUAL: {
				if (argument == null) {
					predicate = path.isNull();
				}
				else {
					if (argument.startsWith(WILDCARD)) {
						argument = WILDCARD_JPA + argument.substring(1);
					}
					if (argument.endsWith(WILDCARD)) {
						argument = argument.substring(0, argument.length() - 1) + WILDCARD_JPA;
					}
					if (argument.startsWith(WILDCARD_JPA) || argument.endsWith(WILDCARD_JPA)) {
						predicate = cb.like(path, argument);
					}
					else {
						predicate = cb.equal(path, argument);
					}
				}
				break;
			}
			case GREATER_THAN: {
				predicate = cb.greaterThan(path, argument);
				break;
			}
			case GREATER_THAN_OR_EQUAL: {
				predicate = cb.greaterThanOrEqualTo(path, argument);
				break;
			}
			case LESS_THAN: {
				predicate = cb.lessThan(path, argument);
				break;
			}
			case LESS_THAN_OR_EQUAL: {
				predicate = cb.lessThanOrEqualTo(path, argument);
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
		}


		return predicate;
	}

}