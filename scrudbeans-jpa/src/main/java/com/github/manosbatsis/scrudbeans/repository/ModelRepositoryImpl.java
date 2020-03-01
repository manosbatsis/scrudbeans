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
package com.github.manosbatsis.scrudbeans.repository;

import com.github.manosbatsis.kotlin.utils.api.Dto;
import com.github.manosbatsis.scrudbeans.api.domain.DisableableModel;
import com.github.manosbatsis.scrudbeans.api.domain.KPersistable;
import com.github.manosbatsis.scrudbeans.api.exception.BeanValidationException;
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry;
import com.github.manosbatsis.scrudbeans.util.EntityUtil;
import lombok.NonNull;
import org.apache.commons.beanutils.PropertyUtils;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.lang.Nullable;
import org.springframework.security.access.method.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.persistence.*;
import javax.persistence.criteria.*;
import javax.persistence.metamodel.SingularAttribute;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

import static org.springframework.data.jpa.repository.query.QueryUtils.*;

public class ModelRepositoryImpl<T, PK extends Serializable>
		extends SimpleJpaRepository<T, PK>
		implements ModelRepository<T, PK> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelRepositoryImpl.class);

	protected static <ST, SPK> JpaEntityInformation<ST, SPK> buildEntityInformation(Class<ST> domainClass, EntityManager em){
		if (KPersistable.class.isAssignableFrom(domainClass)){
			return new KPersistableModelEntityInformation(domainClass, em.getMetamodel());
		}
		else  {
			return (JpaEntityInformation<ST, SPK>) JpaEntityInformationSupport.getEntityInformation(domainClass, em);
		}
	}

	private boolean skipValidation = false;

	private EntityManager em;

	private JpaEntityInformation<T, PK> entityInformation;

	private Class<T> domainClass;

	protected Validator validator;

	protected final boolean disableableDomainClass;

	/**
	 * Creates a new {@link ModelRepositoryImpl} to manage objects of the given domain type.
	 *
     * @param domainClass must not be {@literal null}.
     * @param em          must not be {@literal null}.
	 */
    public ModelRepositoryImpl(@NonNull Class<T> domainClass, @NonNull EntityManager em) {
		this(buildEntityInformation(domainClass, em), em);
    }

	/**
	 * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
	 *
	 * @param entityInformation must not be {@literal null}.
	 * @param entityManager     must not be {@literal null}.
	 */
	public ModelRepositoryImpl(@NonNull JpaEntityInformation<T, PK> entityInformation,
							   @NonNull EntityManager entityManager) {
		super(entityInformation, entityManager);
		LOGGER.debug("new ModelRepositoryImpl, entityInformation: {}, entityManager: {}, validator: {}",
				entityInformation, entityManager, validator);
		this.em = entityManager;
		this.entityInformation = entityInformation;
		this.domainClass = entityInformation.getJavaType();
		this.disableableDomainClass = DisableableModel.class.isAssignableFrom(this.domainClass);
		selfValidate();
		// TODO  Configuration config = ConfigurationFactory.getConfiguration();
		//String[] validatorExcludeClasses = config.getStringArray(ConfigurationFactory.VALIDATOR_EXCLUDES_CLASSESS);
		//this.skipValidation = Arrays.asList(validatorExcludeClasses).contains(domainClass.getCanonicalName());
	}

	private void selfValidate() {
		Assert.isTrue(!KPersistable.class.isAssignableFrom(domainClass) || KPersistableModelEntityInformation.class.isAssignableFrom(entityInformation.getClass()),
				"ModelRepositoryImpl requires a KPersistableModelEntityInformation for KPersistableModel implementation entiry " + domainClass.getCanonicalName() + ", but provided instance type was " + entityInformation.getClass().getCanonicalName());

	}


	/***
	 * {@inheritDoc}
	 */
	@Override
	public String getIdAttributeName() {
		return this.entityInformation.getIdAttribute().getName();
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public Class<T> getDomainClass() {
		return this.domainClass;
	}

	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.em = entityManager;
	}

	public void setValidator(Validator validator) {
		this.validator = validator;
	}

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

	/***
	 * {@inheritDoc}
	 * @deprecated use {@link #create(T)}
	 */
	@Override
	public <S extends T> S save(@NonNull S entity) {
		this.validate(entity);
		boolean isNew = this.entityInformation.isNew(entity);
		LOGGER.debug("ModelRepositoryImpl.save: entity {} as {}, is new: {}", entity, getDomainClass().getSimpleName(), isNew);
		return super.save(entity);
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public T create(@NonNull T entity) {
		return this.save(entity);
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public T create(@NonNull Dto<T> dto) {
		return this.save(dto.toTargetType());
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public T update(@NonNull T resource) {
		String[] ignored = {this.entityInformation.getIdAttribute().getName()};
		return patch(resource, ignored);
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public T update(@NonNull Dto<T> dto) {
		return this.patch(dto);
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public T patch(@NonNull @P("resource") T delta) {
		// update it by copying all non-null properties from the given transient instance
		List<String> ignoredList = new LinkedList<>();
		ignoredList.addAll(Arrays.asList(EntityUtil.getNullPropertyNames(delta)));
		LOGGER.debug("patch, ignored list: {}", ignoredList);
		ignoredList.add("scrudBeanId");
		Iterable<String> idAttributeNames = this.entityInformation.getIdAttributeNames();
		LOGGER.debug("patch, id attributes: {}", idAttributeNames);
		if (idAttributeNames != null) {
			for (String name : idAttributeNames) {
				ignoredList.add(name);
			}
		}
		return patch(delta, ignoredList.toArray(new String[ignoredList.size()]));
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public T patch(@NonNull @P("resource") Dto<T> delta) {

		// load existing
		T entity = this.getOne(getIdAttribute(delta));
		entity = delta.toPatched(entity);
		// validate
		this.validate(entity);
		// persist changes
		return this.em.merge(entity);
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public PK getIdAttribute(Object o) {
		PK id = null;
		try {
			SingularAttribute attr = entityInformation.getIdAttribute();
			LOGGER.info("getIdAttribute, o: {}, attr: {}", o, attr);
			LOGGER.info("getIdAttribute, name: {}", attr.getName());
			id = (PK) PropertyUtils.getProperty(o, attr.getName());
		} catch (Throwable e) {
			throw new RuntimeException("Error retrieving persisted patch target");
		}
		return id;
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public void setIdAttribute(Object o, PK value) {
		try {
			PropertyUtils.setProperty(o, entityInformation.getIdAttribute().getName(), value);
		} catch (Exception e) {
			throw new RuntimeException("Error retrieving persisted patch target");
		}
	}

	/***
	 * {@inheritDoc}
	 */
	private T patch(@NonNull @P("resource") T delta, @NonNull String[] ignoredPropertyNames) {
		// load existing
		T persisted = this.getOne(entityInformation.getId(delta));
		BeanUtils.copyProperties(delta, persisted, ignoredPropertyNames);
		// validate
		this.validate(persisted);
		// persist changes
		return this.em.merge(persisted);
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public <S extends T> S saveAndFlush(S entity) {
		this.validate(entity);
		entity = super.saveAndFlush(entity);
		return entity;
	}


	/***
	 * {@inheritDoc}
	 */
	@Override
	public Set<ConstraintViolation<T>> validateConstraints(T resource) {
        LOGGER.debug("validateConstraints, validator: {}, resource: {}", validator, resource);
        Set<ConstraintViolation<T>> constraintViolations = validator.validate(resource);

        return constraintViolations;
    }

	/**
	 * Validate the resource
	 * @param resource
	 */
	protected void validate(T resource) {
		LOGGER.debug("validate resource: {}", resource);
        if (!this.skipValidation) {
            // un-proxy for validation to work
            resource = (T) em.unwrap(SessionImplementor.class).getPersistenceContext().unproxy(resource);
            LOGGER.debug("validate resource after preSave: {}", resource);
            Set<ConstraintViolation<T>> violations = this.validateConstraints(resource);
            LOGGER.debug("validate violations: {}", violations);
            if (!CollectionUtils.isEmpty(violations)) {
                Set<ConstraintViolation> errors = new HashSet<ConstraintViolation>();
                errors.addAll(violations);
                BeanValidationException ex = new BeanValidationException("Validation failed", errors);
                LOGGER.warn("validate, errors: {}", errors);
                ex.setModelType(this.getDomainClass().getCanonicalName());
				throw ex;
			}
		}

	}

	/**
	 * Used to validate relations
	 *
	 * @param id        the id of the root model
	 * @param fieldInfo the attribute name of the relationship
	 * @param <RT>
	 * @return
	 */
	@Override
	public <RT> RT findRelatedEntityByOwnId(@NonNull PK id, @NonNull FieldInfo fieldInfo) {
		if (!fieldInfo.getFieldMappingType().isToOne()) {
			throw new IllegalArgumentException("Field " + fieldInfo.getFieldName() + " is not a relation to a single entity");
		}

		CriteriaBuilder cb = this.em.getCriteriaBuilder();
		CriteriaQuery query = cb.createQuery(fieldInfo.getFieldType());

		// if we can match by reverse
		Optional<String> reverseName = fieldInfo.getReverseFieldName();
		if (fieldInfo.isOneToOne() && reverseName.isPresent()) {

			Root root = query.from(fieldInfo.getFieldModelType());
			query.where(cb.equal(root.<T>get(reverseName.get()).get("id"), id));
        }
        // else match by join
        else {

            Root<T> root = query.from(this.domainClass);
            query.where(cb.equal(root.get("id"), id));
            // or maybe:
            //CompoundSelection<Integer> selection = cb.construct(fieldInfo.getFieldType(), fieldInfo.getFieldName());
            Selection selection = root.join(fieldInfo.getFieldName(), JoinType.INNER);
            query.select(selection);
        }

        return (RT) this.em.createQuery(query).getSingleResult();
    }


    private void buildEntityGraph(EntityGraph<T> entityGraph, String[] attributeGraph) {
        List<String> attributePaths = Arrays.asList(attributeGraph);

        // Sort to ensure that the intermediate entity subgraphs are created accordingly.
        Collections.sort(attributePaths);
		Collections.reverse(attributePaths);

		// We build the entity graph based on the paths with highest depth first
		for (String path : attributePaths) {

			// Fast value - just single attribute
			if (!path.contains(".")) {
				entityGraph.addAttributeNodes(path);
				continue;
			}

			// We need to build nested sub fetch graphs
			String[] pathComponents = StringUtils.delimitedListToStringArray(path, ".");
			Subgraph<?> parent = null;

			for (int c = 0; c < pathComponents.length - 1; c++) {
				parent = c == 0 ? entityGraph.addSubgraph(pathComponents[c]) : parent.addSubgraph(pathComponents[c]);
			}

			parent.addAttributeNodes(pathComponents[pathComponents.length - 1]);
		}
	}

	/**
	 * Creates a {@link TypedQuery} for the given {@link Specification} and {@link Sort}.
	 *
	 * @param spec can be {@literal null}.
	 * @param domainClass must not be {@literal null}.
	 * @param sort must not be {@literal null}.
	 * @return
	 */
	@Override
	protected <S extends T> TypedQuery<S> getQuery(@Nullable Specification<S> spec, Class<S> domainClass, Sort sort) {
		return super.getQuery(applyDisabledFilter(spec), domainClass, sort);
	}


	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.io.Serializable)
	 */
	@Override
	@Transactional
	public void deleteById(PK id) {
		if (this.disableableDomainClass) {
			this.softDelete(id);
		}
		else super.deleteById(id);
    }

    /**
     * (non-Javadoc)
     * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Object)
     * @deprecated use #delete(java.io.Serializable, com.github.manosbatsis.scrudbeans.api.domain.Persistable)
	 */
	@Deprecated
	@Override
	public void delete(T entity) {
		super.delete(entity);
		throw new UnsupportedOperationException("Signature without explicit ID is not supported");
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.CrudRepository#delete(java.lang.Iterable)
	 */
	@Override
	@Transactional
	public void deleteAll(@NotNull Iterable<? extends T> entities) {
		if (this.disableableDomainClass) {
			IdentifierAdapter<T, PK> idAdapter =
					(IdentifierAdapter<T, PK>) IdentifierAdaptersRegistry.getAdapterForClass(getDomainClass());
			for (T entity : entities) {
				this.softDelete(idAdapter.readId(entity));
			}
		}
		else super.deleteAll(entities);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#deleteInBatch(java.lang.Iterable)
	 */
	@Override
	@Transactional
	public void deleteInBatch(Iterable<T> entities) {
		if (this.disableableDomainClass) {
            if (!entities.iterator().hasNext()) {
                return;
            }
            applyAndBind(
                    getQueryString(SOFT_DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()),
					entities, em
			).executeUpdate();
		}
		else super.deleteInBatch(entities);
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.repository.Repository#deleteAll()
	 */
	@Override
	@Transactional
	public void deleteAll() {
		for (T element : findAll()) {
			delete(element);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.jpa.repository.JpaRepository#deleteAllInBatch()
     */
    @Override
    @Transactional
    public void deleteAllInBatch() {
        em.createQuery(
                getQueryString(DELETE_ALL_QUERY_STRING, entityInformation.getEntityName()))
				.executeUpdate();
	}

	/**
	 * Creates a new count query for the given {@link Specification}.
	 *
	 * @param spec can be {@literal null}.
	 * @param domainClass must not be {@literal null}.
	 * @return
	 */
	@Override
	protected <S extends T> TypedQuery<Long> getCountQuery(@Nullable Specification<S> spec, Class<S> domainClass) {
		return super.getCountQuery(applyDisabledFilter(spec), domainClass);
	}


	/** Applies a soft-delete filter */
	private <S extends T> Specification<S> applyDisabledFilter(@Nullable Specification<S> spec) {
		if (this.disableableDomainClass) {
			if (spec == null) spec = notDisabled();
			else spec = spec.and(notDisabled());
		}
		return spec;
    }

    /**
     * Performs a soft-delete
     */
    protected void softDelete(PK id) {
        CriteriaBuilder cb = this.em.getCriteriaBuilder();
        // create update
        CriteriaUpdate<T> update = cb.createCriteriaUpdate(this.domainClass);
        // set the root class
        Root e = update.from(this.domainClass);
        // set update and where clause
        update.set("disabled", LocalDateTime.now());
        update.where(cb.equal(e.get("id"), id));
		// perform update
		this.em.createQuery(update).executeUpdate();
	}

	private static final String FIELD_DISABLED = "disabled";

	public static final String SOFT_DELETE_ALL_QUERY_STRING = "update %s x set disabled = NOW()";

	private static final class DisabledIsNull<T> implements Specification<T> {
		@Override
		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			return cb.isNull(root.<LocalDateTime>get(FIELD_DISABLED));
		}
	}

	private static final class ScheduledToBeDisabled<T> implements Specification<T> {
		@Override
		public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
			return cb.greaterThan(root.get(FIELD_DISABLED), LocalDateTime.now());
		}
	}

	private static final <T> Specification<T> notDisabled() {
		return Specification.where(new DisabledIsNull<T>()).or(new ScheduledToBeDisabled<T>());
	}


}
