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
package com.github.manosbatsis.scrudbeans.service;

import com.github.manosbatsis.scrudbeans.repository.ModelRepository;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.EntityManager;
import java.io.Serializable;

/**
 * SCRUD service handling a specific type using a {@link ModelRepository}
 *
 * @param <T>  Your resource class to manage, usually an entity class
 * @param <PK> EntityModel id type, usually Long or String
 * @param <R>  The repository class to automatically inject
 */
@Slf4j
public class AbstractJpaPersistableModelServiceImpl<T, PK extends Serializable, R extends ModelRepository<T, PK>>
		extends AbstractPersistableModelServiceImpl<T, PK, R>
		implements JpaPersistableModelService<T, PK> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractJpaPersistableModelServiceImpl.class);

	/** Obtain the EntityManager of the underlying Repository */
	@Override
	public EntityManager getEntityManager() {
		return this.repository.getEntityManager();
	}

}
