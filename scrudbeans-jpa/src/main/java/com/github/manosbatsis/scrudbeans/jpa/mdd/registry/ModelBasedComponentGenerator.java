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
package com.github.manosbatsis.scrudbeans.jpa.mdd.registry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Named;
import javax.persistence.Entity;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.controller.ModelController;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ModelRelatedResource;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudResource;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.mdd.repository.ModelRepository;
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService;
import com.github.manosbatsis.scrudbeans.api.mdd.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.api.specification.IPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.util.Mimes;
import com.github.manosbatsis.scrudbeans.common.util.ClassUtils;
import com.github.manosbatsis.scrudbeans.javassist.CreateClassCommand;
import com.github.manosbatsis.scrudbeans.javassist.JavassistBaseUtil;
import com.github.manosbatsis.scrudbeans.jpa.mdd.controller.AbstractModelServiceBackedController;
import com.github.manosbatsis.scrudbeans.jpa.mdd.repository.ModelRepositoryFactoryBean;
import com.github.manosbatsis.scrudbeans.jpa.mdd.service.AbstractModelServiceImpl;
import com.github.manosbatsis.scrudbeans.jpa.mdd.service.AbstractPersistableModelServiceImpl;
import com.github.manosbatsis.scrudbeans.jpa.mdd.util.ModelContext;
import com.github.manosbatsis.scrudbeans.jpa.specification.SpecificationUtils;
import com.github.manosbatsis.scrudbeans.jpa.specification.factory.AnyToOnePredicateFactory;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.javers.spring.annotation.JaversSpringDataAuditable;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.GenericTypeResolver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.util.Assert;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Generates <code>Repository</code>, <code>Service</code>,
 * <code>Controller</code> and other components for the given {@link ModelInfoImpl} entries
 * {@link ScrudResource} or
 * {@link ModelRelatedResource}.
 */
@Slf4j
public class ModelBasedComponentGenerator {

	private Map<Class<?>, ModelContext> entityModelContextsMap = new HashMap<Class<?>, ModelContext>();

	private Map<Class<?>, ModelInfo> modelInfoEntries;

	private BeanDefinitionRegistry registry;

	private Iterable<String> basePackages;

	private String basePath;

	private String defaultParentPath;

	public ModelBasedComponentGenerator(BeanDefinitionRegistry registry, Map<Class<?>, ModelInfo> modelInfoEntries, Iterable<String> basePackages, String basePath, String defaultParentPath) {
		this.modelInfoEntries = modelInfoEntries;
		this.registry = registry;
		this.basePackages = basePackages;
		this.basePath = basePath;
		this.defaultParentPath = defaultParentPath;
		log.debug("Initialized with modelInfoEntries: {}", modelInfoEntries);
	}

	/**
	 * Create and register missing model-based components
	 */
	public void createComponentsFor() {
		//try {
			createModelContexts();
			findExistingBeans();
			createMissingBeans();
			log.info("Completed generation");
		//}
		//catch (Exception e) {
		//	log.error("Failed generating ApiResources", e);
		//	throw new RuntimeException("Failed generating ApiResources", e);
		//}
	}

	// @Override
	protected void createModelContexts() {
		Collection<ModelInfo> modelRegistryEntries = this.modelInfoEntries.values();
		for (ModelInfo modelInfo : modelRegistryEntries) {
			Class<?> modelType = modelInfo.getModelType();
			log.info("Found resource model class {}", modelType.getCanonicalName());
			entityModelContextsMap.put(modelType, new ModelContext(modelInfo));
		}
	}

	private void createMissingBeans() {

		for (Class<?> model : this.entityModelContextsMap.keySet()) {
			// TODO: add related, after ensuring we have the necessary parent config set

			ModelContext modelContext = this.entityModelContextsMap.get(model);

			// create *ToOne predicates for JPA specifications
			createPredicateFactory(modelContext);

			// create repository, service, and controller components
			if (modelContext.getModelInfo().getScrudResource() != null || model.isAnnotationPresent(ModelRelatedResource.class)) {
				// TODO
				if (modelContext.getModelInfo().isJpaEntity()) {
					createRepository(modelContext);
					createService(modelContext);
					createController(modelContext);
				}
			}
		}

	}

