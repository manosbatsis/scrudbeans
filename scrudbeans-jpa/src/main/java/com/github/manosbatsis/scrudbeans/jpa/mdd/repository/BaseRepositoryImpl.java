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
package com.github.manosbatsis.scrudbeans.jpa.mdd.repository;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.Subgraph;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.repository.ModelRepository;
import com.github.manosbatsis.scrudbeans.common.util.exception.http.BeanValidationException;
import com.github.manosbatsis.scrudbeans.jpa.mdd.util.EntityUtil;
import lombok.NonNull;
import org.hibernate.engine.spi.SessionImplementor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.security.access.method.P;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;


public class BaseRepositoryImpl<T extends PersistableModel<PK>, PK extends Serializable> extends SimpleJpaRepository<T, PK>
		implements ModelRepository<T, PK> {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseRepositoryImpl.class);

	private boolean skipValidation = false;

	private EntityManager entityManager;

	private Class<T> domainClass;

	protected Validator validator;


	/**
	 * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
	 *
	 * @param entityInformation must not be {@literal null}.
	 * @param entityManager must not be {@literal null}.
	 */
	public BaseRepositoryImpl(JpaEntityInformation<T, ?> entityInformation, EntityManager entityManager, Validator validator) {
		super(entityInformation, entityManager);
		LOGGER.debug("new BaseRepositoryImpl, entityInformation: {}, entityManager: {}, validator: {}",
				entityInformation, entityManager, validator);
		Assert.notNull(entityInformation, "BaseRepositoryImpl requires a non-null entityInformation constructor parameter");
		Assert.notNull(entityManager, "BaseRepositoryImpl requires a non-null entityManager constructor parameter");
		this.entityManager = entityManager;
		this.domainClass = entityInformation.getJavaType();
		//Configuration config = ConfigurationFactory.getConfiguration();
		String[] validatorExcludeClasses = {};//TODO config.getStringArray(ConfigurationFactory.VALIDATOR_EXCLUDES_CLASSESS);
		this.skipValidation = true;//Arrays.asList(validatorExcludeClasses).contains(domainClass.getCanonicalName());
		this.validator = validator;
		LOGGER.debug("new BaseRepositoryImpl, domainClass: {}, validator: {}", this.domainClass, this.validator);

	}

    /*
	 * Creates a new {@link SimpleJpaRepository} to manage objects of the given {@link JpaEntityInformation}.
	 *
     * @param domainClass must not be {@literal null}.
     * @param entityManager must not be {@literal null}.

	public BaseRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
		super(domainClass, entityManager);
        //Configuration config = ConfigurationFactory.getConfiguration();
        this.entityManager = entityManager;
		this.domainClass = domainClass;
		//this.validator = validator;
        //tring[] validatorExcludeClasses = config.getStringArray(ConfigurationFactory.VALIDATOR_EXCLUDES_CLASSESS);
        //this.skipValidation = Arrays.asList(validatorExcludeClasses).contains(domainClass.getCanonicalName());
    }
	 */

	/***
	 * {@inheritDoc}
	 */
	@Override
	public Class<T> getDomainClass() {
		return this.domainClass;
	}

	/**
	 * @return the entityManager
	 */
	@Override
	public EntityManager getEntityManager() {
		return this.entityManager;
	}


//	/***
//	 * {@inheritDoc}
//	 */
//	@Override
//	public T merge(T entity) {
//		this.validate(entity);
//		Map<String, MetadatumModel> metadata = noteMetadata(entity);
//		entity = this.getEntityManager().merge(entity);
//		persistNotedMetadata(metadata, entity);
//		return entity;
//	}
//
//	/***
//	 * {@inheritDoc}
//	 */
//	@Override
//	public T persist(T entity) {
//		this.validate(entity);
//		Map<String, MetadatumModel> metadata = noteMetadata(entity);
//		this.getEntityManager().persist(entity);
//		persistNotedMetadata(metadata, entity);
//		return entity;
//	}
//
//	/***
//	 * {@inheritDoc}
//	 */
//	@Override
//	public <S extends T> S save(S entity) {
//		this.validate(entity);
//		Map<String, MetadatumModel> metadata = noteMetadata(entity);
//		entity = super.save(entity);
//		persistNotedMetadata(metadata, entity);
//		return entity;
//	}


	/***
	 * {@inheritDoc}
	 */
	@Override
	public T patch(@P("resource") T delta) {
		LOGGER.debug("patch, delta: {}", delta);
		// load existing
		T persisted = this.getOne(delta.getId());
		LOGGER.debug("patch, delta: {}, persisted: {}", delta, persisted);
		// update it by copying all non-null properties from the given transient instance
		String[] nullPropertyNames = EntityUtil.getNullPropertyNames(delta);
		LOGGER.debug("patch, nullPropertyNames: {}", nullPropertyNames);
		BeanUtils.copyProperties(delta, persisted, nullPropertyNames);
		LOGGER.debug("patch, patched persisted: {}", persisted);
		// validate
		this.validate(persisted);
		// persist changes
		return this.entityManager.merge(persisted);
	}

