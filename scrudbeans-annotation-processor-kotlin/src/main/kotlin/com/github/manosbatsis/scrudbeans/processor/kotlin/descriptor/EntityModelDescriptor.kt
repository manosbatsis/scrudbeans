package com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.asClassName
import jakarta.persistence.EmbeddedId
import jakarta.persistence.Id
import jakarta.persistence.IdClass
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.TypeElement
import javax.lang.model.element.VariableElement
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 * A metadata and utility context helper focusing on a single model annotated with
 * [Entity]. Used during kotlinpoet-driven sourcecode generation.
 */
open class EntityModelDescriptor(
    processingEnvironment: ProcessingEnvironment,
    typeElement: TypeElement
) : ModelDescriptor(processingEnvironment, typeElement), ProcessingEnvironmentAware {

    companion object {
        val idAnnotations = listOf(Id::class.java, EmbeddedId::class.java)
    }

    lateinit var idClassName: ClassName
    lateinit var idFieldName: String
    lateinit var compositeIdClassNames: MutableMap<String, ClassName>
    lateinit var compositeIdFieldNames: MutableList<String>
    var isEmbeddedId = false
    var isIdClass = false
    val isCompositeId: Boolean
        get() = isEmbeddedId || isIdClass

    protected fun checkIfMemberIsId(types: Types, variableElement: VariableElement) {
        if (!this::compositeIdClassNames.isInitialized) {
            compositeIdFieldNames = mutableListOf()
            compositeIdClassNames = mutableMapOf()
        }
        if (variableElement.getAnnotation(Id::class.java) != null) {
            val classnameStr = getMemberType(types, variableElement)
            val simpleName = classnameStr.substring(classnameStr.lastIndexOf('.') + 1)
            val packageName = classnameStr.substring(0, classnameStr.lastIndexOf('.'))
            val fieldName = variableElement.simpleName.toString()

            val className = ClassName(packageName, simpleName)
            compositeIdFieldNames.add(fieldName)
            compositeIdClassNames[fieldName] = className
        }
        if (variableElement.getAnnotation(EmbeddedId::class.java) != null) {
            val className = getMemberType(types, variableElement).let { ClassName.bestGuess(it) }
            idFieldName = variableElement.simpleName.toString()
            idClassName = className
            isEmbeddedId = true
            val embeddedIdFields = variableElement.asType().asTypeElement().getFieldsOnlyForHierarchy()
            for (field in embeddedIdFields) {
                val fieldName = field.simpleName.toString()
                compositeIdFieldNames.add(fieldName)
                compositeIdClassNames[fieldName] = field.asType().asTypeElement().asKotlinClassName()
            }
        }
    }

    override fun scanMember(types: Types, currentTypeElement: TypeElement, memberElement: VariableElement) {
        checkIfMemberIsId(types, memberElement)
    }

    override fun finalise() {
        if (!isEmbeddedId) {
            when {
                // Only a single @Id was present
                compositeIdClassNames.isEmpty() -> throw IllegalStateException("No identifier annotations were present in ${typeElement.asClassName().canonicalName} ")
                // Only a single @Id was present
                compositeIdClassNames.size == 1 -> {
                    idFieldName = compositeIdClassNames.keys.single()
                    idClassName = compositeIdClassNames.values.single()
                    compositeIdFieldNames = mutableListOf()
                    compositeIdClassNames = mutableMapOf()
                }
                // Multiple @Id annotations were present
                else -> {

                    processingEnvironment.messager.printMessage(
                        Diagnostic.Kind.WARNING,
                        "finalise, type: ${typeElement.asClassName().canonicalName}, compositeIdFieldNames: ${compositeIdFieldNames.joinToString { "," }}, " +
                            "compositeIdClassNames: ${compositeIdClassNames.values.joinToString { "," }}"
                    )
                    isIdClass = true
                    val idClassAnnotation = typeElement.findAnnotationMirror(IdClass::class.java)
                        ?: throw IllegalStateException("Multiple @Id annotations were present but ${typeElement.asClassName().canonicalName} is not annotated with @IdClass")
                    idClassName = idClassAnnotation.findValueAsClassNames("value").single()
                }
            }
        }
    }
}