	/**
	 * Creates an {@link IPredicateFactory} instance that is parameterized for a specific entity model. The
	 * predicate factory is registered using {@link SpecificationUtils#addFactoryForClass(Class, IPredicateFactory)}
	 *
	 * @param modelContext
	 * @see SpecificationUtils#addFactoryForClass(Class, IPredicateFactory)
	 */
	protected void createPredicateFactory(ModelContext modelContext) {
		if (modelContext.getModelType().isAnnotationPresent(Entity.class)) {
			IPredicateFactory predicateFactory = SpecificationUtils.getPredicateFactoryForClass(modelContext.getModelType());
			// only add if not already set
			if (predicateFactory == null) {
				String className = "AnyToOne" + modelContext.getGeneratedClassNamePrefix() + "PredicateFactory";
				String fullClassName = new StringBuffer(modelContext.getBeansBasePackage())
						.append(".specification.")
						.append(className).toString();

				// gfire up a create command
				CreateClassCommand createPredicateCmd = new CreateClassCommand(fullClassName,
						AnyToOnePredicateFactory.class);

				// grab the generic types
				List<Class<?>> genericTypes = modelContext.getGenericTypes();
				//LOGGER.debug("createPredicateFactory, Creating class {}, genericTypes: {}", fullClassName, genericTypes);
				createPredicateCmd.setGenericTypes(genericTypes);

				// create and return the predicate class
				Class<IPredicateFactory> factoryType = (Class<IPredicateFactory>) JavassistBaseUtil.createClass(createPredicateCmd);

				predicateFactory = ClassUtils.newInstance(factoryType);

				SpecificationUtils.addFactoryForClass(modelContext.getModelType(), predicateFactory);
			}
			// note
			modelContext.setPredicateFactory(predicateFactory);
		}
	}

	/**
	 * Creates a controller for the given resource model. Consider the following
	 * entity annotation:
	 *
	 * <pre>
	 * {@code
	 * &#64;ScrudResource(pathFragment = "countries", apiName = "Countries", apiDescription = "Operations about countries") one
	 * }
	 * </pre>
	 *
	 * created for the Country class:
	 *
	 * <pre>
	 * {
	 * 	&#64;code
	 * 	&#64;Controller
	 * 	&#64;Api(tags = "Countries", description = "Operations about countries")
	 * 	&#64;RequestMapping(pathFragment = "/api/rest/countries", produces = { "application/json",
	 * 			"application/xml" }, consumes = { "application/json", "application/xml" })
	 * 	public class CountryController extends AbstractPersistableModelController<Country, String, CountryService> {
	 * 		private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);
	 *    }
	 * }
	 * </pre>
	 *
	 * @param modelContext
	 */
	protected void createController(ModelContext modelContext) {
		ModelInfo modelInfo = this.modelInfoEntries.get(modelContext.getModelType());
		Class<?> controllerClass = null;
		BeanDefinition beanDefinition = modelContext.getControllerDefinition();

		if (beanDefinition == null) {
			String className = "RestdudeGenerated" + modelContext.getGeneratedClassNamePrefix() + "Controller";
			String beanName = StringUtils.uncapitalize(className);
			String fullClassName = new StringBuffer(modelContext.getBeansBasePackage())
					.append(".controller.")
					.append(className).toString();

			// gfire up a create command
			CreateClassCommand createControllerCmd = new CreateClassCommand(fullClassName,
					modelContext.getControllerSuperClass());

			// grab the generic types
			if (ArrayUtils.isNotEmpty(modelContext.getControllerSuperClass().getTypeParameters())) {
				List<Class<?>> genericTypes = modelContext.getGenericTypes();
				genericTypes.add(modelContext.getServiceInterfaceType());
				createControllerCmd.setGenericTypes(genericTypes);
				//LOGGER.debug("createController, Creating class " + fullClassName +
				//        ", super: " + modelContext.getControllerSuperClass().getSource() +
				//        ", genericTypes: " + genericTypes);
			}

			// add @RestController stereotype annotation
			Map<String, Object> controllerMembers = new HashMap<String, Object>();
			controllerMembers.put("value", beanName);
			createControllerCmd.addTypeAnnotation(RestController.class, controllerMembers);

			// @RequestMapping
			String pattern = null;
			pattern = getRequestMapping(modelInfo);
			modelInfo.setRequestMapping(pattern);
			log.debug("getMappedModelControllerClass adding pattern: {}", pattern);

			Map<String, Object> requestMappingMembers = new HashMap<>();
			requestMappingMembers.put("value", new String[] {pattern});
			// add JSON and HAL defaults
			String[] defaultMimes = {MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE};
			requestMappingMembers.put("produces", defaultMimes);
			createControllerCmd.addTypeAnnotation(RequestMapping.class, requestMappingMembers);

			// add HATEOAS links support?
			Map<String, Object> exposesResourceForMembers = new HashMap<>();
			exposesResourceForMembers.put("value", modelContext.getModelType());
			createControllerCmd.addTypeAnnotation(ExposesResourceFor.class, exposesResourceForMembers);


			// set swagger Api annotation
			Map<String, Object> apiMembers = modelContext.getApiAnnotationMembers();
			if (MapUtils.isNotEmpty(apiMembers)) {
				createControllerCmd.addTypeAnnotation(Api.class, apiMembers);
			}

			// create and register controller class
			controllerClass = JavassistBaseUtil.createClass(createControllerCmd);

			// add service dependency
			String serviceDependency = StringUtils.uncapitalize(modelContext.getGeneratedClassNamePrefix()) + "Service";
			beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(controllerClass)
					.addDependsOn(serviceDependency).setAutowireMode(Autowire.BY_NAME.value()).getBeanDefinition();

			//LOGGER.debug("createController, registering bean: {}, class: {}, exposes resources: {}", beanName, controllerClass.getSimpleName(), controllerClass.getAnnotation(ExposesResourceFor.class));
			registry.registerBeanDefinition(beanName, beanDefinition);

		}
		else {
			controllerClass = ClassUtils.getClass(beanDefinition.getBeanClassName());
		}

		modelContext.setControllerDefinition(beanDefinition);
		modelInfo.setModelControllerType(controllerClass);

	}

