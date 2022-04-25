package com.github.manosbatsis.scrudbeans.processor.java;

import com.github.manosbatsis.scrudbeans.api.DtoMapper;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.IdentifierAdapterBean;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService;
import com.github.manosbatsis.scrudbeans.controller.AbstractModelServiceBackedController;
import com.github.manosbatsis.scrudbeans.controller.AbstractPersistableModelController;
import com.github.manosbatsis.scrudbeans.processor.java.descriptor.EntityModelDescriptor;
import com.github.manosbatsis.scrudbeans.processor.java.descriptor.ModelDescriptor;
import com.github.manosbatsis.scrudbeans.processor.java.descriptor.ScrudModelDescriptor;
import com.github.manosbatsis.scrudbeans.repository.ModelRepository;
import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl;
import com.github.manosbatsis.scrudbeans.service.AbstractModelServiceImpl;
import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService;
import com.github.manosbatsis.scrudbeans.specification.factory.AnyToOnePredicateFactory;
import com.github.manosbatsis.scrudbeans.util.ClassUtils;
import com.github.manosbatsis.scrudbeans.util.ScrudStringUtils;
import com.squareup.javapoet.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.atteo.evo.inflector.English;
import org.mapstruct.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.lang.model.element.Modifier;
import javax.persistence.Entity;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.github.manosbatsis.scrudbeans.api.util.Mimes.*;

/**
 * Utility methods creating {@link TypeSpec} instances for target SCRUD component types
 */
class TypeSpecBuilder {

    private static final Logger log = LoggerFactory.getLogger(TypeSpecBuilder.class);

    private static final String MIMES_PRODUCED =
            APPLICATIOM_JSON_VALUE + ", " +
                    MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE + ", " +
                    APPLICATION_VND_API_PLUS_JSON_VALUE;

    public static final String CLASSNAME_KEY_REPOSITORY = "repository";

    public static final String CLASSNAME_KEY_SERVICE_INTERFACE = "service";

    public static final String CLASSNAME_KEY_SERVICE_IMPL = "serviceImpl";

    public static final String CLASSNAME_KEY_CONTROLLER = "controller";
    public static final String CLASSNAME_KEY_IDADAPTER = "idadapter";

    protected static Map<String, String> componentSuperClassnames = new HashMap<>();

    static {
        // Default repos
        componentSuperClassnames.put(ModelDescriptor.STACK_JPA + CLASSNAME_KEY_REPOSITORY, ModelRepository.class.getCanonicalName());
        // Default service interface per stack
        componentSuperClassnames.put(CLASSNAME_KEY_SERVICE_INTERFACE, ModelService.class.getCanonicalName());
        componentSuperClassnames.put(ModelDescriptor.STACK_JPA + CLASSNAME_KEY_SERVICE_INTERFACE, JpaPersistableModelService.class.getCanonicalName());
        // Default service interface per stack
        componentSuperClassnames.put(CLASSNAME_KEY_SERVICE_IMPL, AbstractModelServiceImpl.class.getCanonicalName());
        componentSuperClassnames.put(ModelDescriptor.STACK_JPA + CLASSNAME_KEY_SERVICE_IMPL, AbstractJpaPersistableModelServiceImpl.class.getCanonicalName());
        // Default service controller per stack
        componentSuperClassnames.put(CLASSNAME_KEY_CONTROLLER, AbstractModelServiceBackedController.class.getCanonicalName());
        componentSuperClassnames.put(ModelDescriptor.STACK_JPA + CLASSNAME_KEY_CONTROLLER, AbstractPersistableModelController.class.getCanonicalName());
        // Default ID accessor
        componentSuperClassnames.put(ModelDescriptor.STACK_JPA + CLASSNAME_KEY_IDADAPTER, IdentifierAdapter.class.getCanonicalName());
    }
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

		// Content for Swagger annotations
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

		// Controller superclass package/simple name: get from ScrudBean annotation if exists, fallback to defaults otherwise
		String controllerSuperClassName = descriptor.getScrudBean().controllerSuperClass();
		if (StringUtils.isBlank(controllerSuperClassName)) {
			controllerSuperClassName = getSuperclassName(descriptor, CLASSNAME_KEY_CONTROLLER);
		}
		Pair<String, String> pkgAndName = ClassUtils.getPackageAndSimpleName(controllerSuperClassName);