//	@Override
//	public MetadatumModel addMetadatum(PK subjectId, String predicate, String object) {
//		Map<String, String> metadata = new HashMap<String, String>();
//		metadata.put(predicate, object);
//		List<MetadatumModel> saved = addMetadata(subjectId, metadata);
//		if (!CollectionUtils.isEmpty(metadata)) {
//			return saved.get(0);
//		}
//		else {
//			return null;
//		}
//	}
//
//	@Override
//	public List<MetadatumModel> addMetadata(PK subjectId,
//			Map<String, String> metadata) {
//		ensureMetadataIsSupported();
//		List<MetadatumModel> saved;
//		if (!CollectionUtils.isEmpty(metadata)) {
//			saved = new ArrayList<MetadatumModel>(metadata.size());
//			for (String predicate : metadata.keySet()) {
//				LOGGER.info("addMetadatum subjectId: " + subjectId
//						+ ", predicate: " + predicate);
//				MetadatumModel metadatum = this.findMetadatum(subjectId, predicate);
//				LOGGER.info("addMetadatum metadatum: " + metadatum);
//				if (metadatum == null) {
//					T entity = this.getOne(subjectId);
//					// Class<?> metadatumClass = ((MetadataSubjectModel) entity)
//					// .getMetadataDomainClass();
//					MetadataSubjectModel subject = (MetadataSubjectModel) entity;
//					metadatum = this.buildMetadatum(subject, predicate,
//							metadata.get(predicate));
//					this.getEntityManager().persist(metadatum);
//				}
//				else {
//					// if exists, only update the value
//					metadatum.setObject(metadata.get(predicate));
//					metadatum = this.getEntityManager().merge(metadatum);
//				}
//
//				// subject.addMetadatum(model.buildPredicate(), model.getObject());
//				// this.entityManager.merge(entity);
//				LOGGER.info("addMetadatum saved metadatum: " + metadatum);
//				saved.add(metadatum);
//			}
//		}
//		else {
//			saved = new ArrayList<MetadatumModel>(0);
//		}
//		LOGGER.info("addMetadatum returns: " + saved);
//		return saved;
//	}
//
//	@SuppressWarnings("unchecked")
//	private MetadatumModel buildMetadatum(MetadataSubjectModel subject, String predicate,
//			String object) {
//		Class<?> metadatumClass = subject.getMetadataDomainClass();
//		MetadatumModel metadatum = null;
//		try {
//			metadatum = (MetadatumModel) metadatumClass.getConstructor(
//					this.getDomainClass(), String.class, String.class)
//					.newInstance(subject, predicate, object);
//		}
//		catch (Exception e) {
//			throw new RuntimeException("Failed adding metadatum", e);
//		}
//		return metadatum;
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public void removeMetadatum(PK subjectId, String predicate) {
//		Assert.notNull(subjectId);
//		Assert.notNull(predicate);
//		ensureMetadataIsSupported();
//		T subjectEntity = this.getOne(subjectId);
//		Class<?> metadatumClass = ((MetadataSubjectModel) subjectEntity)
//				.getMetadataDomainClass();
//		// TODO: refactor to criteria
//		MetadatumModel metadatum = findMetadatum(subjectId, predicate,
//				metadatumClass);
//		if (metadatum != null) {
//			this.getEntityManager().remove(metadatum);
//		}
//		// CriteriaBuilder builder = this.entityManager.getCriteriaBuilder();
//		// CriteriaQuery criteria = builder.createQuery(metadatumClass);
//		// Root root = criteria.from(metadatumClass);
//		// criteria.where( builder.equal(root.get("predicate"), predicate));
//
//		// T entity = this.findOne(subjectId);
//		// MetadataSubjectModel subject = (MetadataSubjectModel) entity;
//		// if (subject.getMetadata() != null) {
//		// subject.getMetadata().remove(predicate);
//		// this.merge(entity);
//		// }
//	}
//
//	@Override
//	public MetadatumModel findMetadatum(PK subjectId, String predicate) {
//		T subjectEntity = this.getOne(subjectId);
//		Class<?> metadatumClass = ((MetadataSubjectModel) subjectEntity)
//				.getMetadataDomainClass();
//		return this.findMetadatum(subjectId, predicate, metadatumClass);
//
//	}
//
//	protected MetadatumModel findMetadatum(PK subjectId, String predicate,
//			Class<?> metadatumClass) {
//		List<MetadatumModel> results = this
//				.getEntityManager()
//				.createQuery(
//						"from "
//								+ metadatumClass.getSimpleName()
//								+ " m where m.predicate = ?1 and m.subject.id = ?2")
//				.setParameter(1, predicate).setParameter(2, subjectId)
//				.getResultList();
//		MetadatumModel metadatum = results.isEmpty() ? null : results.get(0);
//		return metadatum;
//	}
//
//	protected void ensureMetadataIsSupported() {
//		if (!MetadataSubjectModel.class.isAssignableFrom(getDomainClass())) {
//			throw new UnsupportedOperationException();
//		}
//	}
//
//	@Override
//	public void refresh(T entity) {
//		this.getEntityManager().refresh(entity);
//	}
//
//
//	/**
//	 * Get the entity's file uploads for this property
//	 * @param subjectId the entity id
//	 * @param propertyName the property holding the upload(s)
//	 * @return the uploads
//	 */
//	public List<UploadedFileModel> getUploadsForProperty(PK subjectId, String propertyName) {
//		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
//
//		CriteriaQuery<BinaryFile> query = cb.createQuery(BinaryFile.class);
//		Root<T> root = query.from(this.domainClass);
//		query.where(cb.equal(root.get("id"), subjectId));
//		Selection<? extends BinaryFile> join = root.join(propertyName, JoinType.INNER);
//		query.select(join);
//		List<BinaryFile> results = this.entityManager.createQuery(query).getResultList();
//		List<UploadedFileModel> casted = new ArrayList<>(results.size());
//		for (BinaryFile f : results) {
//			casted.addAll(results);
//		}
//		return casted;
//	}
//
//	@SuppressWarnings({"rawtypes", "unchecked"})
//	private void persistNotedMetadata(Map<String, MetadatumModel> metadata, T saved) {
//
//		if (LOGGER.isDebugEnabled()) {
//			LOGGER.debug("persistNotedMetadata, noted: " + metadata);
//		}
//		if (!CollectionUtils.isEmpty(metadata)) {
//			MetadataSubjectModel subject = (MetadataSubjectModel) saved;
//			MetadatumModel[] metaArray = metadata.values().toArray(
//					new MetadatumModel[metadata.values().size()]);
//			for (int i = 0; i < metaArray.length; i++) {
//				MetadatumModel metadatum = metaArray[i];
//				subject.addMetadatum(this.addMetadatum(
//						saved.getId(), metadatum.getPredicate(),
//						metadatum.getObject()));
//			}
//		}
//	}
//
//	private Map<String, MetadatumModel> noteMetadata(T resource) {
//		Map<String, MetadatumModel> metadata = null;
//		if (MetadataSubjectModel.class.isAssignableFrom(this.getDomainClass())) {
//			metadata = ((MetadataSubjectModel) resource).getMetadata();
//			((MetadataSubjectModel) resource)
//					.setMetadata(new HashMap<String, MetadatumModel>());
//			if (LOGGER.isDebugEnabled()) {
//				LOGGER.debug("noteMetadata, noted: " + metadata);
//			}
//		}
//		else {
//			if (LOGGER.isDebugEnabled()) {
//				LOGGER.debug("noteMetadata, not a metadata subject");
//			}
//		}
//		return metadata;
//	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public Set<ConstraintViolation<T>> validateConstraints(T resource) {
		LOGGER.debug("validateConstraints, validator: {}, resource: {}", validator, resource);
		Set<ConstraintViolation<T>> constraintViolations = validator.<T>validate(resource);

		return constraintViolations;
	}

	protected void validate(T resource) {
		LOGGER.debug("validate resource: {}", resource);
		resource.preSave();
		if (!this.skipValidation) {
			// un-proxy for validation to work
			resource = (T) entityManager.unwrap(SessionImplementor.class).getPersistenceContext().unproxy(resource);
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

	@Override
	public <RT extends PersistableModel> RT findRelatedEntityByOwnId(@NonNull PK id, @NonNull FieldInfo fieldInfo) {
		if (!fieldInfo.getFieldMappingType().isToOne()) {
			throw new IllegalArgumentException("Field " + fieldInfo.getFieldName() + " is not a relation to a single entity");
		}

		CriteriaBuilder cb = this.entityManager.getCriteriaBuilder();
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

		return (RT) this.entityManager.createQuery(query).getSingleResult();
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

}
