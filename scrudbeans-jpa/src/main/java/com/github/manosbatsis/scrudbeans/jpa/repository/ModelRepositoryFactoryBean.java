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
package com.github.manosbatsis.scrudbeans.jpa.repository;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.data.repository.core.support.TransactionalRepositoryFactoryBeanSupport;

//@Component
public class ModelRepositoryFactoryBean<R extends JpaRepository<T, PK>, T extends PersistableModel<PK>, PK extends Serializable>
		extends TransactionalRepositoryFactoryBeanSupport<R, T, PK> {

	private static final Logger LOGGER = LoggerFactory.getLogger(ModelRepositoryFactoryBean.class);

	private Validator validator;

	private EntityManager entityManager;

	public ModelRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
		super(repositoryInterface);
	}

	/**
	 * The {@link EntityManager} to be used.
	 *
	 * @param entityManager the entityManager to set
	 */
	@PersistenceContext
	public void setEntityManager(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.springframework.data.repository.support.
	 * TransactionalRepositoryFactoryBeanSupport#doCreateRepositoryFactory()
	 */
	@Override
	protected RepositoryFactorySupport doCreateRepositoryFactory() {
		LOGGER.debug("doCreateRepositoryFactory, entityManager: {}", entityManager);
		RepositoryFactorySupport repositoryFactorySupport = createRepositoryFactory(entityManager);
		LOGGER.debug("doCreateRepositoryFactory, repositoryFactorySupport: {}", repositoryFactorySupport);
		return repositoryFactorySupport;
	}

	@Autowired
	public void setValidator(Validator validator) {
		this.validator = validator;

	}

	/**
	 * Returns a {@link RepositoryFactorySupport}.
	 *
	 * @param entityManager
	 * @return
	 */
	protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
		LOGGER.debug("getModelRepository, validator: {}", validator);
		RepositoryFactorySupport repositoryFactorySupport = new RepositoryFactory(entityManager, this.validator);
		LOGGER.debug("getModelRepository, repositoryFactorySupport: {}, entityManager: {}", repositoryFactorySupport, entityManager);
		return repositoryFactorySupport;
	}


	private static class RepositoryFactory<T extends PersistableModel<PK>, PK extends Serializable> extends JpaRepositoryFactory {

		private EntityManager entityManager;

		private Validator validator;

		public RepositoryFactory(EntityManager entityManager, Validator validator) {
			super(entityManager);
			this.validator = validator;
			LOGGER.debug("RepositoryFactory, validator: {}, entityManager: {}", validator, entityManager);

		}

		@Override
		protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
			LOGGER.debug("getTargetRepository, information: {}, entityManager: {}", information, entityManager);
			JpaEntityInformation entityInformation = this.getEntityInformation(information.getDomainType());
			BaseRepositoryImpl repository = new BaseRepositoryImpl<T, PK>(entityInformation, entityManager, this.validator);
			LOGGER.debug("getTargetRepository, repository: {}", repository);
			return repository;
		}

		@Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
			LOGGER.debug("getRepositoryBaseClass, metadata: {}", metadata);
			// The RepositoryMetadata can be safely ignored, it is used by the
			// JpaRepositoryFactory
			// to check for QueryDslJpaRepository's which is out of scope.
			return BaseRepositoryImpl.class;
		}
	}

	/**
	 * Fixes exception `org.springframework.beans.NotWritablePropertyException`:
	 * Invalid property 'mappingContext' of bean class
	 * [...]: Bean property 'mappingContext' is not writable or has an invalid setter method.
	 * Does the parameter type of the setter match the return type of the getter?"
	 *
	 */
	@Override
	public void setMappingContext(MappingContext<?, ?> mappingContext) {
		super.setMappingContext(mappingContext);
	}
}
