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

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.Validator;

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.*;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.core.support.RepositoryFactorySupport;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class ModelRepositoryFactoryBean<R extends JpaRepository<T, PK>, T extends Persistable<PK>, PK extends Serializable>
        extends JpaRepositoryFactoryBean<R, T, PK> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ModelRepositoryFactoryBean.class);

    private Validator validator;

    private @Nullable
    EntityManager entityManager;

    public ModelRepositoryFactoryBean(Class<? extends R> repositoryInterface) {
        super(repositoryInterface);
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
        return new ModelRepositoryFactory<T, PK>(entityManager, validator);
    }

    private static class ModelRepositoryFactory<T extends Persistable<PK>, PK extends Serializable> extends JpaRepositoryFactory {

        private EntityManager entityManager;
        private Validator validator;

        public ModelRepositoryFactory(EntityManager entityManager, Validator validator) {
            super(entityManager);
            this.entityManager = entityManager;
            this.validator = validator;
        }

        @Override
        public <T, PK> JpaEntityInformation<T, PK> getEntityInformation(Class<T> domainClass) {
            Assert.notNull(domainClass, "Domain class must not be null!");
            Assert.notNull(entityManager, "EntityManager must not be null!");
            if (Persistable.class.isAssignableFrom(domainClass)) {
                return new ModelEntityInformation(domainClass, entityManager.getMetamodel());
            } else {
                return super.getEntityInformation(domainClass);
            }
        }

        @Override
        protected JpaRepositoryImplementation<?, ?> getTargetRepository(RepositoryInformation information, EntityManager entityManager) {
            ModelRepositoryImpl repository = new ModelRepositoryImpl<>((Class<T>) information.getDomainType(), entityManager);
            repository.setValidator(this.validator);
            return repository;
        }

        @Override
		protected Class<?> getRepositoryBaseClass(RepositoryMetadata metadata) {
            return ModelRepositoryImpl.class;
        }
	}
}
