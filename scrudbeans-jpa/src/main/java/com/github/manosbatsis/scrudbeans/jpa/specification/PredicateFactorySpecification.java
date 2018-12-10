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
package com.github.manosbatsis.scrudbeans.jpa.specification;

import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.specification.PredicateOperator;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;

/**
 * A {@link Specification} implementation that will dynamically resolve and use an appropriate {@link IPredicateFactory} to delegate the creation of a predicate
 * @param <T> the {@link Root} entity model type
 */
@Slf4j
public class PredicateFactorySpecification<T extends Model> implements Specification<T> {

	private final ConversionService conversionService;

	private final ModelInfo modelInfo;

	private final String propertyPath;

	private final PredicateOperator operator;

	private final List<String> propertyValues;

	public PredicateFactorySpecification(
			@NonNull ConversionService conversionService, @NonNull ModelInfo modelInfo, @NonNull String propertyPath, @NonNull PredicateOperator operator, @NonNull List<String> propertyValues) {
		super();
		this.conversionService = conversionService;
		this.modelInfo = modelInfo;
		// remove unnecessary identifier suffix if any
		if (propertyPath.endsWith(".id")) {
			propertyPath = propertyPath.substring(0, propertyPath.length() - 3);
		}
		this.propertyPath = propertyPath;
		if (operator.equals(PredicateOperator.AUTO)) {
			operator = this.getDefaultOperator(propertyValues);
		}
		this.operator = operator;
		this.propertyValues = propertyValues;
	}

	protected PredicateOperator getDefaultOperator(List<String> propertyValues) {
		PredicateOperator op = PredicateOperator.EQUAL;
		if (propertyValues != null) {
			if (propertyValues.size() > 1) {
				op = PredicateOperator.IN;
			}
		}
		return op;
	}

	@Override
	public Predicate toPredicate(
			Root<T> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		Predicate predicate = null;
		IPredicateFactory predicateFactory = null;
		Class fieldType = SpecificationUtils.getMemberType(this.modelInfo.getModelType(), this.propertyPath);
		if (fieldType != null) {
			predicateFactory = SpecificationUtils.getPredicateFactoryForClass(fieldType);
			log.debug("toPredicate, predicateFactory: {}", predicateFactory);
			if (predicateFactory != null) {
				predicate = predicateFactory.buildPredicate(root, builder, this.propertyPath, fieldType, conversionService, this.operator, this.propertyValues);
			}
		}
		if (predicate == null) {
			log.warn("toPredicate, failed constructing predicate for fieldType: {}, predicateFactory: {}", fieldType, predicateFactory);
		}
		return predicate;
	}

}