	private String getRequestMapping(ModelInfo modelInfo) {
		String pattern;
		String modelUriComponent = modelInfo.getUriComponent();
		String modelParentPath = modelInfo.getParentPath(this.defaultParentPath);
		String modelBasePath = modelInfo.getBasePath(this.basePath);
		pattern = new StringBuffer("/")
				.append(modelBasePath)
				.append("/")
				.append(modelParentPath)
				.append("/")
				.append(modelUriComponent).toString();
		pattern = pattern.replaceAll("/{2,}", "/");
		return pattern;
	}


	protected void createService(ModelContext modelContext) {
		if (modelContext.getServiceDefinition() == null) {

			String className = modelContext.getGeneratedClassNamePrefix() + "Service";
			String beanName = StringUtils.uncapitalize(className);
			String fullClassName = new StringBuffer(modelContext.getBeansBasePackage())
					.append(".service.")
					.append(className).toString();
			//LOGGER.debug("createService interface: {}", fullClassName);


			// grab the generic types
			List<Class<?>> genericTypes = modelContext.getGenericTypes();

			// extend the base service interface
			Class<?> newServiceInterface = JavassistBaseUtil.createInterface(fullClassName,
					modelContext.getModelInfo().isJpaEntity() ? PersistableModelService.class : ModelService.class,
					genericTypes);

			ArrayList<Class<?>> interfaces = new ArrayList<Class<?>>(1);
			interfaces.add(newServiceInterface);

			// create a service implementation bean

			Class<?> serviceImplSuper = modelContext.getModelInfo().isJpaEntity() ? AbstractPersistableModelServiceImpl.class : AbstractModelServiceImpl.class;
			String newBImpllassName = new StringBuffer(modelContext.getBeansBasePackage())
					.append(".service.impl.")
					.append(className)
					.append("Impl").toString();
			//LOGGER.debug("createService class: {}", newBImpllassName);
			CreateClassCommand createServiceCmd = new CreateClassCommand(newBImpllassName, serviceImplSuper);
			createServiceCmd.setInterfaces(interfaces);
			createServiceCmd.setGenericTypes(genericTypes);
			// add repo type param if entity
			if (modelContext.getModelInfo().isJpaEntity()) {
				createServiceCmd.addGenericType(modelContext.getRepositoryType());
			}
			HashMap<String, Object> named = new HashMap<String, Object>();
			named.put("pathFragment", beanName);
			createServiceCmd.addTypeAnnotation(Named.class, named);

			// create and register a service implementation bean
			Class<?> serviceClass = JavassistBaseUtil.createClass(createServiceCmd);
			AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(serviceClass)
					.setAutowireMode(Autowire.BY_NAME.value()).getBeanDefinition();
			registry.registerBeanDefinition(beanName, def);

			// note in context as a dependency to a controller
			modelContext.setServiceDefinition(def);
			modelContext.setServiceInterfaceType(newServiceInterface);
			modelContext.setServiceImplType(serviceClass);

		}
		else {
			Class<?> serviceType = ClassUtils.getClass(modelContext.getServiceDefinition().getBeanClassName());
			// grab the service interface
			if (!serviceType.isInterface()) {
				Class<?>[] serviceInterfaces = serviceType.getInterfaces();
				if (ArrayUtils.isNotEmpty(serviceInterfaces)) {
					for (Class<?> interfaze : serviceInterfaces) {
						if (PersistableModelService.class.isAssignableFrom(interfaze)) {
							modelContext.setServiceInterfaceType(interfaze);
							break;
						}
					}
				}
			}
			Assert.notNull(modelContext.getRepositoryType(),
					"Found a service bean definition for " + modelContext.getGeneratedClassNamePrefix()
							+ "  but failed to figure out the service interface type.");
		}
	}

