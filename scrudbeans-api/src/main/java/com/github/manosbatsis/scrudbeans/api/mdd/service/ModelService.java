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
package com.github.manosbatsis.scrudbeans.api.mdd.service;

import com.github.manosbatsis.kotlin.utils.api.Dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * CRUD Service interface.
 *
 * @param <T>  Your resource POJO to manage, maybe an entity or DTO class
 * @param <PK> EntityModel id type, usually Long or String
 */
public interface ModelService<T, PK extends Serializable> extends BaseService {


    /**
     * Get the entity Class corresponding to the generic T
     *
     * @return the corresponding entity Class
     */
    Class<T> getDomainClass();

    PK getIdAttribute(Object o);

	void setIdAttribute(Object o, PK value);

	/**
	 * Create a new resource.
	 *
	 * @param resource EntityModel to create
	 * @return new resource
	 */
	T create(T resource);

	/**
	 * Create a new resource.
	 *
	 * @param resource EntityModel to create
	 * @return new resource
	 */
	T create(Dto<T> resource);

	/**
	 * Override to handle post-create
	 *
	 * @param resource The created resource
	 */
	void postCreate(T resource);

	/**
	 * Override to handle post-update
	 *
	 * @param resource The updated resource
	 */
	void postUpdate(T resource);

	/**
	 * Override to handle post-delete
	 *
	 * @param resource The created resource
	 */
	void postDelete(T resource);

	/**
	 * Update an existing resource.
	 *
	 * @param resource EntityModel to update
	 * @return resource updated
	 */
	T update(T resource);

	/**
	 * Update an existing resource.
	 *
	 * @param resource EntityModel to update
	 * @return resource updated
	 */
	T update(Dto<T> resource);


	/**
	 * Partially update an existing resource.
	 *
	 * @param resource EntityModel to update
	 * @return resource updated
	 */
	T patch(T resource);

	/**
	 * Partially update an existing resource.
	 *
	 * @param resource EntityModel to update
	 * @return resource updated
	 */
	T patch(Dto<T> resource);

	/**
	 * Delete an existing resource.
	 *
	 * @param resource EntityModel to delete
	 */
	void delete(T resource);

	/**
     * Delete an existing resource.
     *
     * @param id EntityModel id
	 */
	void delete(PK id);

    /**
     * Find resource by id.
     *
     * @param id EntityModel id
     * @return resource
	 */
	T findById(PK id);

    /**
     * Find resources by their ids.
     *
     * @param ids EntityModel ids
     * @return a list of retrieved resources, empty if no resource found
	 */
	List<T> findByIds(Set<PK> ids);

	/**
	 * Find all resources.
	 *
	 * @return a list of all resources.
	 */
	List<T> findAll();

	/**
	 * Count all resources.
	 *
	 * @return number of resources
	 */
	Long count();

}
