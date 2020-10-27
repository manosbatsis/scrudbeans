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
package com.github.manosbatsis.scrudbeans.registry;

import com.github.manosbatsis.scrudbeans.ScrudBeansProperties;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.IdentifierAdapterBean;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.FieldInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfoRegistry;
import com.github.manosbatsis.scrudbeans.specification.SpecificationUtils;
import com.github.manosbatsis.scrudbeans.specification.factory.AnyToOnePredicateFactory;
import com.github.manosbatsis.scrudbeans.util.ClassUtils;
import com.github.manosbatsis.scrudbeans.util.EntityUtil;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Provides metadata for all JPA entity types and generates missing model-based components
 * i.e. <code>Repository</code>, <code>Service</code> and
 * <code>Controller</code> mdd
 */
@Slf4j
public class JpaModelInfoRegistry implements BeanDefinitionRegistryPostProcessor, EnvironmentAware, ModelInfoRegistry {

	public static final String BEAN_NAME = "modelInfoRegistry";
	private static final Logger log = LoggerFactory.getLogger(JpaModelInfoRegistry.class);

	//private ApplicationContext applicationContext;
	private ScrudBeansProperties scrudBeansProperties;

	private Map<Class<?>, ModelInfo> modelEntries = new HashMap<>();

	private Map<Class<?>, Class<?>> handlerModelTypes = new HashMap<>();


	@Override
	public ModelInfo getEntryFor(Class<?> modelClass) {
		Assert.notNull(modelClass, "Cannot get entry for null modelClass");
		return this.modelEntries.get(modelClass);
	}

	@Override
	public List<ModelInfo> getEntries() {
		ArrayList<ModelInfo> entries = new ArrayList<>(this.modelEntries.size());
		entries.addAll(this.modelEntries.values());
        return entries;
    }

    @Override
    public List<Class> getTypes() {
        ArrayList<Class> entries = new ArrayList<>(this.modelEntries.size());
        entries.addAll(this.modelEntries.keySet());
        return entries;
    }

    protected void scanPackages(Iterable<String> basePackages) {
        // scan for models
        for (String basePackage : basePackages) {
            log.trace("scanPackages " + basePackage);
            Set<BeanDefinition> entityBeanDefs = EntityUtil.findAllModels(basePackage);
            for (BeanDefinition beanDef : entityBeanDefs) {
                Class<?> modelType = ClassUtils.getClass(beanDef.getBeanClassName());
				this.addEntryFor(modelType);
            }
        }
        for (ModelInfo modelInfo : this.getEntries()) {
			setRelatedFieldsModelInfo(modelInfo, modelInfo.getToOneFieldNames());
			setRelatedFieldsModelInfo(modelInfo, modelInfo.getToManyFieldNames());
		}
	}

	protected void scanForHelpers(Iterable<String> basePackages) {
		// scan for models
		for (String basePackage : basePackages) {
			Set<BeanDefinition> entityBeanDefs = EntityUtil.findAllHelpers(basePackage);
			for (BeanDefinition beanDef : entityBeanDefs) {
				Class<?> beanType = ClassUtils.getClass(beanDef.getBeanClassName());
				EntityPredicateFactory predicateFactoryAnnotation = beanType.getAnnotation(EntityPredicateFactory.class);
				if (predicateFactoryAnnotation != null) {
					try {
						Class entityClass = Class.forName(predicateFactoryAnnotation.entityClass());
						SpecificationUtils.addFactoryForClass(entityClass, (AnyToOnePredicateFactory) ClassUtils.newInstance(beanType));
					} catch (ClassNotFoundException e) {
						log.error("Failed registering AnyToOnePredicateFactory type {}, target class not found: {}",
								beanType, predicateFactoryAnnotation.entityClass());
					}
				}
				IdentifierAdapterBean identifierAdapterAnnotation = beanType.getAnnotation(IdentifierAdapterBean.class);
				if (identifierAdapterAnnotation != null) {
					String className = identifierAdapterAnnotation.className();
					try {
						Class modelClass = Class.forName(className);
						IdentifierAdaptersRegistry.addAdapterForClass(modelClass,
								(com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter) ClassUtils.newInstance(beanType));
					} catch (ClassNotFoundException e) {
						log.error("Failed registering IdentifierAdapterBean type {}, target class not found: {}",
								beanType, className);
					}
				}
			}
		}
	}

	/**
	 * Set the reverse entity ModelInfo for each relationship field
	 * @param modelInfo
	 * @param fNames
	 */
	private void setRelatedFieldsModelInfo(ModelInfo modelInfo, Set<String> fNames) {
		for (String fieldName : fNames) {
			FieldInfo field = modelInfo.getField(fieldName);
            log.debug("setRelatedFieldModelInfo, model: {}, fieldName: {}, field: {}", modelInfo.getModelType(), fieldName, field);
            Class<?> fieldModelType = field.getFieldModelType();

            ModelInfo relatedModelInfo = fieldModelType != null ? this.getEntryFor(field.getFieldModelType()) : null;
            if (relatedModelInfo != null) {
                field.setRelatedModelInfo(relatedModelInfo);
            }
        }
    }

