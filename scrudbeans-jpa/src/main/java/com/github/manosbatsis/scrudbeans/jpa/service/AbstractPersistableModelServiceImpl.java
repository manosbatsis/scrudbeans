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
package com.github.manosbatsis.scrudbeans.jpa.service;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolation;

import com.github.manosbatsis.scrudbeans.api.domain.DisableableModel;
import com.github.manosbatsis.scrudbeans.api.domain.MetadatumModel;
import com.github.manosbatsis.scrudbeans.api.domain.SettableIdModel;
import com.github.manosbatsis.scrudbeans.api.domain.UploadedFileModel;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.FilePersistence;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.common.repository.ModelRepository;
import com.github.manosbatsis.scrudbeans.common.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.jpa.specification.SpecificationUtils;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartHttpServletRequest;

//TODO
//import com.github.manosbatsis.scrudbeans.api.domain.UserDetails;
//import com.github.manosbatsis.scrudbeans.api.domain.event.EntityCreatedEvent;
//import com.github.manosbatsis.scrudbeans.api.domain.event.EntityUpdatedEvent;
//import com.github.manosbatsis.scrudbeans.api.domain.users.model.User;

/**
 * SCRUD service handling a specific type of {@link SettableIdModel} using a {@link ModelRepository}
 *
 * @param <T>  Your resource class to manage, usually an entity class
 * @param <PK> Resource id type, usually Long or String
 * @param <R>  The repository class to automatically inject
 */
