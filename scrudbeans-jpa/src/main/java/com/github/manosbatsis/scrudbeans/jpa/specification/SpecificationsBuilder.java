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
package com.github.manosbatsis.scrudbeans.jpa.specification;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.specification.PredicateOperator;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.convert.ConversionService;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

public class SpecificationsBuilder<T extends Persistable<PK>, PK extends Serializable> {

    public static final String PARAM_RELATION_NAME = "relationName";

    public static final String PARAM_FILTER = "filter";

    public static final String PARAM_JSONAPI_PAGE_NUMBER = "page[number]";

    public static final String PARAM_JSONAPI_PAGE_SIZE = "page[size]";

    public static final String PARAM_SORT = "sort";

	public static final String PARAM_PK = "id";

	public static final String PARAM_PAGE_NUMBER = "_pn";

	public static final String PARAM_PAGE_SIZE = "_ps";

	public static final String[] PARAMS_IGNORE_FOR_CRITERIA = {PARAM_RELATION_NAME, PARAM_FILTER, PARAM_JSONAPI_PAGE_NUMBER, PARAM_JSONAPI_PAGE_SIZE, PARAM_SORT, PARAM_PK, PARAM_PAGE_NUMBER, PARAM_PAGE_SIZE};

	private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationsBuilder.class);

	private final Class<T> domainClass;

	private final ConversionService conversionService;

	public SpecificationsBuilder(Class<T> domainClass, ConversionService conversionService) {
		this.domainClass = domainClass;
		this.conversionService = conversionService;
	}

	/**
	 * Dynamically create specification for the given class and search
	 * parameters. This is the entry point for query specifications construction
	 * by repositories.
	 *
	 * @param searchTerms the search terms to match
	 * @return the result specification
	 */
	public Specification<T> build(final Map<String, String[]> searchTerms) {
		return new Specification<T>() {

			/**
			 * Cache for {@link SimpleJpaRepository#getCountQuery(Specification)}
			 */
			private Predicate rootPredicate = null;


			@Override
			public Predicate toPredicate(@SuppressWarnings("rawtypes") Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
				// Cache for {@link SimpleJpaRepository#getCountQuery(org.springframework.data.jpa.domain.Specification)}
				if (this.rootPredicate == null) {
					LOGGER.debug("toPredicate, adding to cache, root: {}", root.getModel().getName());
					this.rootPredicate = buildRootPredicate(searchTerms, root, cb);
				}
				else {
					LOGGER.debug("toPredicate, getting from cache, root: {}", root);
				}
				return this.rootPredicate;
			}
		};
	}

	/**
	 * Get the root predicate, either a conjunction or disjunction
	 * @param searchTerms the search terms to match
	 * @param root the criteria root
	 * @param cb the criteria builder
	 * @return the resulting predicate
	 */
	private Predicate buildRootPredicate(final Map<String, String[]> searchTerms,
			Root<T> root, CriteriaBuilder cb) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("buildRootPredicate0, clazz: {}, searchTerms: {}", domainClass, Arrays.toString(searchTerms.entrySet().toArray()));
		}
		Map<String, String[]> normalizedSearchTerms = new HashMap<>();
		Iterator<String> keyIterator = searchTerms.keySet().iterator();

		String propertyName;
		String newPropertyName;
		while (keyIterator.hasNext()) {
			propertyName = keyIterator.next();
			newPropertyName = propertyName;
			if (propertyName.endsWith(".id")) {
				newPropertyName = propertyName.substring(0, propertyName.length() - 3);
			}
			normalizedSearchTerms.put(newPropertyName, searchTerms.get(propertyName));
		}
		// build a list of criteria/predicates
		LinkedList<Predicate> predicates = buildSearchPredicates(normalizedSearchTerms, root, cb);
		// wrap list in AND/OR junction
		Predicate predicate;
		if (searchTerms.containsKey(SpecificationUtils.SEARCH_MODE) && searchTerms.get(SpecificationUtils.SEARCH_MODE)[0].equalsIgnoreCase(SpecificationUtils.OR)
				// A disjunction of zero predicates is false so...
				&& predicates.size() > 0) {
			predicate = cb.or(predicates.toArray(new Predicate[predicates.size()]));
		}
		else {
			predicate = cb.and(predicates.toArray(new Predicate[predicates.size()]));
		}

		// return the resulting junction
		return predicate;
	}

	private void printMap(Map<String, String[]> searchTerms) {
		if (MapUtils.isNotEmpty(searchTerms)) {
			for (String key : searchTerms.keySet()) {
				String[] values = searchTerms.get(key);
				LOGGER.debug("{}: {}", key, Arrays.toString(values));
			}
		}
	}

	/**
	 * Build the list of predicates corresponding to the given search terms
	 * @param searchTerms the search terms to match
	 * @param root the criteria root
	 * @param cb the criteria builder
	 * @return the list of predicates corresponding to the search terms
	 */
	private LinkedList<Predicate> buildSearchPredicates(final Map<String, String[]> searchTerms, Root<T> root, CriteriaBuilder cb) {

		LOGGER.debug("buildSearchPredicates, domainClass: {}, searchTerms: {}", domainClass, searchTerms);
		LinkedList<Predicate> predicates = new LinkedList<Predicate>();
		List<String> ignoredTerms = Arrays.asList(PARAMS_IGNORE_FOR_CRITERIA);
		if (!MapUtils.isEmpty(searchTerms)) {
			Set<String> propertyNames = searchTerms.keySet();
			for (String propertyName : propertyNames) {
				if (!ignoredTerms.contains(propertyName)) {
					String[] values = searchTerms.get(propertyName);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("buildSearchPredicates, propertyName: {}, values: {}", propertyName, Arrays.toString(values));
					}
					addPredicate(root, cb, predicates, values, propertyName);
				}
			}
		}
		// return the list of predicates
		return predicates;
	}


	/**
	 * Add a predicate to the given list if valid
	 * @param root the criteria root
	 * @param cb the criteria builder
	 * @param predicates the list to add the predicate into
	 * @param propertyValues the predicate values
	 * @param propertyName the predicate name
	 */
	private void addPredicate(Root<T> root, CriteriaBuilder cb,
			LinkedList<Predicate> predicates, String[] propertyValues, String propertyName) {

		LOGGER.debug("addPredicate1, domainClass: {}, propertyName: {}", domainClass, propertyName);
		Class fieldType = SpecificationUtils.getMemberType(domainClass, propertyName);
		IPredicateFactory predicateFactory = null;
		if (fieldType != null) {

			LOGGER.debug("addPredicate2, found field type for domainClass: {}, propertyName: {}, fieldType: {}", domainClass, propertyName, fieldType);
			predicateFactory = SpecificationUtils.getPredicateFactoryForClass(fieldType);
			if (predicateFactory != null) {
				LOGGER.debug("addPredicate3, found predicate factory: {}", predicateFactory);
				PredicateOperator operator = getPredicateOperator(propertyValues);

				predicates.add(predicateFactory.buildPredicate(root, cb, propertyName, fieldType, conversionService, operator, Arrays.asList(propertyValues)));
			}
			else {
				LOGGER.debug("addPredicate3, could not find predicate factory for field type: {}", fieldType);
			}

		}
		else {
			LOGGER.debug("addPredicate1, field type not found for domainClass: {}, propertyName: {}, fieldType: {}", domainClass, propertyName, fieldType);
		}

	}

	private PredicateOperator getPredicateOperator(String[] propertyValues) {
		PredicateOperator operator = null;
		// special cases
		if (propertyValues != null) {
			if ("IS_NULL".equals(propertyValues[0]) || "null".equals(propertyValues[0])) {
				operator = PredicateOperator.IS_NULL;
			}
			else if ("IS_NOT_NULL".equals(propertyValues[0])) {
				operator = PredicateOperator.IS_NOT_NULL;
			}
		}
		// regular cases
		if (operator == null) {
			operator = ArrayUtils.isNotEmpty(propertyValues) && propertyValues.length > 1
					? PredicateOperator.IN
					: PredicateOperator.EQUAL;
		}
		LOGGER.debug("getPredicateOperator: {}, values: {}", operator, propertyValues);
		return operator;
	}

}
