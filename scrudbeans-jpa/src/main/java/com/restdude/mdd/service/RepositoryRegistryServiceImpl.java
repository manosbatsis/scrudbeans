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
package com.restdude.mdd.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.data.repository.core.RepositoryInformation;
import org.springframework.data.repository.query.QueryMethod;
import org.springframework.data.repository.support.Repositories;
import org.springframework.stereotype.Service;

/**
 * Provides dynamic access to model repositories and their metadata.
 * The {@link Repositories} instance is created as late as possible, i.e. after a context refresh..
 */
@Service("repositoryRegistryService")
class RepositoryRegistryServiceImpl implements RepositoryRegistryService {

	private ListableBeanFactory listableBeanFactory;

	private Repositories repositories;

	@Autowired
	public void setListableBeanFactory(ListableBeanFactory listableBeanFactory) {
		this.listableBeanFactory = listableBeanFactory;
	}

	@EventListener
	public void handleContextRefresh(ContextRefreshedEvent event) {
		this.repositories = new Repositories(listableBeanFactory);

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean hasRepositoryFor(Class<?> domainClass) {
		return this.repositories.hasRepositoryFor(domainClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object getRepositoryFor(Class<?> domainClass) {
		return this.repositories.getRepositoryFor(domainClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <T, S extends Serializable> EntityInformation<T, S> getEntityInformationFor(Class<?> domainClass) {
		return this.repositories.getEntityInformationFor(domainClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<RepositoryInformation> getRepositoryInformationFor(Class<?> domainClass) {
		return this.repositories.getRepositoryInformationFor(domainClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<RepositoryInformation> getRepositoryInformation(Class<?> repositoryInterface) {
		return this.repositories.getRepositoryInformation(repositoryInterface);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PersistentEntity<?, ?> getPersistentEntity(Class<?> domainClass) {
		return this.repositories.getPersistentEntity(domainClass);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<QueryMethod> getQueryMethodsFor(Class<?> domainClass) {
		return this.repositories.getQueryMethodsFor(domainClass);
	}

}
