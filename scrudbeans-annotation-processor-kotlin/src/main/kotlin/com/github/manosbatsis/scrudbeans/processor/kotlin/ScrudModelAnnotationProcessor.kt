package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.scrudbeans.api.DtoMapper
import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.api.mdd.model.EntityModelDescriptor
import com.github.manosbatsis.scrudbeans.api.mdd.model.ModelDescriptor
import com.github.manosbatsis.scrudbeans.api.mdd.model.ScrudModelDescriptor
import com.github.manosbatsis.kotlinpoet.utils.BaseProcessor
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.WildcardTypeName
import com.squareup.kotlinpoet.asTypeName
import java.io.File

import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.persistence.Entity
import javax.tools.StandardLocation
import java.io.IOException
import java.util.*

/**
 * Annotation processor that generates SCRUD components
 * for model annotated with @[ScrudBean]
 * and JPA specification predicate factories for models
 * annotated with @[Entity]
 */
@SupportedAnnotationTypes("com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class ScrudModelAnnotationProcessor : BaseProcessor() {

    companion object {
        const val BLOCK_FUN_NAME = "block"
        const val KAPT_KOTLIN_SCRUDBEANS_GENERATED_OPTION_NAME = "kapt.kotlin.vaultaire.generated"
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

        val TYPE_PARAMETER_STAR = WildcardTypeName.producerOf(Any::class.asTypeName().copy(nullable = true))
    }

    private var complete = false
    private var filer: Filer? = null

    // Config properties, i.e. "application.properties" from the classpath
    private var configProps: Properties? = null

    val generatedSourcesRoot: String by lazy {
        processingEnv.options[KAPT_KOTLIN_SCRUDBEANS_GENERATED_OPTION_NAME]
                ?: processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
                ?: throw IllegalStateException("Can't find the target directory for generated Kotlin files.")
    }

    val sourceRootFile by lazy {
        val sourceRootFile = File(generatedSourcesRoot)
        sourceRootFile.mkdir()
        sourceRootFile
    }

    /**
     * {@inheritDoc}
     */
    override fun process(annotations: Set<TypeElement>, roundEnv: RoundEnvironment): Boolean {
        // short-circuit if there are multiple rounds
        if (complete) {
            processingEnv.noteMessage { "Processor has already been executed, ignoring" }
            return true
        }
        processingEnv.noteMessage { "ScrudModelAnnotationProcessor processing started" }
        // Init a filer
        this.filer = processingEnv.filer
        // Load config/properties
        configProps = this.loadProperties()
        // Create JPA query predicate factories for each entity in the source path
        generateEntityPredicateFactories(roundEnv)
        // Create other SCRUD components for each model annotated with ScrudBean
        generateScrudComponents(roundEnv)
        // Claiming that annotations have been processed by this processor
        complete = true
        return true
    }

    /**
     * Create SCRUD components for the target model
     * @param roundEnv The current compilation round environment
     */
    private fun generateScrudComponents(roundEnv: RoundEnvironment) {
        val annotatedModels = roundEnv.getElementsAnnotatedWith(ScrudBean::class.java)
        val modelDescriptors = HashMap<Name, ScrudModelDescriptor>()
        processingEnv.noteMessage { "ScrudModelAnnotationProcessor found ${annotatedModels?.size ?: 0} annotated classes" }
        if (annotatedModels != null) {
            for (element in annotatedModels) {
                try {
                    if (element is TypeElement) {
                        // Parse model to something more convenient
                        val descriptor = ScrudModelDescriptor(processingEnv, element, configProps)
                        // Generate components for model
                        generateDtoMappers(descriptor)
                        createRepository(descriptor)
                        createService(descriptor)
                        createController(descriptor)
                    } else {
                        processingEnv.errorMessage { "Not an instance of TypeElement but annotated with ScrudBean: ${element.simpleName}" }
                    }
                } catch (e: ScrudModelProcessorException) {
                    processingEnv.errorMessage { "Failed processing ScrudBean annotation for: ${element.simpleName}: ${e.message}" }
                    throw e
                }

            }
        }
    }

    /**
     * Create JPA query predicate factories for each entity in the source path
     * @param roundEnv The current compilation round environment
     */
    private fun generateEntityPredicateFactories(roundEnv: RoundEnvironment) {
        val entities = roundEnv.getElementsAnnotatedWith(ScrudBean::class.java)
        for (element in entities) {
            try {
                if (element.getAnnotation(Entity::class.java) != null) {
                    if (element is TypeElement) {
                        processingEnv.noteMessage { "generateEntityPredicateFactories, processing element: ${element.getSimpleName()}" }
                        val descriptor = EntityModelDescriptor(processingEnv, element)
                        createPredicateFactory(descriptor)
                    } else {
                            processingEnv.noteMessage { "Not an instance of TypeElement but annotated with ScrudBean: ${element.simpleName}" }
                    }
                }
            } catch (e: RuntimeException) {
                processingEnv.errorMessage { "Error generating components for element.simpleName ${e.message}: " }
                throw e
            } catch (e: ScrudModelProcessorException) {
                processingEnv.errorMessage { "Error generating components for ${element.simpleName}: " + e.message }
                throw e
            }

        }
    }

    /**
     * Create a SCRUD REST controller source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createController(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = TypeSpecBuilder.createController(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".controller")
    }

    /**
     * Create [DtoMapper]s for the ScudBeans' target DTOs
     * @param descriptor The target model descriptor
     * @return the mapper files
     */
    private fun generateDtoMappers(descriptor: ScrudModelDescriptor): List<FileSpec?> {
        val files = LinkedList<FileSpec?>()
        descriptor.getDtoTypes().forEach { dtoClass ->
            val typeSpec = TypeSpecBuilder.createDtoMapper(descriptor, dtoClass)
            files.add(writeKotlinFile(
                    descriptor,
                    typeSpec,
                    descriptor.getParentPackageName() + ".mapper"))
        }
        return files
    }

    /**
     * Create SCRUD service source files
     * @param descriptor The target model descriptor
     * @return the written files: interface and implementation
     */
    private fun createService(descriptor: ScrudModelDescriptor): List<FileSpec?> {
        val files = LinkedList<FileSpec?>()
        // Ensure a service has not already been created
        val serviceQualifiedName = descriptor.getParentPackageName() +
                ".service." + descriptor.getSimpleName() + "Service"
        val existing = processingEnv.elementUtils.getTypeElement(serviceQualifiedName)
        if (Objects.isNull(existing)) {
            files.add(createServiceInterface(descriptor))
            files.add(createServiceImpl(descriptor))
        } else {
            processingEnv.noteMessage { "createService: $serviceQualifiedName} already exists, skipping" }
        }
        return files
    }

    /**
     * Create a SCRUD service interface source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createServiceInterface(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = TypeSpecBuilder.createServiceInterface(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".service")

    }

    /**
     * Create a SCRUD service implementation source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createServiceImpl(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = TypeSpecBuilder.createServiceImpl(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".service")
    }

    /**
     * Create a SCRUD repository source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createRepository(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = TypeSpecBuilder.createRepository(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".repository")
    }

    /**
     * Create a JPA specification predicate factory source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createPredicateFactory(descriptor: EntityModelDescriptor): FileSpec? {
        val typeSpec = TypeSpecBuilder.createPredicateFactory(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.getParentPackageName() + ".specification")
    }

    /**
     * Write and return a source file for the given [TypeSpec]
     * @param typeSpec The target model type spec
     * @param descriptor The target model descriptor
     * @param packageName The target source file package
     * @return the written file
     */
    private fun writeKotlinFile(descriptor: ModelDescriptor, typeSpec: TypeSpec, packageName: String): FileSpec? {
        val fileObjectName = packageName + "." + typeSpec.name!!
        var file: FileSpec? = null
        try {
            val existing = processingEnv.elementUtils.getTypeElement(fileObjectName)
            if (existing == null) {
                processingEnv.noteMessage { "writeJavaFile for $fileObjectName" }
                file = FileSpec.builder(packageName, typeSpec.name!!)
                        .addComment("-------------------- DO NOT EDIT -------------------\n")
                        .addComment(" This file is automatically generated by scrudbeans,\n")
                        .addComment(" see https://manosbatsis.github.io/scrudbeans\n")
                        .addComment(" To edit this file, copy it to the appropriate package \n")
                        .addComment(" in your src/main/kotlin folder and edit there. \n")
                        .addComment("----------------------------------------------------")
                        .addType(typeSpec)
                        .build()
                file.writeTo(sourceRootFile)
            } else {
                processingEnv.noteMessage { "writeJavaFile: Skipping for $fileObjectName as it already exists" }
            }
        } catch (e: Exception) {
            processingEnv.noteMessage { "writeJavaFile: Error creating file for $fileObjectName: ${e.message}" }
            throw e
        }

        return file
    }

    private fun loadProperties(): Properties? {
        var props: Properties? = null
        try {
            val fileObject = this.filer!!
                    .getResource(StandardLocation.CLASS_OUTPUT, "", "application.properties")
            props = Properties()
            props.load(fileObject.openInputStream())
            processingEnv.noteMessage { "loadProperties, props: $props" }
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return props
    }

}