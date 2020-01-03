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

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public abstract class AbstractModelServiceImpl<T extends Persistable<PK>, PK extends Serializable>
        extends AbstractBaseServiceImpl
        implements ModelService<T, PK> {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractModelServiceImpl.class);

    /**
     * Get the entity Class corresponding to the generic T
     *
     * @return the corresponding entity Class
     */
	@Override
	public Class<T> getDomainClass() {
		return null;
	}

    /**
     * Create a new resource.
     *
     * @param resource EntityModel to create
     * @return new resource
     */
	@Override
	public T create(T resource) {
		return null;
	}

	/**
	 * Override to handle post-create
	 *
	 * @param resource The created resource
	 */
	@Override
	public void postCreate(T resource) {

    }

    /**
     * Update an existing resource.
     *
     * @param resource EntityModel body to use for the update
     * @return resource updated
     */
    @Override
    public T update(T resource) {
        return null;
    }

    /**
     * Partially update an existing resource.
     *
     * @param resource EntityModel body to use for as patch
     * @return resource updated
     */
    @Override
    public T patch(T resource) {
        return null;
    }

    /**
     * Delete an existing resource.
     *
     * @param resource EntityModel to delete
     */
    @Override
    public void delete(T resource) {

    }

    /**
     * Delete an existing resource.
     *
     * @param id EntityModel id
	 */
	@Override
	public void delete(PK id) {

    }

    /**
     * Find resource by id.
     *
     * @param id EntityModel id
     * @return resource
	 */
	@Override
	public T findById(PK id) {
		return null;
    }

    /**
     * Find resources by their ids.
     *
     * @param ids EntityModel ids
     * @return a list of retrieved resources, empty if no resource found
	 */
	@Override
	public List<T> findByIds(Set<PK> ids) {
		return null;
	}

	/**
	 * Find all resources.
	 *
	 * @return a list of all resources.
	 */
	@Override
	public List<T> findAll() {
		return null;
	}

	/**
	 * Count all resources.
	 *
	 * @return number of resources
	 */
	@Override
	public Long count() {
		return null;
	}
}
