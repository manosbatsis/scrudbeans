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

import java.io.Serializable;
import java.util.List;
import java.util.Set;

import com.github.manosbatsis.scrudbeans.api.domain.IdModel;

/**
 * CRUD Service interface.
 *
 * @param <T>
 *            Your resource POJO to manage, maybe an entity or DTO class
 * @param <PK>
 *            Resource id type, usually Long or String
 */
public interface ModelService<T extends IdModel<PK>, PK extends Serializable> extends BaseService {


	/**
	 * Get the entity Class corresponding to the generic T
	 *
	 * @return the corresponding entity Class
	 */
	Class<T> getDomainClass();

	/**
	 * Create a new resource.
	 *
	 * @param resource Resource to create
	 * @return new resource
	 */
	T create(T resource);

	/**
	 * Override to handle post-create
	 *
	 * @param resource The created resource
	 */
	void postCreate(T resource);

	/**
	 * Update an existing resource.
	 *
	 * @param resource Resource to update
	 * @return resource updated
	 */
	T update(T resource);


	/**
	 * Partially update an existing resource.
	 *
	 * @param resource Resource to update
	 * @return resource updated
	 */
	T patch(T resource);

	/**
	 * Delete an existing resource.
	 *
	 * @param resource Resource to delete
	 */
	void delete(T resource);

	/**
	 * Delete an existing resource.
	 *
	 * @param id Resource id
	 */
	void delete(PK id);

	/**
	 * Find resource by id.
	 *
	 * @param id Resource id
	 * @return resource
	 */
	T findById(PK id);

	/**
	 * Find resources by their ids.
	 *
	 * @param ids Resource ids
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
