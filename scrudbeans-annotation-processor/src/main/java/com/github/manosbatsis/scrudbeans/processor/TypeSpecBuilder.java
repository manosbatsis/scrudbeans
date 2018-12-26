package com.github.manosbatsis.scrudbeans.processor;

import static com.github.manosbatsis.scrudbeans.api.util.Mimes.APPLICATIOM_JSON_VALUE;
import static com.github.manosbatsis.scrudbeans.api.util.Mimes.APPLICATION_VND_API_PLUS_JSON_VALUE;
import static com.github.manosbatsis.scrudbeans.api.util.Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE;

import java.util.Objects;

import javax.lang.model.element.Modifier;
import javax.persistence.Entity;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.mdd.model.EntityModelDescriptor;
import com.github.manosbatsis.scrudbeans.api.mdd.model.ScrudModelDescriptor;
import com.github.manosbatsis.scrudbeans.api.mdd.repository.ModelRepository;
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService;
import com.github.manosbatsis.scrudbeans.api.mdd.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.common.util.ScrudStringUtils;
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractModelServiceBackedController;
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractPersistableModelController;
import com.github.manosbatsis.scrudbeans.jpa.service.AbstractModelServiceImpl;
import com.github.manosbatsis.scrudbeans.jpa.service.AbstractPersistableModelServiceImpl;
import com.github.manosbatsis.scrudbeans.jpa.specification.factory.AnyToOnePredicateFactory;
import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeSpec;
import io.swagger.annotations.Api;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.atteo.evo.inflector.English;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Utility methods creating {@link TypeSpec} instances for target SCRUD component types
 */
class TypeSpecBuilder {

	private static final Logger log = LoggerFactory.getLogger(TypeSpecBuilder.class);

