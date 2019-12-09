package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.kotlinpoet.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.scrudbeans.api.DtoMapper
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService
import com.github.manosbatsis.scrudbeans.api.util.Mimes.APPLICATIOM_JSON_VALUE
import com.github.manosbatsis.scrudbeans.api.util.Mimes.APPLICATION_VND_API_PLUS_JSON_VALUE
import com.github.manosbatsis.scrudbeans.api.util.Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE
import com.github.manosbatsis.scrudbeans.common.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.common.service.PersistableModelService
import com.github.manosbatsis.scrudbeans.common.util.ClassUtils
import com.github.manosbatsis.scrudbeans.common.util.ScrudStringUtils
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractModelServiceBackedController
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractPersistableModelController
import com.github.manosbatsis.scrudbeans.jpa.service.AbstractModelServiceImpl
import com.github.manosbatsis.scrudbeans.jpa.service.AbstractPersistableModelServiceImpl
import com.github.manosbatsis.scrudbeans.jpa.specification.factory.AnyToOnePredicateFactory
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.EntityModelDescriptor
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.ModelDescriptor
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.ScrudModelDescriptor
import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeSpec
import io.swagger.annotations.Api
import org.apache.commons.lang3.StringUtils
import org.atteo.evo.inflector.English
import org.mapstruct.Mapper
import org.slf4j.LoggerFactory
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.Objects
import javax.annotation.processing.ProcessingEnvironment
import javax.persistence.Entity

/**
 * Utility methods creating [TypeSpec] instances for target SCRUD component types
 */
