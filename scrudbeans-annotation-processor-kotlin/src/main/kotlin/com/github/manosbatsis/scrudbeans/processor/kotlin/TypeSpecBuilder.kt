package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.kotlin.utils.kapt.processor.SimpleAnnotatedElementInfo
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.IdentifierAdapterBean
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.api.mdd.service.ModelService
import com.github.manosbatsis.scrudbeans.api.util.Mimes.*
import com.github.manosbatsis.scrudbeans.controller.AbstractModelServiceBackedController
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.ModelDescriptor
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.ScrudModelDescriptor
import com.github.manosbatsis.scrudbeans.processor.kotlin.strategy.ScrudBeansDtoStrategy
import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService
import com.github.manosbatsis.scrudbeans.util.ClassUtils
import com.github.manosbatsis.scrudbeans.util.ScrudStringUtils
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.KModifier.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import org.apache.commons.lang3.StringUtils
import org.atteo.evo.inflector.English
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Repository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.io.File
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.VariableElement
import javax.persistence.*

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
        val CLASSNAME_KEY_IDADAPTER = "idadapter"
        val componentSuperClassnames: Map<String, String> = mapOf(
                // Default repos
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_REPOSITORY to ModelRepository::class.java.canonicalName,
                // Default service interface per stack
                CLASSNAME_KEY_SERVICE_INTERFACE to JpaPersistableModelService::class.java.canonicalName,
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_SERVICE_INTERFACE to JpaPersistableModelService::class.java.canonicalName,
                // Default service interface per stack
                CLASSNAME_KEY_SERVICE_IMPL to AbstractJpaPersistableModelServiceImpl::class.java.canonicalName,
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_SERVICE_IMPL to AbstractJpaPersistableModelServiceImpl::class.java.canonicalName,
                // Default service controller per stack
                CLASSNAME_KEY_CONTROLLER to AbstractModelServiceBackedController::class.java.canonicalName,
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_CONTROLLER to AbstractModelServiceBackedController::class.java.canonicalName,
                // Default ID accessor
                ModelDescriptor.STACK_JPA + CLASSNAME_KEY_IDADAPTER to IdentifierAdapter::class.java.canonicalName
        )
    }

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
                /*.addAnnotation(
                        AnnotationSpec.builder(Tag::class.java)
                                .addMember("name=%S", apiName)
                                .addMember("description=%S", apiDescription).build())

                 */
                .addAnnotation(
                        AnnotationSpec.builder(RequestMapping::class.java)
                                . addMember("value = [%S]", getRequestMappingPattern(descriptor)).build())
                //.addAnnotation(
                //        AnnotationSpec.builder(ExposesResourceFor::class.java)
                //                . addMember("value = %L", descriptor.simpleName + "::class").build())
                .superclass(
                        ClassName(pkgAndName.left, pkgAndName.right).parameterizedBy(
                                ClassName(descriptor.packageName, descriptor.simpleName),
                                descriptor.idClassName,
                                ClassName(descriptor.parentPackageName + ".service", descriptor.simpleName + "Service"),
                                ClassName(descriptor.packageName, descriptor.simpleName + "Dto")))
                .addModifiers(KModifier.PUBLIC)
                .build()
    }


    /**
     * Create an implementation of [IdentifierAdapter]
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createIdAdapter(modelClassName: ClassName, descriptor: ScrudModelDescriptor): TypeSpec {
        val className: String = modelClassName.simpleName + "IdentifierAdapter"
        val pkgAndName = ClassUtils.getPackageAndSimpleName(
                getSuperclassName(descriptor, TypeSpecBuilder.CLASSNAME_KEY_IDADAPTER))
        return TypeSpec.objectBuilder(className)
                .addAnnotation(AnnotationSpec.builder(IdentifierAdapterBean::class.java)
                        .addMember("className = %S", modelClassName)
                        .build())
                .addModifiers(PUBLIC)
                .addSuperinterface(
                        ClassName(pkgAndName.left, pkgAndName.right)
                                .parameterizedBy(modelClassName, descriptor.idClassName)).addFunction(FunSpec.builder("getIdName")
                        .addModifiers(PUBLIC, OVERRIDE)
                        .returns(String::class)
                        .addParameter(ParameterSpec.builder("resource", modelClassName).build())
                        .addStatement("return %S", descriptor.idName)
                        .build())
                .addFunction(FunSpec.builder("readId")
                        .addModifiers(PUBLIC, OVERRIDE)
                        .returns(descriptor.idClassName.copy(nullable = true))
                        .addParameter(ParameterSpec.builder("resource", modelClassName).build())
                        .addStatement("return resource.%L", descriptor.idName)
                        .build())
                .build()
    }

    /**
     * Create a sub-interface [TypeSpec] of [JpaPersistableModelService]
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
     * Create a subclass [TypeSpec] of [AbstractJpaPersistableModelServiceImpl] or
     * or [AbstractModelServiceImpl]depending on whether
     * the mndel is an [Entity]
     *
     * @param descriptor The target model descriptor
     * @return the resulting type spec
     */
    fun createServiceImpl(descriptor: ScrudModelDescriptor): TypeSpec {
        val className: String = descriptor.simpleName + "ServiceImpl"
        val interfaceClassName: String = descriptor.simpleName + "Service"
        val entityType = ClassName(descriptor.packageName, descriptor.simpleName)
        val repositoryType = ClassName(descriptor.parentPackageName + ".repository", descriptor.simpleName + "Repository")
        val superClassPackageAndName = ClassUtils.getPackageAndSimpleName(getSuperclassName(descriptor, CLASSNAME_KEY_SERVICE_IMPL))
        val superClassName = ClassName(superClassPackageAndName.left, superClassPackageAndName.right).parameterizedBy(
            entityType, descriptor.idClassName, repositoryType)
        val identifierAdapterClassName = ClassName(descriptor.packageName, "${descriptor.simpleName}IdentifierAdapter")
        return TypeSpec.classBuilder(className)
                .addAnnotation(
                        AnnotationSpec.builder(Service::class.java)
                                .addMember("value = %S", interfaceClassName.decapitalize()).build())
            .also {
                if(!descriptor.scrudBean.transactionManager.isNullOrEmpty())
                    it.addAnnotation(
                        AnnotationSpec.builder(Transactional::class.java)
                            .addMember("value = %S", descriptor.scrudBean.transactionManager)
                            .addMember("readOnly = true")
                            .build())
            }
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter("repository", repositoryType)
                .addParameter("entityManager", EntityManager::class.java)
                .also {
                    if(!descriptor.scrudBean.transactionManager.isNullOrEmpty())
                        it.addAnnotation(
                            AnnotationSpec.builder(Qualifier::class.java)
                                .addMember("value = %S", descriptor.scrudBean.transactionManager).build())
                }
                .build())
            .superclass(superClassName)
            .addSuperinterface(ClassName(descriptor.parentPackageName + ".service", interfaceClassName))
            .addModifiers(PUBLIC, OPEN)
            .addSuperclassConstructorParameter("repository")
            .addSuperclassConstructorParameter("%T::class.java", entityType)
            .addSuperclassConstructorParameter("%T::class.java", descriptor.idClassName)
            .addSuperclassConstructorParameter("entityManager")
            .addProperty(PropertySpec.builder(
                "identifierAdapter", identifierAdapterClassName, OVERRIDE)
                .initializer("%T", identifierAdapterClassName)
                .build())
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

    fun isNonUpdatableField(
        stateInfo: ScrudModelDescriptor,
        stateField: VariableElement
    ): Boolean{
        val typeElementHierarchy = stateInfo.typeElement.getTypeElementHierarchy()
        return typeElementHierarchy.find { currentTypeElement ->
            processingEnvironment.elementUtils
                .getAllMembers(currentTypeElement)
                .find {
                    it.simpleName == stateField.simpleName
                            && (it.hasAnnotation(Id::class.java)
                                || it.hasAnnotation(EmbeddedId::class.java)
                                || (it.hasAnnotation(Column::class.java)
                                        && it.getAnnotationMirror(Column::class.java)
                                            .findAnnotationValue("updatable")?.value == false))
                } != null
        } != null
    }
    fun getNonUpdatableFields(
        stateInfo: ScrudModelDescriptor,
        stateFields: List<VariableElement>
    ): List<String>{
        return stateFields
            .filter { isNonUpdatableField(stateInfo, it) }
            .map { it.simpleName.toString() }
    }

    /** Create a DTO for the given model */
    fun dtoSpecBuilder(stateInfo: ScrudModelDescriptor, sourceRootFile: File): TypeSpec {
        val primaryTargetTypeElementFields = stateInfo.typeElement.accessibleConstructorParameterFields()
        val elementInfo = SimpleAnnotatedElementInfo(
                processingEnvironment = processingEnvironment,
                primaryTargetTypeElement = stateInfo.typeElement,
                primaryTargetTypeElementFields = primaryTargetTypeElementFields,
                annotation = stateInfo.typeElement.getAnnotationMirror(ScrudBean::class.java),
                ignoreProperties = emptyList(),
                nonUpdatableProperties = getNonUpdatableFields(stateInfo, primaryTargetTypeElementFields),
                copyAnnotationPackages = listOf("io.swagger.v3.oas.annotations", "com.fasterxml.jackson.annotation"),
                sourceRoot = sourceRootFile,
                generatedPackageName = stateInfo.packageName,
                mixinTypeElement = null,
                mixinTypeElementFields = emptyList(),
                mixinTypeElementSimpleName = null,
                secondaryTargetTypeElement = null,
                secondaryTargetTypeElementFields = emptyList(),
                secondaryTargetTypeElementSimpleName = null,
                isNonDataClass = true
        )
        val dtoStrategy = ScrudBeansDtoStrategy(elementInfo)
        return dtoStrategy.dtoTypeSpec()
        /*
        return dtoSpec(DtoInputContext(
                processingEnvironment = processingEnvironment,
                originalTypeElement = stateInfo.typeElement,
                fields = stateInfo.typeElement.accessibleConstructorParameterFields(),
                //stateInfo.packageName,
                copyAnnotationPackages = listOf("io.swagger.v3.oas.annotations", "com.fasterxml.jackson.annotation")))

         */
    }

    /** Create a mapstruct-based mapper for non-generated DTOs
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
    }*/

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
