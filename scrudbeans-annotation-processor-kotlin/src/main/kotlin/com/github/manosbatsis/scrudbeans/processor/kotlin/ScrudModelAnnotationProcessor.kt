package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.ModelDescriptor
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.ScrudModelDescriptor
import com.squareup.kotlinpoet.*
import jakarta.persistence.Entity
import org.slf4j.LoggerFactory
import java.io.File
import java.io.IOException
import java.util.LinkedList
import java.util.Objects
import java.util.Properties
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Name
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic
import javax.tools.StandardLocation

/**
 * Annotation processor that generates SCRUD components
 * for model annotated with @[ScrudBean]
 * and JPA specification predicate factories for models
 * annotated with @[Entity]
 */
@SupportedAnnotationTypes("com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean")
@SupportedSourceVersion(SourceVersion.RELEASE_17)
class ScrudModelAnnotationProcessor : AbstractProcessor(), ProcessingEnvironmentAware {

    companion object {
        private val log = LoggerFactory.getLogger(ScrudModelAnnotationProcessor::class.java)
        const val BLOCK_FUN_NAME = "block"
        const val KAPT_KOTLIN_SCRUDBEANS_GENERATED_OPTION_NAME = "kapt.kotlin.vaultaire.generated"
        const val KAPT_KOTLIN_GENERATED_OPTION_NAME = "kapt.kotlin.generated"

        val typeParameterStar = WildcardTypeName.producerOf(Any::class.asTypeName().copy(nullable = true))
    }

    private var complete = false
    private val typeSpecBuilder by lazy { TypeSpecBuilder(processingEnv) }
    private lateinit var filer: Filer

    // Config properties, i.e. "application.properties" from the classpath
    private lateinit var configProps: Properties

    /** Implement [ProcessingEnvironmentAware.processingEnvironment] for access to a [ProcessingEnvironment] */
    override val processingEnvironment by lazy {
        processingEnv
    }

    val sourceRootFile by lazy {
        val sourceRootFile = File(generatedSourcesRoot)
        sourceRootFile.mkdir()
        sourceRootFile
    }
    val generatedSourcesRoot: String by lazy {
        processingEnv.options[KAPT_KOTLIN_SCRUDBEANS_GENERATED_OPTION_NAME]
            ?: processingEnv.options[KAPT_KOTLIN_GENERATED_OPTION_NAME]
            ?: defaultGeneratedSourcesDir()
    }

    private fun defaultGeneratedSourcesDir(): String {
        val path = filer.createSourceFile("ScrudBeansGeneratedSourcesRoot")
            .let { File(it.toUri().toURL().file).parent }
        val msg = "No kapt.kotlin.generated option provided. Using $path"
        this.processingEnv.messager.printMessage(Diagnostic.Kind.MANDATORY_WARNING, msg + "\n")
        return path
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
        // generateEntityPredicateFactories(roundEnv)
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
                        processingEnv.noteMessage { "ScrudModelAnnotationProcessor processing ${element.simpleName}" }
                        // Parse model to something more convenient
                        val descriptor = ScrudModelDescriptor(processingEnv, element, configProps)
                        // Mappers for manual DTOs
                        // generateDtoMappers(descriptor)
                        generateDto(descriptor)
                        createIdAdapters(descriptor)
                        createRepository(descriptor)
                        createService(descriptor)
                        createController(descriptor)
                    } else {
                        element.errorMessage { "Not an instance of TypeElement but annotated with ScrudBean: ${element.simpleName}" }
                    }
                } catch (e: Throwable) {
                    element.errorMessage { "Failed processing ScrudBean annotation for ${element.simpleName}: ${e.message ?: e.cause?.message}" }
                    e.printStackTrace()
                    throw e
                }
            }
        }
    }

    /**
     * Create a DTO source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun generateDto(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = typeSpecBuilder.dtoSpecBuilder(descriptor, sourceRootFile)
        return writeKotlinFile(descriptor, typeSpec, descriptor.packageName)
    }

    /**
     * Create a SCRUD REST controller source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createController(descriptor: ScrudModelDescriptor): FileSpec? {
        return if (ScrudBean.NONE != descriptor.scrudBean.controllerSuperClass) {
            writeKotlinFile(
                descriptor,
                typeSpecBuilder.createController(descriptor),
                descriptor.parentPackageName + ".controller",
            )
        } else {
            null
        }
    }

    /**
     * Create SCRUD service source files
     * @param descriptor The target model descriptor
     * @return the written files: interface and implementation
     */
    private fun createService(descriptor: ScrudModelDescriptor): List<FileSpec?> {
        val files = LinkedList<FileSpec?>()
        // Ensure a service has not already been created
        val serviceQualifiedName = descriptor.parentPackageName + ".service." + descriptor.simpleName + "Service"
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
        val typeSpec = typeSpecBuilder.createServiceInterface(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.parentPackageName + ".service")
    }

    /**
     * Create a SCRUD service implementation source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createServiceImpl(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = typeSpecBuilder.createServiceImpl(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.parentPackageName + ".service")
    }

    /**
     * Create a SCRUD repository source file
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createRepository(descriptor: ScrudModelDescriptor): FileSpec? {
        val typeSpec = typeSpecBuilder.createRepository(descriptor)
        return writeKotlinFile(descriptor, typeSpec, descriptor.parentPackageName + ".repository")
    }

    /**
     * Create an [IdentifierAdapter] implementation
     * @param descriptor The target model descriptor
     * @return the written file
     */
    private fun createIdAdapters(descriptor: ScrudModelDescriptor): List<FileSpec> {
        return listOf(
            ClassName(descriptor.packageName, descriptor.simpleName),
        )
            .mapNotNull {
                writeKotlinFile(descriptor, typeSpecBuilder.createIdAdapter(it, descriptor), descriptor.packageName)
            }
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
                    .addFileComment("-------------------- DO NOT EDIT -------------------\n")
                    .addFileComment(" This file is automatically generated by scrudbeans,\n")
                    .addFileComment(" see https://manosbatsis.github.io/scrudbeans\n")
                    .addFileComment(" To edit this file, copy it to the appropriate package \n")
                    .addFileComment(" in your src/main/kotlin folder and edit there. \n")
                    .addFileComment("----------------------------------------------------")
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

    private fun loadProperties(): Properties {
        var props = Properties()
        try {
            val fileObject = this.filer!!
                .getResource(StandardLocation.CLASS_OUTPUT, "", "application.properties")
            props.load(fileObject.openInputStream())
            processingEnv.noteMessage { "loadProperties, props: $props" }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return props
    }
}