    protected <T, PK extends Serializable> void addEntryFor(Class<T> modelClass) {
		Assert.notNull(modelClass, "Parameter modelClass cannot be null");

		// ignore abstract classes
		if (Modifier.isAbstract(modelClass.getModifiers())) {
			log.warn("addEntryFor, given model class is abstract: {}", modelClass);
		}

		log.debug("addEntryFor model class {}", modelClass.getCanonicalName());
		// check for existing
		if (this.modelEntries.containsKey(modelClass)) {
			throw new RuntimeException("ModelInfoRegistry entry already exists, failed to add model type: " + modelClass.getCanonicalName());
		}

		// create entry
		ModelInfo entry = new ModelInfoImpl(modelClass);

		// add entry
		this.modelEntries.put(modelClass, entry);
	}

	/**
	 * Scan the configured packages for models that trigger component generation
	 * @param registry
	 * @throws BeansException
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		log.debug("postProcessBeanDefinitionRegistry, ScrudBeansProperties: {}", this.scrudBeansProperties);
		Set<String> packagesToScan = scrudBeansProperties.getPackagesToScanAsSet();
		// register predicate factories
		this.scanForHelpers(packagesToScan);
		// scan for and create the rest
		this.scanPackages(packagesToScan);

		for (ModelInfo info : this.getEntries()) {
			this.resolveInverseFields(info);
			if (!this.handlerModelTypes.containsKey(info.getModelControllerType())) {
				if (info.getModelControllerType() != null && info.getModelType() != null) {
					log.debug("postProcessBeanDefinitionRegistry, adding handlerModelType entry: {}:{}, linkable: {}", info.getModelControllerType(), info.getModelType(), info.isLinkableResource());
					this.handlerModelTypes.put(info.getModelControllerType(), info.getModelType());
				}
			}
		}

	}

	protected void resolveInverseFields(ModelInfo modelInfo) {
		resolveInverseFields(modelInfo, modelInfo.getToOneFieldNames());
		resolveInverseFields(modelInfo, modelInfo.getToManyFieldNames());
	}

	protected void resolveInverseFields(ModelInfo modelInfo, Set<String> fieldNames) {
		for (String fieldName : fieldNames) {
			FieldInfo field = modelInfo.getField(fieldName);
			if (!field.isInverse()) {
				if (field.getFieldModelType() != null) {

					// scan ModelInfo on the other side to find an inverse JPA mapping, if any
					ModelInfo inverseModelInfo = this.getEntryFor(field.getFieldModelType());

					// warn if the inverse field type model info does not exist
					if (inverseModelInfo == null) {
						log.warn("resolveInverseFields: No model info entry found for type: {}", field.getFieldModelType());
					}
					else {
						Set<String> inversePropertyNames = inverseModelInfo.getInverseFieldNames();

						// go over inverse fields to find a match, if any
						for (String inversePropertyName : inversePropertyNames) {
							FieldInfo inverseField = inverseModelInfo.getField(inversePropertyName);
							if (inverseField != null && fieldName.equals(inverseField.getReverseFieldName())) {
								field.setReverseFieldName(inverseField.getFieldName());
								break;
							}
						}
					}
				}
				else {

					log.debug("resolveInverseFields, not fieldModelType found for model: {}, field: {}", modelInfo.getModelType(), fieldName);
				}

			}
			// break if found
			Optional<String> reverseFieldName = field.getReverseFieldName();
			if (reverseFieldName.isPresent()) {
				log.debug("resolveInverseFields, resolved field: {}, reverse: {}", field.getFieldName(), reverseFieldName.get());
				break;
			}
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

	}

	@Override
	public Class<?> getHandlerModelType(@NonNull Class<?> handlerType) {
        /*
        if(String.class.isAssignableFrom(handler.getClass())){

            log.debug("getHandlerModelType, handler is a spring bean name: {}", handler);
            handler = getApplicationContext().getBean(handler.toString());
        }
        Class<?> modelType = this.handlerModelTypes.get(AopProxyUtils.ultimateTargetClass(handler));
        log.debug("getHandlerModelType, modelType: {}", modelType);

        return modelType;
        */
		return this.handlerModelTypes.get(handlerType);
	}

	/**
	 * Set the {@code Environment} that this component runs in.
	 * @param environment
	 */
	@Override
	public void setEnvironment(Environment environment) {
		this.scrudBeansProperties = buildScrudBeansProperties((ConfigurableEnvironment) environment);
	}

	/**
	 * Parse spring-boot config files to a [ScrudBeansProperties] instance
	 */
	private ScrudBeansProperties buildScrudBeansProperties(ConfigurableEnvironment environment) {
		Iterable<ConfigurationPropertySource> sources = ConfigurationPropertySources.from(environment.getPropertySources());
		Binder binder = new Binder(sources);
		return binder.bind("scrudbeans", ScrudBeansProperties.class).orElse(new ScrudBeansProperties());
	}
}
