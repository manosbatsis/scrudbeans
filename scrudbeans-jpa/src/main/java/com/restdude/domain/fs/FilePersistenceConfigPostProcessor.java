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
package com.restdude.domain.fs;

import com.restdude.mdd.service.FilePersistenceService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;

public class FilePersistenceConfigPostProcessor
		implements BeanDefinitionRegistryPostProcessor {

	private static final Logger LOGGER = LoggerFactory.getLogger(FilePersistenceConfigPostProcessor.class);

	@SuppressWarnings("rawtypes")
	private Class repositoryClass;

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry)
			throws BeansException {
		if (this.repositoryClass == null) {

			// get class canonical name from configuration
			String repositoryClassName = "";//TODO: ConfigurationFactory.getConfiguration().getString(ConfigurationFactory.FS_IMPL_CLASS);
			if (StringUtils.isNotBlank(repositoryClassName)) {
				// get a class instance
				try {
					this.getClass();
					this.repositoryClass = FilePersistenceConfigPostProcessor.class.forName(repositoryClassName);
				}
				catch (ClassNotFoundException e) {
					LOGGER.error("Failed to obtain repository class {}, will fallback to dummy, non-persisting impl", repositoryClassName);
				}
			}
			else {

				this.repositoryClass = DummyFilePersistenceServiceImpl.class;
			}
		}
		// dummy fallback class
		if (this.repositoryClass == null) {
			LOGGER.warn("No filesystem repository implementation was configured, falling back to dummy, non-persisting impl");
			this.repositoryClass = DummyFilePersistenceServiceImpl.class;
		}

		LOGGER.debug("Setting file persistence service BEAN {}", this.repositoryClass.getCanonicalName());
		// create bean
		RootBeanDefinition beanDefinition =
				new RootBeanDefinition(this.repositoryClass); //The service implementation
		beanDefinition.setTargetType(FilePersistenceService.class); //The service interface
		//beanDefinition.setRole(BeanDefinition.ROLE_APPLICATION);
		registry.registerBeanDefinition(FilePersistenceService.BEAN_ID, beanDefinition);

		LOGGER.debug("Added FilePersistenceService bean using: " + this.repositoryClass.getName());
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// TODO Auto-generated method stub

	}
}