	protected void createRepository(ModelContext modelContext) {
		if (modelContext.getRepositoryDefinition() == null && modelContext.getModelInfo().isJpaEntity()) {
			Class<?> repoSUperInterface = ModelRepository.class;

			String className = modelContext.getGeneratedClassNamePrefix() + "Repository";
			String fullClassName = new StringBuffer(modelContext.getBeansBasePackage())
					.append(".repository.")
					.append(className).toString();

			// grab the generic types
			List<Class<?>> genericTypes = modelContext.getGenericTypes();
			log.debug("#createRepository: create repository: {}, genericTypes: {}", fullClassName, genericTypes);

			Map<Class<?>, Map<String, Object>> typeAnnotations = new HashMap<>();

			// TODO
			if (false/*modelContext.isAuditable()*/) {
				typeAnnotations.put(JaversSpringDataAuditable.class, null);
			}
			//            @
			// create the new interface
			Class<?> newRepoInterface = JavassistBaseUtil.createInterface(
					fullClassName
					, repoSUperInterface,
					genericTypes, typeAnnotations);


			// register using the uncapitalised className as the key
			AbstractBeanDefinition def = BeanDefinitionBuilder.rootBeanDefinition(ModelRepositoryFactoryBean.class)
					.addConstructorArgValue(newRepoInterface).setAutowireMode(Autowire.BY_NAME.value())
					.getBeanDefinition();
			//def.setDependsOn("entityManagerFactory", "localValidatorFactoryBean");
			//def.setSynthetic(false);
			//def.setAutowireCandidate(true);
			//def.setLazyInit(true);
			registry.registerBeanDefinition(StringUtils.uncapitalize(newRepoInterface.getSimpleName()), def);

			// note the repo in context
			modelContext.setRepositoryDefinition(def);
			modelContext.setRepositoryType(newRepoInterface);

		}
	}