	private static final String MIMES_PRODUCED =
			APPLICATIOM_JSON_VALUE + ", " +
					MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE + ", " +
					APPLICATION_VND_API_PLUS_JSON_VALUE;
	/**
	 * Create a subclass {@link TypeSpec} of {@link AbstractPersistableModelController}
	 * or {@link AbstractModelServiceBackedController} depending on whether 
	 * the mndel is an {@link Entity}
	 * or {@link AbstractModelServiceImpl}
	 *
	 * @param descriptor The target model descriptor 
	 * @return the resulting type spec
	 */
	static TypeSpec createController(ScrudModelDescriptor descriptor) {
		String className = descriptor.getSimpleName() + "Controller";
		String beanName = ScrudStringUtils.withFirstCharToLowercase(className);
		// Get controller superclass from annotation if exists, fallback to defaults otherwise
		Class controllerSuperClass;
		String controllerSuperClassName = descriptor.getScrudBean().controllerSuperClass();
		String apiName = descriptor.getScrudBean().apiName();
		if (StringUtils.isBlank(apiName)) {
			// To plural, de-camelcase
			apiName = ScrudStringUtils.decamelize(English.plural(descriptor.getSimpleName()));
		}
		String apiDescription = descriptor.getScrudBean().apiDescription();
		if (StringUtils.isBlank(apiDescription)) {
			apiDescription = "Search or manage " +
					ScrudStringUtils.decamelize(descriptor.getSimpleName()) + " entries";
		}
		if (StringUtils.isBlank(controllerSuperClassName)) {
			controllerSuperClass = descriptor.getJpaEntity() ? AbstractPersistableModelController.class : AbstractModelServiceBackedController.class;
		}
		else {
			try {
				controllerSuperClass = ClassUtils.getClass(controllerSuperClassName);
			}
			catch (ClassNotFoundException e) {
				throw new RuntimeException(
						"Could not obtain controllerSuperClass for ScrudBean " + descriptor.getQualifiedName(), e);
			}
		}
		return TypeSpec.classBuilder(className)
				.addAnnotation(
						AnnotationSpec.builder(RestController.class)
								.addMember("value", "\"" + beanName + "\"").build())
				.addAnnotation(
						AnnotationSpec.builder(Api.class)
								.addMember("tags", "\"" + apiName + "\"")
								.addMember("description", "\"" + apiDescription + "\"")
								.addMember("produces", "\"" + MIMES_PRODUCED + "\"").build())
				.addAnnotation(
						AnnotationSpec.builder(RequestMapping.class)
								.addMember("value", getRequestMappingPattern(descriptor)).build())
				.addAnnotation(
						AnnotationSpec.builder(ExposesResourceFor.class)
								.addMember("value", descriptor.getSimpleName() + ".class").build())
				.superclass(
						ParameterizedTypeName.get(
								ClassName.get(controllerSuperClass),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType()),
								ClassName.get(descriptor.getParentPackageName() + ".service", descriptor.getSimpleName() + "Service")))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Create a sub-interface {@link TypeSpec} of {@link PersistableModelService}
	 * or {@link ModelService} depending on whether
	 * the mndel is an {@link Entity}
	 *
	 * @param descriptor The target model descriptor
	 * @return the resulting type spec
	 */
	static TypeSpec createServiceInterface(ScrudModelDescriptor descriptor) {
		String className = descriptor.getSimpleName() + "Service";
		return TypeSpec.interfaceBuilder(className)
				.addSuperinterface(
						ParameterizedTypeName.get(
								ClassName.get(descriptor.getJpaEntity() ? PersistableModelService.class : ModelService.class),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType())))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Create a subclass {@link TypeSpec} of {@link AbstractPersistableModelServiceImpl} or
	 * or {@link AbstractModelServiceImpl}depending on whether 
	 * the mndel is an {@link Entity}
	 *
	 * @param descriptor The target model descriptor 
	 * @return the resulting type spec
	 */
	static TypeSpec createServiceImpl(ScrudModelDescriptor descriptor) {
		String className = descriptor.getSimpleName() + "ServiceImpl";
		String interfaceClassName = descriptor.getSimpleName() + "Service";
		String beanName = "\"" + Character.toLowerCase(interfaceClassName.charAt(0)) + interfaceClassName.substring(1) + "\"";
		return TypeSpec.classBuilder(className)
				.addAnnotation(
						AnnotationSpec.builder(Service.class)
								.addMember("value", beanName).build())
				.superclass(
						ParameterizedTypeName.get(
								ClassName.get(descriptor.getJpaEntity() ? AbstractPersistableModelServiceImpl.class : AbstractModelServiceImpl.class),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType()),
								ClassName.get(descriptor.getParentPackageName() + ".repository", descriptor.getSimpleName() + "Repository")))
				.addSuperinterface(ClassName.get(descriptor.getParentPackageName() + ".service", interfaceClassName))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Create a sub-interface {@link TypeSpec} of {@link ModelRepository}
	 *
	 * @param descriptor The target model descriptor 
	 * @return the resulting type spec
	 */
	static TypeSpec createRepository(ScrudModelDescriptor descriptor) {
		String className = descriptor.getSimpleName() + "Repository";
		return TypeSpec.interfaceBuilder(className)
				.addAnnotation(Repository.class)
				.addSuperinterface(
						ParameterizedTypeName.get(
								ClassName.get(ModelRepository.class),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType())))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Create a subclass or {@link AnyToOnePredicateFactory} for the target {@link Entity} model
	 *
	 * @param descriptor The target model descriptor 
	 * @return the resulting type spec
	 */
	static TypeSpec createPredicateFactory(EntityModelDescriptor descriptor) {
		String className = "AnyToOne" + descriptor.getSimpleName() + "PredicateFactory";
		log.debug("createPredicateFactory, id: {}", descriptor.getIdType());
		//AnyToOnePredicateFactory
		return TypeSpec.classBuilder(className)
				.addAnnotation(
						AnnotationSpec.builder(EntityPredicateFactory.class)
								.addMember("entityClass", "\"" + descriptor.getQualifiedName() + "\"").build())
				.superclass(
						ParameterizedTypeName.get(
								ClassName.get(AnyToOnePredicateFactory.class),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType())))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Generate a {@link RequestMapping} pattern for an {@link Entity}-specific  SCRUD REST controller
	 * @param descriptor The target model descriptor 
	 * @return the resulting pattern string
	 */
	private static String getRequestMappingPattern(ScrudModelDescriptor descriptor) {
		// Get a reference to the annotation
		ScrudBean scrudBean = descriptor.getScrudBean();
		// Construct the endpoint end URL fragment
		String modelUriComponent = Objects.nonNull(scrudBean) ? scrudBean.pathFragment() : null;
		if (StringUtils.isBlank(modelUriComponent)) {
			// To plural and with 1st char to low case
			modelUriComponent = English.plural(
					ScrudStringUtils.withFirstCharToLowercase(descriptor.getSimpleName()));
		}
		// Construct the base API path
		String modelBasePath = StringUtils.isNotBlank(scrudBean.basePath()) ? scrudBean.basePath() : "api/rest";
		// Construct the complete endpoint URL
		String pattern = "\"/" + modelBasePath + "/" + scrudBean.parentPath() + "/" + modelUriComponent + "\"";
		return pattern.replaceAll("/{2,}", "/");
	}
}
