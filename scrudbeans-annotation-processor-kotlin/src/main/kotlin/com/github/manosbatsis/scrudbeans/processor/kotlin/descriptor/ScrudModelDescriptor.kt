package com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor.EntityModelDescriptor
import com.squareup.kotlinpoet.ClassName
import java.util.Properties
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.AnnotationMirror
import javax.lang.model.element.TypeElement

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * [ScrudBean]. Used during kotlinpoet-driven sourcecode generation.
 */
class ScrudModelDescriptor(
        processingEnv: ProcessingEnvironment,
        typeElement: TypeElement,
        val configProperties: Properties) : EntityModelDescriptor(processingEnv, typeElement) {

    val scrudBean: ScrudBean
    val className: ClassName
    val dtoTypes: MutableSet<String> = mutableSetOf()

    init {
        scrudBean = typeElement.getAnnotation(ScrudBean::class.java)
        className = typeElement.asKotlinClassName()
        initDtoClassnames(typeElement)
    }


    /** Initialise the set of DTO classnames for this ScrudBean. Used to create mappers from/to this ScrudBean  */
    private fun initDtoClassnames(typeElement: TypeElement) {
        // Get DTO classnames from "dtoTypes"
        val ignored = listOf(
                Any::class.java.canonicalName,
                Object::class.java.canonicalName
        )
        toAnnotationClassNamesValueStream(typeElement, ScrudBean::class.java, "dtoTypes")
                .filter { it.isNotBlank() && !ignored.contains(it) }
                .forEach { dtoTypes.add(it) }
        // Add DTO classnames from "dtoTypeNames"
        scrudBean.dtoTypeNames.filter { it.isNotBlank() && !ignored.contains(it) }.forEach { dtoTypes.add(it) }
    }

    /**
     * Avoid [MirroredTypesException] when retrieving Class or Class[] typed annotation attribute values
     * @param typeElement
     * @param annotationClass
     * @param annotationAttributeName
     * @return a stream with the value classnames
     */
    private fun toAnnotationClassNamesValueStream(
            typeElement: TypeElement, annotationClass: Class<*>,
            annotationAttributeName: String): List<String> {
        return typeElement.annotationMirrors
                .filter { annotationMirror: AnnotationMirror -> annotationMirror.annotationType.toString().contains(annotationClass.name) }
                .map { obj: AnnotationMirror -> obj.elementValues }
                .flatMap { it.entries }
                .filter { it.key.simpleName.contentEquals(annotationAttributeName) }
                .flatMap {
                    val value = it.value.value
                    if (value is List<*>) value
                    else if (value is Array<*>)  value.toList()
                    else listOf(value)
                }
                .filterNotNull()
                .map { it: Any ->
                    var value = it.toString()
                    if (value.endsWith(".class")) value = value.substring(0, value.length - 6)
                    value
                }
    }


}