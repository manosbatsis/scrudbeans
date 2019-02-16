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
import java.util.Optional;

import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.support.Repositories;

/**
 * Created by manos on 4/3/2017.
 */
public interface RepositoryRegistryService {


	/**
	 * @see Repositories#hasRepositoryFor(Class)
	 */
	boolean hasRepositoryFor(Class<?> domainClass);

	/**
	 * org.springframework.data.repository.support.Repositories#getRepositoryFor(java.lang.Class)
	 */
	Object getRepositoryFor(Class<?> domainClass);

	/**
	 * @see Repositories#getEntityInformationFor(Class)
	 */
	<T, S extends Serializable> EntityInformation<T, S> getEntityInformationFor(Class<?> domainClass);

	/**
	 *
	 * @see Repositories#getRepositoryInformationFor(Class)
	 */
	Optional<RepositoryInformation> getRepositoryInformationFor(Class<?> domainClass);

	/**
	 * @see Repositories#getRepositoryInformation(Class)
	 */
	Optional<RepositoryInformation> getRepositoryInformation(Class<?> repositoryInterface);

	/**
	 * @see Repositories#getPersistentEntity(Class)
	 */
	PersistentEntity<?, ?> getPersistentEntity(Class<?> domainClass);

	/**
	 * @see Repositories#getQueryMethodsFor(Class)
	 */
	List<QueryMethod> getQueryMethodsFor(Class<?> domainClass);
}
