package com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor

import com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean
import com.squareup.kotlinpoet.ClassName
import java.util.Properties
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * [ScrudBean]. Used during kotlinpoet-driven sourcecode generation.
 */
class ScrudModelDescriptor(
    processingEnvironment: ProcessingEnvironment,
    typeElement: TypeElement,
    val configProperties: Properties,
) : EntityModelDescriptor(processingEnvironment, typeElement) {
    companion object {
        val ignoredClassNameStringValues = setOf(
            java.lang.Object::class.java.canonicalName,
            Any::class.java.canonicalName,
        )
    }

    val scrudBean: ScrudBean
    val className: ClassName
    val dtoTypes: MutableSet<String> = mutableSetOf()

    init {
        try {
            scrudBean = typeElement.getAnnotation(ScrudBean::class.java)
            className = typeElement.asKotlinClassName()
        } catch (e: Throwable) {
            e.printStackTrace()
            throw e
        }
    }

    fun scrudBeanClassNamesValue(annotationAttributeName: String): String? {
        return typeElement.findAnnotationValueAsClassNameStrings(ScrudBean::class.java, annotationAttributeName)
            .filterNot { it.isNullOrBlank() || ignoredClassNameStringValues.contains(it) }
            .singleOrNull()
    }
}
