package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.kotlin.utils.kapt.processor.SimpleAnnotatedElementInfo
import com.github.manosbatsis.scrudbeans.api.exception.NotFoundException
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
import org.springframework.context.annotation.Lazy
import org.springframework.core.convert.ConversionService
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.stereotype.Component
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

        val TYPE_PARAMETER_STAR = WildcardTypeName.producerOf(Any::class.asTypeName().copy(nullable = true))
        val ANY_CLASS_TYPENAME =  Class::class.asClassName().parameterizedBy(TYPE_PARAMETER_STAR)
        val CLASSNAME_KEY_REPOSITORY = "repository"
        val CLASSNAME_KEY_SERVICE_INTERFACE = "service"
        val CLASSNAME_KEY_SERVICE_IMPL = "serviceImpl"
        val CLASSNAME_KEY_CONTROLLER = "controller"
        val CLASSNAME_KEY_IDADAPTER = "idadapter"

        val multipleSlashesRegex = "/{2,}".toRegex()
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
                        ClassName(pkgAndName.first, pkgAndName.second).parameterizedBy(
                                ClassName(descriptor.packageName, descriptor.simpleName),
                                descriptor.idClassName,
                                ClassName(descriptor.parentPackageName + ".service", descriptor.simpleName + "Service"),
                                ClassName(descriptor.packageName, descriptor.simpleName + "Dto")))
                .addModifiers(PUBLIC)
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
                getSuperclassName(descriptor, CLASSNAME_KEY_IDADAPTER))
        return TypeSpec.classBuilder(className)
            .addAnnotation(Component::class.java)
            .addAnnotation(AnnotationSpec.builder(IdentifierAdapterBean::class.java)
                .addMember("className = %S", modelClassName)
                .build())
            .addModifiers(PUBLIC)
            .addSuperinterface(
                    ClassName(pkgAndName.first, pkgAndName.second)
                            .parameterizedBy(modelClassName, descriptor.idClassName)
            )
            .primaryConstructor(FunSpec.constructorBuilder()
                .addParameter(ParameterSpec.builder("conversionService", ConversionService::class.java)
                    .addAnnotation(Lazy::class)
                    .build())
                .build())
            .addProperty(
                PropertySpec.builder("conversionService", ConversionService::class)
                    .initializer("conversionService")
                    .addModifiers(KModifier.PRIVATE)
                    .build()
            )
            .addProperty(PropertySpec.builder(
                "entityType", Class::class.asClassName().parameterizedBy(modelClassName), OVERRIDE)
                .initializer("%T::class.java", modelClassName)
                .build())
            .addProperty(PropertySpec.builder(
                "entityIdType", Class::class.asClassName().parameterizedBy(descriptor.idClassName), OVERRIDE)
                .initializer("%T::class.java", descriptor.idClassName)
                .build())
            .addProperty(PropertySpec.builder(
                "isCompositeId", Boolean::class, OVERRIDE)
                .initializer("%L", descriptor.isCompositeId)
                .build())
            .addSuperclassConstructorParameter("%T::class.java", descriptor.idClassName)
            .addFunction(FunSpec.builder("getId")
                .addModifiers(PUBLIC, OVERRIDE)
                .returns(descriptor.idClassName.copy(nullable = true))
                .addParameter(ParameterSpec.builder("resource", Any::class.asClassName().copy(nullable = true)).build())
                .addStatement("if(resource == null) return null")
                .addStatement("if(resource !is %T) throw %T(%S)",
                    descriptor.className,
                    IllegalArgumentException::class,
                    buildCodeBlock {
                        add("Incompatible type "+'$'+"{resource::class.java.canonicalName}, should be %T",
                            descriptor.idClassName)
                    }
                )
                .let { funcSpecBuilder ->
                    if(descriptor.isIdClass){
                        val funcBody = CodeBlock.builder().addStatement("return %T(", descriptor.idClassName)
                        funcBody.indent()
                        descriptor.compositeIdFieldNames.forEachIndexed { index, fieldName ->
                            //val fieldType = descriptor.compositeIdClassNames[fieldName]?: error("No type for composite id field $fieldName")
                            val maybeComma = if(index + 1 == descriptor.compositeIdFieldNames.size) "" else ","
                            funcBody.addStatement("$fieldName = resource.$fieldName$maybeComma")
                        }
                        funcBody.unindent()
                        funcBody.addStatement(")")
                        funcSpecBuilder.addCode(funcBody.build())
                    }
                    else funcSpecBuilder.addStatement("return resource.%L", descriptor.idFieldName)
                    funcSpecBuilder
                }
                .build())

            .addFunction(FunSpec.builder("getIdAsString")
                .addModifiers(PUBLIC, OVERRIDE)
                .returns(String::class.java.asTypeName().asKotlinTypeName().copy(nullable = true))
                .addParameter(ParameterSpec.builder("resource",
                    Any::class.asClassName().copy(nullable = true) /*modelClassName*/).build())
                .addStatement("return convertIdToString(getId(resource))")
                .build())
            .addFunction(FunSpec.builder("buildIdFromString")
                .addModifiers(PUBLIC, OVERRIDE)
                .returns(descriptor.idClassName.copy(nullable = true))
                .addParameter(ParameterSpec.builder(
                    "from",
                    String::class.java.asTypeName().asKotlinTypeName()
                        .copy(nullable = true)
                ).build())
                .let { funcSpecBuilder ->
                    funcSpecBuilder.addStatement("if(from == null) return null")
                    if(descriptor.isCompositeId){
                        val idFieldsSize = descriptor.compositeIdFieldNames.size
                        funcSpecBuilder.addStatement(
                            "val notfoundMsg = %S",
                            buildCodeBlock {
                                add("Cannot find entity for string representation of %T or one of it' components", descriptor.idClassName)
                            })
                        funcSpecBuilder.addStatement("val idComponents = from.split(%S)", "_")
                        funcSpecBuilder.addStatement(
                            "if(idComponents.size != %L) throw IllegalArgumentException(%S)",
                            idFieldsSize,
                            buildCodeBlock {
                                add("String representation of %T must have %L non-blank components", descriptor.idClassName, idFieldsSize)
                            })
                        //if(idComponents.size != 2)
                        funcSpecBuilder.addComment("compositeIdFieldNames: ${descriptor.compositeIdFieldNames.joinToString(",")}")
                        val funcBody = CodeBlock.builder().addStatement("return %T(", descriptor.idClassName)
                        funcBody.indent()
                        descriptor.compositeIdFieldNames.forEachIndexed { index, fieldName ->
                            val fieldType = descriptor.compositeIdClassNames[fieldName]?: error("No type for composite id field $fieldName")
                            val maybeComma = if(index + 1 == descriptor.compositeIdFieldNames.size) "" else ","
                            funcBody.addStatement("$fieldName = conversionService.convert(idComponents[%L], %T::class.java)",
                                index, fieldType
                            )
                            funcBody.indent().addStatement("?: throw %T(notfoundMsg)$maybeComma", NotFoundException::class.java).unindent()
                        }
                        funcBody.unindent()
                        funcBody.addStatement(")")
                        funcSpecBuilder.addCode(funcBody.build())
                    }
                    else funcSpecBuilder.addStatement("return conversionService.convert(from, %T::class.java)", descriptor.idClassName)
                    funcSpecBuilder
                }
                .build())
            .addFunction(FunSpec.builder("convertIdToString")
                .addModifiers(PUBLIC, OVERRIDE)
                .returns(String::class.java.asTypeName().asKotlinTypeName()
                    .copy(nullable = true))
                .addParameter(ParameterSpec.builder(
                    "resourceId",
                    descriptor.idClassName.copy(nullable = true)
                ).build())
                .addStatement("if(resourceId == null) return null")
                .addStatement(
                    "val incompleteIdMsg = %S",
                    buildCodeBlock {
                        add("Cannot build string representation from incomplete %T", descriptor.idClassName)
                    })
                .let { funcSpecBuilder ->
                    if(descriptor.isCompositeId){
                        val funcBody = CodeBlock.builder().addStatement("return %T()", StringBuilder::class.java)
                        funcBody.indent()
                        descriptor.compositeIdFieldNames.forEachIndexed { index, fieldName ->
                            //val fieldType = descriptor.compositeIdClassNames[fieldName]?: error("No type for composite id field $fieldName")

                            funcBody.addStatement(
                                ".append(conversionService.convert(resourceId.%L ?: throw %T(incompleteIdMsg), %T::class.java))",
                                fieldName,
                                IllegalArgumentException::class.java,
                                String::class.java.asTypeName().asKotlinTypeName()
                            )
                            if(index + 1 < descriptor.compositeIdFieldNames.size)
                                funcBody.addStatement(".append(%S)", "_")
                        }
                        funcBody.addStatement(".toString()")
                        funcBody.unindent()
                        funcSpecBuilder.addCode(funcBody.build())
                    }
                    else funcSpecBuilder.addStatement("return conversionService.convert(resourceId, %T::class.java)",
                        String::class.java.asTypeName().asKotlinTypeName())
                    funcSpecBuilder
                }
                .build())
            .build()
    }

    private fun getTargetPackageAndSimpleName(
        descriptor: ScrudModelDescriptor,
        scrudBeansPropName: String,
        defaultPropValueKey: String
    ): ClassName {
        val classNameStringOrNull = descriptor.scrudBeanClassNamesValue(scrudBeansPropName)
        return if(classNameStringOrNull.isNullOrBlank() || classNameStringOrNull == Object::class.qualifiedName)
            ClassName.bestGuess(getSuperclassName(descriptor, defaultPropValueKey))
        else {
            ClassName.bestGuess(classNameStringOrNull)
        }
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
        val superClassName = getTargetPackageAndSimpleName(
            descriptor, "serviceSuperInterface", CLASSNAME_KEY_SERVICE_INTERFACE)
        return TypeSpec.interfaceBuilder(className)
                .addSuperinterface(
                    superClassName.parameterizedBy(
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
        val superClassNameRaw = getTargetPackageAndSimpleName(
            descriptor, "serviceImplSuperClass", CLASSNAME_KEY_SERVICE_IMPL)
        val superClassName = superClassNameRaw.parameterizedBy(
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
                .addParameter(ParameterSpec.builder(
                    "identifierAdapter", identifierAdapterClassName)
                    .addAnnotation(org.springframework.context.annotation.Lazy::class.java)
                    .build())
                //.addParameter("conversionService", ConversionService::class.java)
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
            .addSuperclassConstructorParameter("entityManager")
            .addSuperclassConstructorParameter("identifierAdapter")
            //.addSuperclassConstructorParameter("conversionService")
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
                        ClassName(pkgAndName.first, pkgAndName.second).parameterizedBy(
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
        return pattern.replace(multipleSlashesRegex, "/")
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
                            && (it.hasAnnotation(CreatedDate::class.java)
                                || it.hasAnnotation(LastModifiedDate::class.java)
                                || it.hasAnnotation(CreatedBy::class.java)
                                || it.hasAnnotation(LastModifiedBy::class.java)
                                || it.hasAnnotation(Transient::class.java)
                                || it.hasAnnotation(org.springframework.data.annotation.Transient::class.java)
                                || it.hasAnnotation(org.springframework.data.annotation.ReadOnlyProperty::class.java)
                                || it.hasAnnotation(Id::class.java)
                                || it.hasAnnotation(Id::class.java)
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
        val primaryTargetTypeElementFields = getFieldInfos(stateInfo.typeElement)
        val elementInfo = SimpleAnnotatedElementInfo(
                processingEnvironment = processingEnvironment,
                primaryTargetTypeElement = stateInfo.typeElement,
                primaryTargetTypeElementFields = primaryTargetTypeElementFields,
                annotation = stateInfo.typeElement.getAnnotationMirror(ScrudBean::class.java),
                ignoreProperties = emptyList(),
                nonUpdatableProperties = getNonUpdatableFields(stateInfo, primaryTargetTypeElementFields.map { it.variableElement }),
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
