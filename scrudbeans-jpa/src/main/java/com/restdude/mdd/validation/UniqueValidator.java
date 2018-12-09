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
package com.restdude.mdd.validation;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import com.restdude.domain.PersistableModel;
import com.restdude.mdd.util.EntityUtil;
import com.restdude.util.ValidatorUtil;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.proxy.HibernateProxyHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class UniqueValidator implements ConstraintValidator<Unique, PersistableModel> {

	private static final Logger LOGGER = LoggerFactory.getLogger(UniqueValidator.class);


	private EntityManager entityManager;

	@Autowired
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public void initialize(Unique annotation) {
	}

	@Override
	@Transactional(readOnly = true)
	public boolean isValid(PersistableModel value, ConstraintValidatorContext constraintValidatorContext) {
		LOGGER.debug("isValid pathFragment: {}", value);
		boolean valid = true;
		// skip validation if null
		if (value != null) {

			// get the entity class being proxied by <code>pathFragment</code> if any, or the actual <code>pathFragment</code> class otherwise
			Class domainClass = HibernateProxyHelper.getClassWithoutInitializingProxy(value);

			try {
				// get unique field names
				List<String> uniqueFieldNames = ValidatorUtil.getUniqueFieldNames(domainClass);

				// get records matching the unique field values
				List<PersistableModel> resultSet = getViolatingRecords(value, domainClass, uniqueFieldNames);

				// process violating records
				if (!resultSet.isEmpty()) {

					// disable default constraint validation construction
					// as it will point to  the object instead of the property
					constraintValidatorContext.disableDefaultConstraintViolation();

					for (PersistableModel match : resultSet) {
						if (!match.getId().equals(value.getId())) {
							for (String propertyName : uniqueFieldNames) {
								Object newValue = PropertyUtils.getProperty(value, propertyName);
								Object existingValue = PropertyUtils.getProperty(match, propertyName);
								if (newValue != null) {
									// ignore case for strings?
									if (newValue instanceof String && !EntityUtil.isCaseSensitive(domainClass, propertyName)) {
										newValue = ((String) newValue).toLowerCase();
										existingValue = existingValue != null ? ((String) existingValue).toLowerCase() : null;
									}
									// match?
									if (newValue.equals(existingValue)) {
										LOGGER.debug("isValid, adding violation for property name: {}, value: {}", propertyName, newValue);
										valid = false;
										// report violation
										constraintValidatorContext
												.buildConstraintViolationWithTemplate("Unique value not available for property: " + propertyName)
												.addPropertyNode(propertyName).addConstraintViolation();
									}
								}
							}
						}
					}
				}
			}
			catch (Exception e) {
				LOGGER.error("Error while validating constraints", e);
			}
		}

		LOGGER.debug("isValid returns: {}", valid);
		return valid;

	}

	private List<PersistableModel> getViolatingRecords(PersistableModel value, Class domainClass, List<String> uniqueFieldNames) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

		LOGGER.debug("getViolatingRecords, for pathFragment: {}, uniqueFieldNames: {}", value, uniqueFieldNames);
		CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
		CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(domainClass);
		Root<? extends PersistableModel<Serializable>> root = criteriaQuery.from(domainClass);
		List<Predicate> predicates = new ArrayList<Predicate>(uniqueFieldNames.size());
		for (String propertyName : uniqueFieldNames) {
			LOGGER.debug("getViolatingRecords, adding predicate for field: {}", propertyName);
			Object propertyValue = PropertyUtils.getProperty(value, propertyName);
			Predicate predicate = criteriaBuilder.equal(root.get(propertyName), propertyValue);
			predicates.add(predicate);
		}

		criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
		TypedQuery<PersistableModel> typedQuery = this.entityManager.createQuery(criteriaQuery);

		// tell JPA not to flush just vbecause we want to check existing records
		typedQuery.setFlushMode(FlushModeType.COMMIT);
		return typedQuery.getResultList();
	}

}