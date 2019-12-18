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

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.util.EntityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

public class ModelRepositoryFactoryBean<R extends Repository<T, PK>, T, PK>
        extends JpaRepositoryFactoryBean<R, T, PK> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRepositoryFactoryBean.class);

    private Validator validator;

    private @Nullable
    EntityManager entityManager;

    public ModelRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
        LOGGER.debug("ModelRepositoryFactoryBean for repo interface: {}", repositoryInterface.getCanonicalName());

    }

    @Override
    @PersistenceContext
    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
        super.setEntityManager(entityManager);
    }

    @Autowired
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    @Override
    protected RepositoryFactorySupport createRepositoryFactory(EntityManager entityManager) {
        ModelRepositoryFactory<T, PK> repositoryFactorySupport = new ModelRepositoryFactory<>(entityManager);
        repositoryFactorySupport.setValidator(this.validator);
        return repositoryFactorySupport;
    }

    private static class ModelRepositoryFactory<T, PK> extends JpaRepositoryFactory {

        private EntityManager entityManager;
        private Validator validator;

        public ModelRepositoryFactory(EntityManager entityManager) {
            super(entityManager);
            this.entityManager = entityManager;
        }

        @Override
        public <T, PK> JpaEntityInformation<T, PK> getEntityInformation(Class<T> domainClass) {
            Assert.notNull(domainClass, "Domain class must not be null!");
            Assert.notNull(entityManager, "EntityManager must not be null!");
            JpaEntityInformation<T, PK> entityInfo;
            if (Persistable.class.isAssignableFrom(domainClass)) {
                entityInfo = new ModelEntityInformation(domainClass, entityManager.getMetamodel());
            } else {
                entityInfo = super.getEntityInformation(domainClass);
            }
            LOGGER.debug("getEntityInformation for {}: {}", domainClass, entityInfo.toString());

            return entityInfo;
        }


        /**
         * Prepares a {@link ModelRepositoryImpl} instance if a ScrudBean,
         * a regular {@link JpaRepositoryImplementation} otherwise.
         */
        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            Class<?> domainType = information.getDomainType();
            JpaRepositoryImplementation result;
            if (EntityUtil.isScrudBean(domainType)) {
                ModelRepositoryImpl repository = new ModelRepositoryImpl(domainType, entityManager);
                repository.setValidator(this.validator);
                result = repository;
            } else {
                result = super.getTargetRepository(information, entityManager);
            }

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("getTargetRepository for {} returns a {}", domainType.getCanonicalName(), result.getClass().getCanonicalName());

            return result;
        }

        /**
         * Prepares the {@link ModelRepositoryImpl} class if a ScrudBean,
         * whatever super will otherwise.
         */
        @Override
        protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            Class<?> result;
            Class<?> domainType = metadata.getDomainType();
            if (EntityUtil.isScrudBean(domainType)) result = ModelRepositoryImpl.class;
            else result = super.getRepositoryBaseClass(metadata);

            if (LOGGER.isDebugEnabled())
                LOGGER.debug("getRepositoryBaseClass for {} returns a {}", domainType.getCanonicalName(), result.getCanonicalName());

            return result;
        }

        public void setValidator(Validator validator) {
            this.validator = validator;
        }
    }
}