	/**
	 * Iterate over registered beans to find any manually-created components
	 * (Controllers, Services, Repositories) we can skipp from generating.
	 *
	 */
	protected void findExistingBeans() {
		for (String beanDefName : registry.getBeanDefinitionNames()) {

			BeanDefinition d = registry.getBeanDefinition(beanDefName);

			if (d instanceof AbstractBeanDefinition && d.getBeanClassName() != null) {
				AbstractBeanDefinition def = (AbstractBeanDefinition) d;
				//LOGGER.debug("#findExistingBeans BeanDefinition def: {}, class source: {}", def,  def.getBeanClassName());
				Class<?> beanType = ClassUtils.getClass(def.getBeanClassName());

				// if model controller
				if (beanType.isAnnotationPresent(ModelController.class) || isOfType(def, AbstractModelServiceBackedController.class)) {

					Class<?> modelType = getaControllerModelType(beanType);

					ModelContext modelContext = entityModelContextsMap.get(modelType);
					if (modelContext != null) {
						modelContext.setControllerDefinition(def);
					}
					else {
						throw new RuntimeException("Invalid model type " + modelType.getCanonicalName() + " specified for controller class " + beanType.getCanonicalName() + "");
					}
				}
                /*
                if (isOfType(def, AbstractPersistableModelController.class)) {
                    Class<?> entity = GenericTypeResolver.resolveTypeArguments(
                            beanType, AbstractPersistableModelController.class)[0];

                    ModelContext modelContext = entityModelContextsMap.get(entity);
                    if (modelContext != null) {
                        modelContext.setControllerDefinition(def);
                        LOGGER.debug("findExistingBeans, found existing model controller class: {}, mapping: {}", beanType.getCanonicalName(), beanType.getAnnotation(RequestMapping.class));
                    }
                }
                */
				// if service
				else if (isOfType(def, AbstractPersistableModelServiceImpl.class)) {
					Class<?> entity = GenericTypeResolver
							.resolveTypeArguments(ClassUtils.getClass(def.getBeanClassName()), PersistableModelService.class)[0];
					ModelContext modelContext = entityModelContextsMap.get(entity);
					if (modelContext != null) {
						modelContext.setServiceDefinition(def);
					}
				}
				// if repository
				else if (isOfType(def, ModelRepositoryFactoryBean.class) || isOfType(def, JpaRepository.class)) {
					log.debug("#findExistingBeans, found bean: {}", beanDefName);
					Class<?> entity = null;
					Class<?> repoInterface = null;
					Object o = def.getPropertyValues().get("repositoryInterface");
					// spring-data-jpa 1.11.1.RELEASE does not provide a "repositoryInterface" property
					// since a constructor value is used
					if (o == null) {
						ConstructorArgumentValues.ValueHolder holder = def.getConstructorArgumentValues().getArgumentValue(0, JpaRepository.class);
						o = holder.getValue();
					}

					if (o instanceof String) {
						repoInterface = ClassUtils.getClass(o.toString());
					}
					else if (o instanceof Class) {
						repoInterface = (Class<?>) o;
					}

					// figure out entity type
					if (repoInterface != null && ModelRepository.class.isAssignableFrom(repoInterface)) {
						Class<?>[] resolveTypeArguments = GenericTypeResolver.resolveTypeArguments(repoInterface,
								ModelRepository.class);
						log.debug("#findExistingBeans repository bean: {}, is assignable, arguments: {}", repoInterface, resolveTypeArguments);
						entity = resolveTypeArguments[0];
						if (entity != null) {
							ModelContext modelContext = entityModelContextsMap.get(entity);
							if (modelContext != null) {
								log.debug("#findExistingBeans repository bean: {}, added to modelContext", repoInterface);
								modelContext.setRepositoryDefinition(def);
								modelContext.setRepositoryType(repoInterface);
								log.debug("#findExistingBeans modelContext repository definition: {}, type: {}", modelContext.getRepositoryDefinition(), modelContext.getRepositoryType());

							}
						}
						else {
							throw new IllegalStateException("Unknown repository interface type encountered: '" + beanDefName + "' bean definition: " + def);
						}

					}
					else {
						log.warn("Unknown repository interface for bean source: '{}', definition: {}", beanDefName, def);
					}

				}

			}

		}
	}

	private Class<?> getaControllerModelType(Class<?> controllerType) {
		Class<?> modelType = null;
		boolean isModelController = controllerType.isAnnotationPresent(ModelController.class);
		boolean isControllerSubclass = AbstractModelServiceBackedController.class.isAssignableFrom(controllerType);
		if (isControllerSubclass) {
			modelType = GenericTypeResolver.resolveTypeArguments(
					controllerType, AbstractModelServiceBackedController.class)[0];
		}
		else if (isModelController) {
			ModelController ann = controllerType.getAnnotation(ModelController.class);
			modelType = ann.modelType();
			if (Object.class.equals(modelType)) {
				throw new RuntimeException("Cannot determine model type for controller class " + controllerType.getCanonicalName() + " as ns class is not a recognized subclass and no modelType was specified in the annotation");
			}
		}
		return modelType;
	}

	/**
	 * Checks if the given BeanDefinition extends/impleents the given target
	 * type
	 *
	 * @param beanDef
	 * @param targetType
	 * @return
	 */
	protected boolean isOfType(BeanDefinition beanDef, Class<?> targetType) {
		if (beanDef.getBeanClassName() != null) {
			Class<?> beanClass = ClassUtils.getClass(beanDef.getBeanClassName());
			return targetType.isAssignableFrom(beanClass);
		}
		return false;
	}

}
