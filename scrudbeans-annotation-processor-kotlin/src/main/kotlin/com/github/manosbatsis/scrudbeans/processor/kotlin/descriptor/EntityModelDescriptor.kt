package com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor

import com.squareup.kotlinpoet.ClassName
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Types
import javax.persistence.EmbeddedId
import javax.persistence.Id

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * [Entity]. Used during kotlinpoet-driven sourcecode generation.
 */
open class EntityModelDescriptor(
    processingEnvironment: ProcessingEnvironment,
    typeElement: TypeElement
) : ModelDescriptor(processingEnvironment, typeElement) {

    companion object{
        val idAnnotations = listOf(Id::class.java, EmbeddedId::class.java)
    }

    lateinit var idClassName: ClassName
    lateinit var idName: String

    protected fun checkIfMemberIsId(types: Types, e: VariableElement) {
        for(annotationClass in idAnnotations) if (e.getAnnotation(annotationClass) != null) {
            val className = getMemberType(types, e)
            val simpleName = className.substring(className.lastIndexOf('.') + 1)
            val packageName = className.substring(0, className.lastIndexOf('.'))
            idClassName = ClassName(packageName, simpleName)
            idName = e.simpleName.toString()
            break
        }

    }

    override fun scanMember(types: Types, currentTypeElement: TypeElement, memberElement: VariableElement) {
        checkIfMemberIsId(types, memberElement)
    }
}