internal class TypeSpecBuilder(
        override val processingEnvironment: ProcessingEnvironment
): ProcessingEnvironmentAware {

    companion object {
        @JvmStatic
        private val log = LoggerFactory.getLogger(TypeSpecBuilder::class.java)
        private val MIMES_PRODUCED = APPLICATIOM_JSON_VALUE + ", " +
                MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE + ", " +
                APPLICATION_VND_API_PLUS_JSON_VALUE
        val CLASSNAME_KEY_REPOSITORY = "repository"
        val CLASSNAME_KEY_SERVICE_INTERFACE = "service"
        val CLASSNAME_KEY_SERVICE_IMPL = "serviceImpl"
        val CLASSNAME_KEY_CONTROLLER = "controller"
        val componentSuperClassnames: Map<String, String> = mapOf(
                // Default repos
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_REPOSITORY to ModelRepository::class.java.canonicalName,
                // Default service interface per stack
                CLASSNAME_KEY_SERVICE_INTERFACE to ModelService::class.java.canonicalName,
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_SERVICE_INTERFACE to PersistableModelService::class.java.canonicalName,
                // Default service interface per stack
                CLASSNAME_KEY_SERVICE_IMPL to AbstractModelServiceImpl::class.java.canonicalName,
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_SERVICE_IMPL to AbstractPersistableModelServiceImpl::class.java.canonicalName,
                // Default service controller per stack
                CLASSNAME_KEY_CONTROLLER to AbstractModelServiceBackedController::class.java.canonicalName,
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_CONTROLLER to AbstractPersistableModelController::class.java.canonicalName
        )
    }

    /**
     * Create a subclass [TypeSpec] of [AbstractPersistableModelController]
     * or [AbstractModelServiceBackedController] depending on whether
     * the mndel is an [Entity]
     * or [AbstractModelServiceImpl]
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createController(descriptor: ScrudModelDescriptor): TypeSpec {
        val className = descriptor.simpleName + "Controller"
        val beanName = ScrudStringUtils.withFirstCharToLowercase(className)

        // Content for Swagger annotations
        var apiName = descriptor.scrudBean.apiName
        if (StringUtils.isBlank(apiName)) {
            // To plural, de-camelcase
            apiName = ScrudStringUtils.decamelize(English.plural(descriptor.simpleName))
        }
        var apiDescription = descriptor.scrudBean.apiDescription
        if (StringUtils.isBlank(apiDescription)) {
            apiDescription = "Search or manage " +
                    ScrudStringUtils.decamelize(descriptor.simpleName) + " entries"
        }

        // Controller superclass package/simple name: get from ScrudBean annotation if exists, fallback to defaults otherwise
        var controllerSuperClassName = descriptor.scrudBean.controllerSuperClass
        if (StringUtils.isBlank(controllerSuperClassName)) {
            controllerSuperClassName = getSuperclassName(descriptor, CLASSNAME_KEY_CONTROLLER)
        }
        val pkgAndName = ClassUtils.getPackageAndSimpleName(controllerSuperClassName)


        return TypeSpec.classBuilder(className)
                .addAnnotation(
                        AnnotationSpec.builder(RestController::class.java)
                                . addMember("value = %S", beanName).build())
                .addAnnotation(
                        AnnotationSpec.builder(Api::class.java)
                                .addMember("tags = [%S]", apiName)
                                .addMember("description = %S", apiDescription)
                                .addMember("produces = %S", MIMES_PRODUCED).build())
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping::class.java)
                                . addMember("value = %S", getRequestMappingPattern(descriptor)).build())
                .addAnnotation(
                        AnnotationSpec.builder(ExposesResourceFor::class.java)
                                . addMember("value = %L", descriptor.simpleName + "::class").build())
                .superclass(
                        ClassName(pkgAndName.left, pkgAndName.right).parameterizedBy(
                                ClassName(descriptor.packageName, descriptor.simpleName),
                                descriptor.idClassName,
                                ClassName(descriptor.parentPackageName + ".service", descriptor.simpleName + "Service")))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }

    /**
     * Create a sub-interface [TypeSpec] of [PersistableModelService]
     * or [ModelService] depending on whether
     * the mndel is an [Entity]
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createServiceInterface(descriptor: ScrudModelDescriptor): TypeSpec {
        val className: String = descriptor.simpleName + "Service"
        val pkgAndName = ClassUtils.getPackageAndSimpleName(
                getSuperclassName(descriptor, TypeSpecBuilder.CLASSNAME_KEY_SERVICE_INTERFACE)!!)
        return TypeSpec.interfaceBuilder(className)
                .addSuperinterface(
                        ClassName(pkgAndName.left, pkgAndName.right).parameterizedBy(
                                ClassName(descriptor.packageName, descriptor.simpleName),
                                descriptor.idClassName))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }

    /**
     * Create a subclass [TypeSpec] of [AbstractPersistableModelServiceImpl] or
     * or [AbstractModelServiceImpl]depending on whether
     * the mndel is an [Entity]
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createServiceImpl(descriptor: ScrudModelDescriptor): TypeSpec {
        val className: String = descriptor.simpleName + "ServiceImpl"
        val interfaceClassName: String = descriptor.simpleName + "Service"
        val pkgAndName = ClassUtils.getPackageAndSimpleName(
                getSuperclassName(descriptor, CLASSNAME_KEY_SERVICE_IMPL)!!)
        return TypeSpec.classBuilder(className)
                .addAnnotation(
                        AnnotationSpec.builder(Service::class.java)
                                .addMember("value = %S", interfaceClassName.decapitalize()).build())
                .superclass(
                        ClassName(pkgAndName.left, pkgAndName.right).parameterizedBy(
                                ClassName(descriptor.packageName, descriptor.simpleName),
                                descriptor.idClassName,
                                ClassName(descriptor.parentPackageName + ".repository", descriptor.simpleName + "Repository")))
                .addSuperinterface(ClassName(descriptor.parentPackageName + ".service", interfaceClassName))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }

    /**
     * Create a sub-interface [TypeSpec] of [ModelRepository]
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createRepository(descriptor: ScrudModelDescriptor): TypeSpec {
        val className: String = descriptor.simpleName + "Repository"
        val pkgAndName = ClassUtils.getPackageAndSimpleName(
                getSuperclassName(descriptor, CLASSNAME_KEY_REPOSITORY)!!)
        return TypeSpec.interfaceBuilder(className)
                .addAnnotation(Repository::class.java)
                .addSuperinterface(
                        ClassName(pkgAndName.left, pkgAndName.right).parameterizedBy(
                                ClassName(descriptor.packageName, descriptor.simpleName),
                                descriptor.idClassName))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }

    /**
     * Create a subclass or [AnyToOnePredicateFactory] for the target [Entity] model
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createPredicateFactory(descriptor: EntityModelDescriptor): TypeSpec {
        val className = "AnyToOne" + descriptor.simpleName + "PredicateFactory"
        return TypeSpec.classBuilder(className)
                .addAnnotation(
                        AnnotationSpec.builder(EntityPredicateFactory::class.java)
                                .addMember("entityClass = %S", descriptor.qualifiedName).build())
                .superclass(
                        ClassName(AnyToOnePredicateFactory::class.java.`package`.name, AnyToOnePredicateFactory::class.java.simpleName)
                                .parameterizedBy(
                                        ClassName(descriptor.packageName, descriptor.simpleName),
                                        descriptor.idClassName))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }

    /**
     * Generate a [RequestMapping] pattern for an [Entity]-specific  SCRUD REST controller
     * @param descriptor The target model descriptor
     * @return the resulting pattern string
     */
    private fun getRequestMappingPattern(descriptor: ScrudModelDescriptor): String { // Get a reference to the annotation
        val scrudBean: ScrudBean = descriptor.scrudBean
        // Construct the endpoint end URL fragment
        var modelUriComponent: String? = if (Objects.nonNull(scrudBean)) scrudBean.pathFragment else null
        if (StringUtils.isBlank(modelUriComponent)) { // To plural and with 1st char to low case
            modelUriComponent = English.plural(
                    ScrudStringUtils.withFirstCharToLowercase(descriptor.simpleName))
        }
        // Construct the base API path
        val modelBasePath = if (StringUtils.isNotBlank(scrudBean.basePath)) scrudBean.basePath else "api/rest"
        // Construct the complete endpoint URL
        val pattern = "/$modelBasePath/${scrudBean.parentPath}/$modelUriComponent"
        return pattern.replace("/{2,}".toRegex(), "/")
    }

    fun createDtoMapper(descriptor: ScrudModelDescriptor, dtoClass: String): TypeSpec {
        log.debug("createDtoMapper, dtoClass: $dtoClass")
        val dtoSimpleName = dtoClass.substring(dtoClass.lastIndexOf('.') + 1)
        val dtoPackage = dtoClass.substring(0, dtoClass.lastIndexOf('.'))
        log.debug("createDtoMapper, dtoPackage: {}, dtoSimpleName: {}", dtoPackage, dtoSimpleName)
        val className: String = descriptor.simpleName + "To" + dtoSimpleName + "Mapper"
        log.debug("createDtoMapper, className: {}", className)
        return TypeSpec.interfaceBuilder(className)
                //@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = "spring")
                .addAnnotation(
                        AnnotationSpec.builder(Mapper::class.java)
                                .addMember("unmappedTargetPolicy = %L", "org.mapstruct.ReportingPolicy.IGNORE")
                                .addMember("componentModel = %S", "spring").build())
                // extends DtoMapper<EntityClass, DTOClass>
                .addSuperinterface(
                        ClassName(DtoMapper::class.java.`package`.name, DtoMapper::class.java.simpleName)
                                .parameterizedBy(
                                        ClassName(descriptor.packageName, descriptor.simpleName),
                                        ClassName(dtoPackage, dtoSimpleName)))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }

    /**
     * Get the superclass type for the given component type.
     * @param descriptor the model descriptor
     * @param componentTypeKey the component type key, i.e. one of [TypeSpecBuilder.CLASSNAME_KEY_REPOSITORY], [TypeSpecBuilder.CLASSNAME_KEY_SERVICE_INTERFACE], [TypeSpecBuilder.CLASSNAME_KEY_SERVICE_IMPL], [TypeSpecBuilder.CLASSNAME_KEY_CONTROLLER]
     */
    fun getSuperclassName(descriptor: ScrudModelDescriptor, componentTypeKey: String): String {
        return getSuperclassName(descriptor, componentTypeKey, null)
    }

    private fun getSuperclassName(
            descriptor: ScrudModelDescriptor, componentTypeKey: String, defaultClassname: String?): String {
        var defaultClassname = defaultClassname
        // Get a default by stack if missing
        if (Objects.isNull(defaultClassname))
            defaultClassname = componentSuperClassnames[descriptor.stack + componentTypeKey]
        // Return the superclass name if configured, the default otherwise
        return descriptor.configProperties
                .getProperty("scrudbeans.processor." + descriptor.stack + "." + componentTypeKey, defaultClassname)
    }

}
