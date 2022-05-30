package com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.squareup.kotlinpoet.ClassName
import java.util.*
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * [ScrudBean]. Used during kotlinpoet-driven sourcecode generation.
 */
class ScrudModelDescriptor(
    processingEnvironment: ProcessingEnvironment,
    typeElement: TypeElement,
    val configProperties: Properties
) : EntityModelDescriptor(processingEnvironment, typeElement) {
    companion object{
        val ignoredClassNameStringValues = setOf(
            java.lang.Object::class.java.canonicalName,
            Any::class.java.canonicalName
        )
    }

    val scrudBean: ScrudBean
    val className: ClassName
    val dtoTypes: MutableSet<String> = mutableSetOf()

    init {
        try {
            scrudBean = typeElement.getAnnotation(ScrudBean::class.java)
            className = typeElement.asKotlinClassName()
            initDtoClassnames(typeElement)
        }catch (e: Throwable){
            e.printStackTrace()
            throw e;
        }
    }


    /** Initialise the set of DTO classnames for this ScrudBean. Used to create mappers from/to this ScrudBean  */
    private fun initDtoClassnames(typeElement: TypeElement) {
        // Get DTO classnames from "dtoTypes"
        typeElement.findAnnotationValueAsClassNameStrings(ScrudBean::class.java, "dtoTypes")
                .filterNot { it.isNullOrBlank() || ignoredClassNameStringValues.contains(it) }
                .forEach { dtoTypes.add(it) }
        // Add DTO classnames from "dtoTypeNames"
        scrudBean.dtoTypeNames
            .filterNot { it.isNullOrBlank() || ignoredClassNameStringValues.contains(it) }
            .forEach { dtoTypes.add(it) }
    }


    fun scrudBeanClassNamesValue(annotationAttributeName: String): String? {
        return typeElement.findAnnotationValueAsClassNameStrings(ScrudBean::class.java, annotationAttributeName)
            .filterNot { it.isNullOrBlank() || ignoredClassNameStringValues.contains(it) }
            .singleOrNull()
    }

}