@Slf4j
public class AbstractPersistableModelServiceImpl<T extends SettableIdModel<PK>, PK extends Serializable, R extends ModelRepository<T, PK>>
		extends AbstractBaseServiceImpl
		implements PersistableModelService<T, PK> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPersistableModelServiceImpl.class);

	protected R repository;

	@SuppressWarnings("SpringJavaAutowiringInspection")
	@Autowired
	public void setRepository(R repository) {
		this.repository = repository;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
	public Class<T> getDomainClass() {
		return this.repository.getDomainClass();
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getIdentifier(Object entity) {
		return this.repository.getEntityManager().getEntityManagerFactory().getPersistenceUnitUtil().getIdentifier(entity);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void initData() {
// TODO
//        User systemUser = (User) this.getPrincipalLocalUser();
//        boolean wasAnonymous = Objects.isNull(systemUser) || StringUtils.isBlank(systemUser.getId());
//
//
//        //? login?
//        if(wasAnonymous){
//
//            // load system user if it exists
//            systemUser = this.userRepository.findByUsername("admin");
//
//            Authentication auth = null;
//
//            // init auth with system user or emulate
//            if(systemUser != null){
//                auth = new UserDetailsAuthenticationToken(UserDetailsImpl.fromUser(systemUser));
//            }
//            else{
//                auth = new AnonymousAuthenticationToken(this.getClass().getName(), this.getClass().getName(),
//                        Arrays.asList(new SimpleGrantedAuthority[]{new SimpleGrantedAuthority(Roles.ROLE_USER), new SimpleGrantedAuthority(Roles.ROLE_ADMIN)}));
//            }
//
//            // login
//            SecurityContextHolder.getContext().setAuthentication(auth);
//        }
//
//        // init data
//        log.debug("initData, type: {}, was anonymous: {} ", this.getDomainClass().getSimpleName(), wasAnonymous);
//        log.debug("initData > initDataOverride with user: {}", systemUser);
//        this.initDataOverride(systemUser);
//
//        // logout?
//        if(wasAnonymous) {
//            SecurityContextHolder.clearContext();
//        }
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public T create(@P("resource") T resource) {
		Assert.notNull(resource, "Resource can't be null");
		resource = repository.save(resource);
		this.postCreate(resource);

		// TODO
		//EntityCreatedEvent<T> event = new EntityCreatedEvent<T>(resource);
		//this.applicationEventPublisher.publishEvent(event);

		return resource;
	}


	@Override
	public void postCreate(T resource) {

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public T update(@P("resource") T resource) {
		Assert.notNull(resource, "Resource can't be null");
		log.debug("update resource: {}", resource);
		resource = repository.save(resource);
		// TODO applicationEventPublisher.publishEvent(new EntityUpdatedEvent<T>(resource));
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public T patch(@P("resource") T resource) {
		log.debug("patch resource: {}", resource);
		resource = repository.patch(resource);
		// TODOv applicationEventPublisher.publishEvent(new EntityUpdatedEvent<T>(resource));
		return resource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(T resource) {
		Assert.notNull(resource, "Resource can't be null");
		// soft delete if disableable
		if (DisableableModel.class.isAssignableFrom(this.getDomainClass())) {
			((DisableableModel) resource).setDisabled(LocalDateTime.now());
			this.patch(resource);
			LOGGER.debug("delete: soft-deleted resource");
		}
		// else delete physically
		else {
			repository.delete(resource);
			LOGGER.debug("delete: deleted resource");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void delete(PK id) {
		Assert.notNull(id, "Resource PK can't be null");
		// soft delete if disableable
		if (DisableableModel.class.isAssignableFrom(this.getDomainClass())) {
			T resource = this.repository.getOne(id);
			((DisableableModel) resource).setDisabled(LocalDateTime.now());
			LOGGER.debug("delete: soft-deleted by ID");
		}
		// else delete physically
		else {
			repository.deleteById(id);
			LOGGER.debug("delete: deleted by id");
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void deleteAll() {
		repository.deleteAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public void deleteAllWithCascade() {
		Iterable<T> list = repository.findAll();
		for (T entity : list) {
			repository.delete(entity);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T findById(PK id) {
		Assert.notNull(id, "Resource PK can't be null");
		return repository.findById(id).orElse(null);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findByIds(Set<PK> ids) {
		Assert.notNull(ids, "Resource ids can't be null");
		return repository.findAllById(ids);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SettableIdModel findRelatedSingle(@NonNull PK id, @NonNull FieldInfo fieldInfo) {
		// throw error if not valid or linkable relationship
		if (!fieldInfo.isLinkableResource() || !fieldInfo.isToOne()) {
			throw new IllegalArgumentException("Related must be linkable and *ToOne");
		}
		return repository.findRelatedEntityByOwnId(id, fieldInfo);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <M extends SettableIdModel<MID>, MID extends Serializable> Page<M> findRelatedPaginated(Class<M> entityType, Specification<M> spec, @NonNull Pageable pageable) {
		ModelRepository<M, MID> repo = (ModelRepository) this.repositoryRegistryService.getRepositoryFor(entityType);

		if (repo == null) {
			throw new IllegalArgumentException("Could not find a repository for model type: " + entityType);
		}

		if (spec != null) {
			return repo.findAll(spec, pageable);
		}
		else {
			return repo.findAll(pageable);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<T> findAll() {
		return repository.findAll();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Page<T> findPaginated(Specification<T> spec, @NonNull Pageable pageable) {
		log.debug("findPaginated, pageable: {}", pageable);

		if (spec != null) {
			return this.repository.findAll(spec, pageable);
		}
		else {
			return this.repository.findAll(pageable);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long count() {
		return repository.count();
	}

	/***
	 * {@inheritDoc}
	 */
	@Override
	public Set<ConstraintViolation<T>> validateConstraints(T resource) {
		return this.repository.validateConstraints(resource);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	public void addMetadatum(PK subjectId, MetadatumModel dto) {
		if (log.isDebugEnabled()) {
			log.debug("addMetadatum subjectId: " + subjectId + ", metadatum: " + dto);
		}
		//TODO
		//this.repository.addMetadatum(subjectId, dto.getPredicate(),dto.getObject());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = false)
	public void addMetadata(PK subjectId, Collection<MetadatumModel> dtos) {
		if (!CollectionUtils.isEmpty(dtos)) {
			for (MetadatumModel dto : dtos) {
				this.addMetadatum(subjectId, dto);
			}
		}
	}

	@Transactional(readOnly = false)
	public void removeMetadatum(PK subjectId, String predicate) {
		if (log.isDebugEnabled()) {
			log.debug("removeMetadatum subjectId: " + subjectId + ", predicate: "
					+ predicate);
		}
		// TODO
		//this.repository.removeMetadatum(subjectId, predicate);
	}


	/**
	 * Get the entity's file uploads for this propert
	 *
	 * @param subjectId    the entity id
	 * @param propertyName the property holding the upload(s)
	 * @return the uploads
	 */
	public List<UploadedFileModel> getUploadsForProperty(PK subjectId, String propertyName) {
		return null;//TODOthis.repository.getUploadsForProperty(subjectId, propertyName);
	}

	@Override
	@Transactional(readOnly = false)
	public T updateFiles(@PathVariable PK id, MultipartHttpServletRequest request, HttpServletResponse response) {
		T entity = this.findById(id);
		log.debug("Entity before uploading files: {}", entity);
		try {
			String basePath = new StringBuffer(this.getDomainClass().getSimpleName())
					.append('/').append(id).append('/').toString();
			String propertyName;
			for (Iterator<String> iterator = request.getFileNames(); iterator.hasNext(); ) {
				// get the property name
				propertyName = iterator.next();

				// verify the property exists
				Field fileField = SpecificationUtils.getField(this.getDomainClass(), propertyName);
				if (fileField == null || !fileField.isAnnotationPresent(FilePersistence.class)) {
					throw new IllegalArgumentException("No FilePersistence annotation found for member: " + propertyName);
				}

				// store the file and update the property URL
				String url = this.filePersistenceService.saveFile(fileField, request.getFile(propertyName), basePath + propertyName);
				BeanUtils.setProperty(entity, propertyName, url);

			}
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to update files", e);
		}
		// return the updated entity
		entity = this.update(entity);

		log.debug("Entity after uploading files: {}", entity);
		return entity;
	}

	/**
	 * Utility method to be called by implementations
	 *
	 * @param id
	 * @param filenames
	 */
	@Override
	@PreAuthorize("hasRole('ROLE_USER')")
	@Transactional(readOnly = false)
	public void deleteFiles(PK id, String... filenames) {
		String basePath = new StringBuffer(this.getDomainClass().getSimpleName())
				.append('/').append(id).append('/').toString();
		List<String> keys = new LinkedList<String>();

		for (String propertyName : filenames) {
			// verify the property exists
			Field fileField = SpecificationUtils.getField(this.getDomainClass(), propertyName);
			if (fileField == null || !fileField.isAnnotationPresent(FilePersistence.class)) {
				throw new IllegalArgumentException("No FilePersistence annotation found for member: " + propertyName);
			}

			// store the file key
			keys.add(basePath + propertyName);
		}

		// delete files
		this.filePersistenceService.deleteFiles(keys.toArray(new String[keys.size()]));
	}

}
