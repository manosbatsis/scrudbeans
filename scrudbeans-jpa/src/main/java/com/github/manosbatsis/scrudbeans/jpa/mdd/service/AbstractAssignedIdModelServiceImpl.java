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
package com.github.manosbatsis.scrudbeans.jpa.mdd.service;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;

import com.github.manosbatsis.scrudbeans.api.mdd.repository.ModelRepository;
import com.github.manosbatsis.scrudbeans.jpa.mdd.model.AbstractAssignedIdPersistableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.method.P;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

/**
 * Created by manos on 30/11/2016.
 */
public class AbstractAssignedIdModelServiceImpl<T extends AbstractAssignedIdPersistableModel<PK>, PK extends Serializable, R extends ModelRepository<T, PK>>
		extends AbstractPersistableModelServiceImpl<T, PK, R> implements AbstractAssignedIdModelService<T, PK> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAssignedIdModelServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	@Transactional(readOnly = false)
	public T findOrCreate(@P("resource") T resource) {
		Assert.notNull(resource, "Resource can't be null");
		Assert.notNull(resource.getId(), "Resource PK can't be null");

		EntityManager em = this.repository.getEntityManager();

		// check for existing
		T persisted = em.find(this.getDomainClass(), resource.getId());
		if (persisted != null) {
			LOGGER.debug("Returning pre-persisted {} with ID: {}", this.getDomainClass().getName(), persisted.getId());
		}
		else {

			// cannot create new transaction in shared entity manager, create another
			EntityManager innerEntityManager = this.repository.getEntityManager().getEntityManagerFactory().createEntityManager();
			innerEntityManager.getTransaction().begin();

			try {
				innerEntityManager.persist(resource);
				innerEntityManager.getTransaction().commit();
				persisted = resource;
				LOGGER.debug("Returning newly-persisted {} with ID: {}", this.getDomainClass().getName(), persisted.getId());
			}
			catch (PersistenceException ex) {

				//This may be a unique constraint violation or it could be some
				//other issue.  We will attempt to determine which it is by trying
				//to find the entity.  Either way, our attempt failed and we
				//roll back the tx.
				innerEntityManager.getTransaction().rollback();
				persisted = em.find(this.getDomainClass(), resource.getId());
				if (persisted == null) {
					//Must have been some other issue
					throw ex;
				}

				//Either it was a unique constraint violation or we don't
				//care because someone else has succeeded
				LOGGER.debug("Returning elsewhere-persisted {} with ID: {}", this.getDomainClass().getName(), persisted.getId());

			}
			catch (Throwable t) {
				innerEntityManager.getTransaction().rollback();
				throw t;
			}
			finally {
				innerEntityManager.close();
			}

			if (persisted == null) {
				throw new RuntimeException("Could not find or create resource " + resource);
			}
		}

		return persisted;
	}
}
