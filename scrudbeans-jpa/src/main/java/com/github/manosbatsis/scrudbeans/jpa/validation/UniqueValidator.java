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
package com.github.manosbatsis.scrudbeans.jpa.validation;

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

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.jpa.util.EntityUtil;
import com.github.manosbatsis.scrudbeans.jpa.util.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.proxy.HibernateProxyHelper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provides meaningful constraint validation messages
 *
 * @see Unique
 */
@Slf4j
public class UniqueValidator implements ConstraintValidator<Unique, Persistable> {

    private EntityManager entityManager;

    @Autowired
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    public void initialize(Unique annotation) {
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isValid(Persistable value, ConstraintValidatorContext constraintValidatorContext) {
        log.debug("isValid pathFragment: {}", value);
        boolean valid = true;
        // skip validation if null
        if (value != null) {

            // get the entity class being proxied by <code>pathFragment</code> if any, or the actual <code>pathFragment</code> class otherwise
            Class domainClass = HibernateProxyHelper.getClassWithoutInitializingProxy(value);

            try {
                // get unique field names
                List<String> uniqueFieldNames = ValidatorUtil.getUniqueFieldNames(domainClass);
                log.debug("isValid uniqueFieldNames: {}", uniqueFieldNames);
                // get records matching the unique field values
                List<Persistable> resultSet = getViolatingRecords(value, domainClass, uniqueFieldNames);

                log.debug("isValid violating records: {}", resultSet.size());
                // process violating records
                if (!resultSet.isEmpty()) {

                    // disable default constraint validation construction
                    // as it will point to the object instead of the property
                    constraintValidatorContext.disableDefaultConstraintViolation();

                    for (Persistable match : resultSet) {

                        // If value is new or otherwise different than the violating record
                        if (value.isNew() || !value.getScrudBeanId().equals(match.getScrudBeanId())) {
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
										log.debug("isValid, adding violation for property name: {}, value: {}", propertyName, newValue);
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
            } catch (Exception e) {
                log.error("Error while validating constraints", e);
            }
        }

        log.debug("isValid returns: {}", valid);
        return valid;

    }

    private List<Persistable> getViolatingRecords(Persistable value, Class domainClass, List<String> uniqueFieldNames) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {

        log.debug("getViolatingRecords, for pathFragment: {}, uniqueFieldNames: {}", value, uniqueFieldNames);
        CriteriaBuilder criteriaBuilder = this.entityManager.getCriteriaBuilder();
        CriteriaQuery criteriaQuery = criteriaBuilder.createQuery(domainClass);
        Root<? extends Persistable<Serializable>> root = criteriaQuery.from(domainClass);
        List<Predicate> predicates = new ArrayList<Predicate>(uniqueFieldNames.size());
        for (String propertyName : uniqueFieldNames) {
            log.debug("getViolatingRecords, adding predicate for field: {}", propertyName);
            Object propertyValue = PropertyUtils.getProperty(value, propertyName);
            Predicate predicate = criteriaBuilder.equal(root.get(propertyName), propertyValue);
            predicates.add(predicate);
        }

        criteriaQuery.where(predicates.toArray(new Predicate[predicates.size()]));
        TypedQuery<Persistable> typedQuery = this.entityManager.createQuery(criteriaQuery);

        // tell JPA not to flush just vbecause we want to check existing records
        typedQuery.setFlushMode(FlushModeType.COMMIT);
        return typedQuery.getResultList();
	}

}