		return TypeSpec.classBuilder(className)
				.addAnnotation(
						AnnotationSpec.builder(RestController.class)
								.addMember("value", "\"" + beanName + "\"").build())
				.addAnnotation(
                        AnnotationSpec.builder(Tag.class)
								.addMember("name", "\"" + apiName + "\"")
								.addMember("description", "\"" + apiDescription + "\"")
                                .build())
				.addAnnotation(
						AnnotationSpec.builder(RequestMapping.class)
								.addMember("value", getRequestMappingPattern(descriptor)).build())
				/*.addAnnotation(
						AnnotationSpec.builder(ExposesResourceFor.class)
								.addMember("value", descriptor.getSimpleName() + ".class").build())

				 */
				.superclass(
						ParameterizedTypeName.get(
								ClassName.get(pkgAndName.getLeft(), pkgAndName.getRight()),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType()),
								ClassName.get(descriptor.getParentPackageName() + ".service", descriptor.getSimpleName() + "Service")))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Create a sub-interface {@link TypeSpec} of {@link JpaPersistableModelService}
	 * or {@link ModelService} depending on whether
	 * the mndel is an {@link Entity}
	 *
	 * @param descriptor The target model descriptor
	 * @return the resulting type spec
	 */
	static TypeSpec createServiceInterface(ScrudModelDescriptor descriptor) {
		String className = descriptor.getSimpleName() + "Service";
		Pair<String, String> pkgAndName = ClassUtils.getPackageAndSimpleName(
				getSuperclassName(descriptor, CLASSNAME_KEY_SERVICE_INTERFACE));
		return TypeSpec.interfaceBuilder(className)
				.addSuperinterface(
						ParameterizedTypeName.get(
								ClassName.get(pkgAndName.getLeft(), pkgAndName.getRight()),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.bestGuess(descriptor.getIdType())))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Create a subclass {@link TypeSpec} of {@link AbstractJpaPersistableModelServiceImpl} or
	 * or {@link AbstractModelServiceImpl}depending on whether
	 * the mndel is an {@link Entity}
	 *
	 * @param descriptor The target model descriptor
	 * @return the resulting type spec
	 */
	static TypeSpec createServiceImpl(ScrudModelDescriptor descriptor) {
		String className = descriptor.getSimpleName() + "ServiceImpl";
		String interfaceClassName = descriptor.getSimpleName() + "Service";
		String beanName = "\"" + Character.toLowerCase(interfaceClassName.charAt(0)) +
				interfaceClassName.substring(1) + "\"";
		Pair<String, String> pkgAndName = ClassUtils.getPackageAndSimpleName(
				getSuperclassName(descriptor, CLASSNAME_KEY_SERVICE_IMPL));
		return TypeSpec.classBuilder(className)
				.addAnnotation(
						AnnotationSpec.builder(Service.class)
								.addMember("value", beanName).build())
				.superclass(
						ParameterizedTypeName.get(
								ClassName.get(pkgAndName.getLeft(), pkgAndName.getRight()),
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
		Pair<String, String> pkgAndName = ClassUtils.getPackageAndSimpleName(
				getSuperclassName(descriptor, CLASSNAME_KEY_REPOSITORY));
		return TypeSpec.interfaceBuilder(className)
                .addAnnotation(Repository.class)
                .addSuperinterface(
                        ParameterizedTypeName.get(
                                ClassName.get(pkgAndName.getLeft(), pkgAndName.getRight()),
                                ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
                                ClassName.bestGuess(descriptor.getIdType())))
                .addModifiers(Modifier.PUBLIC)
                .build();
    }

    /**
     * Create an implementation of {@link IdentifierAdapter}
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    static TypeSpec createIdAccessor(ScrudModelDescriptor descriptor) {
        String className = descriptor.getSimpleName() + "IdentifierAdapterBean";
        Pair<String, String> pkgAndName = ClassUtils.getPackageAndSimpleName(
                getSuperclassName(descriptor, CLASSNAME_KEY_IDADAPTER));
        ClassName modelClassName = ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName());
        ClassName idClassName = ClassName.bestGuess(descriptor.getIdType());
        return TypeSpec.classBuilder(className)
                .addSuperinterface(
                        ParameterizedTypeName.get(
                                ClassName.get(pkgAndName.getLeft(), pkgAndName.getRight()),
                                modelClassName,
                                idClassName))
                .addAnnotation(AnnotationSpec.builder(IdentifierAdapterBean.class)
                        .addMember("className", "$S", modelClassName.toString())
                        .build())
                .addModifiers(Modifier.PUBLIC)
                .addMethod(MethodSpec.methodBuilder("getIdName")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .returns(String.class)
                        .addParameter(ParameterSpec.builder(modelClassName, "resource", Modifier.FINAL).build())
                        .addStatement("return $S", descriptor.getIdName())
                        .build())
                .addMethod(MethodSpec.methodBuilder("readId")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override.class)
                        .returns(idClassName)
                        .addParameter(ParameterSpec.builder(modelClassName, "resource", Modifier.FINAL).build())
                        .addStatement("return resource.$L()", toGetterName(descriptor.getIdName()))
                        .build())
                .build();
    }

    private static String toGetterName(String fieldName) {
        return toAccessorMethodName(fieldName, "get");
    }

    private static String toSetterName(String fieldName) {
        return toAccessorMethodName(fieldName, "set");
    }

    private static String toAccessorMethodName(String fieldName, String prefix) {
        if (fieldName.startsWith(prefix)) return fieldName;
        else return prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
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

	public static TypeSpec createDtoMapper(ScrudModelDescriptor descriptor, String dtoClass) {
		String dtoSimpleName = dtoClass.substring(dtoClass.lastIndexOf('.') + 1);
		String dtoPackage = dtoClass.substring(0, dtoClass.lastIndexOf('.'));
		log.debug("createDtoMapper, dtoPackage: {}, dtoSimpleName: {}", dtoPackage, dtoSimpleName);
		String className = descriptor.getSimpleName() + "To" + dtoSimpleName + "Mapper";
		log.debug("createDtoMapper, className: {}", className);

		return TypeSpec.interfaceBuilder(className)
				//@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
				.addAnnotation(
						AnnotationSpec.builder(Mapper.class)
								.addMember("unmappedTargetPolicy", "org.mapstruct.ReportingPolicy.IGNORE")
								.addMember("componentModel", "\"spring\"").build())
				// extends DtoMapper<EntityClass, DTOClass>
				.addSuperinterface(
						ParameterizedTypeName.get(
								ClassName.get(DtoMapper.class),
								ClassName.get(descriptor.getPackageName(), descriptor.getSimpleName()),
								ClassName.get(dtoPackage, dtoSimpleName)))
				.addModifiers(Modifier.PUBLIC)
				.build();
	}

	/**
	 * Get the superclass type for the given component type.
	 * @param descriptor the model descriptor
	 * @param componentTypeKey the component type key, i.e. one of {@link TypeSpecBuilder#CLASSNAME_KEY_REPOSITORY}, {@link TypeSpecBuilder#CLASSNAME_KEY_SERVICE_INTERFACE}, {@link TypeSpecBuilder#CLASSNAME_KEY_SERVICE_IMPL}, {@link TypeSpecBuilder#CLASSNAME_KEY_CONTROLLER}
	 */
	public static String getSuperclassName(ScrudModelDescriptor descriptor, String componentTypeKey) {
		return getSuperclassName(descriptor, componentTypeKey, null);
	}

	private static String getSuperclassName(
			@NonNull ScrudModelDescriptor descriptor, @NonNull String componentTypeKey, String defaultClassname) {
		// Get a default by stack if missing
		if (Objects.isNull(defaultClassname)) defaultClassname =
				componentSuperClassnames.get(descriptor.getStack() + componentTypeKey);
		// Return the superclass name if configured, the default otherwise
		return descriptor.getConfigProperties()
				.getProperty("scrudbeans.processor." + descriptor.getStack() + "." + componentTypeKey, defaultClassname);
	}

}
