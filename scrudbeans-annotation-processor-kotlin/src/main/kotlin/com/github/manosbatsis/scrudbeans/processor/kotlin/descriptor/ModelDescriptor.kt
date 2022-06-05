package com.github.manosbatsis.scrudbeans.processor.kotlin.descriptor

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.scrudbeans.api.mdd.ScrudModelProcessorException
import javax.persistence.Entity
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.element.*
import javax.lang.model.element.ElementKind.FIELD
import javax.lang.model.element.ElementKind.METHOD
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.TypeKind.DECLARED
import javax.lang.model.type.TypeMirror
import javax.lang.model.type.TypeVariable
import javax.lang.model.util.Types

/**
 * Base implementation for classes describing (entity) models
 */
abstract class ModelDescriptor(
    override val processingEnvironment: ProcessingEnvironment,
    val typeElement: TypeElement
) : ProcessingEnvironmentAware {

    var jpaEntity: Boolean = false
    val qualifiedName: String
    val simpleName: String
    val packageName: String
    val parentPackageName: String

    init {
        jpaEntity = typeElement.getAnnotation(Entity::class.java) != null
        simpleName = this.typeElement.simpleName.toString()
        qualifiedName = this.typeElement.qualifiedName.toString()
        packageName = qualifiedName.substring(0, qualifiedName.length - (simpleName.length + 1))
        parentPackageName = packageName.substring(0, packageName.lastIndexOf("."))
        scanMembers(processingEnvironment.typeUtils, typeElement)
        finalise()
    }

    protected fun scanMembers(types: Types, currentTypeElement: Element) {
        processingEnvironment.noteMessage {
            "scanMembers, currentTypeElement: ${currentTypeElement.simpleName}, " +
                "kind: ${currentTypeElement.kind}"
        }
        when (currentTypeElement.kind) {
            ElementKind.CLASS -> {
                val typeElement = currentTypeElement as TypeElement
                scanMembers(types, typeElement, typeElement.accessibleConstructorParameterFields())
            }
            ElementKind.CONSTRUCTOR -> {
                val constructorTypeElement = currentTypeElement as ExecutableElement
                scanMembers(types, constructorTypeElement.enclosingElement as TypeElement, constructorTypeElement.parameters)
            }
            else -> throw IllegalArgumentException("Invalid element type, expected a class or constructor")
        }
    }

    protected fun scanMembers(types: Types, currentTypeElement: TypeElement, fields: List<VariableElement>) {

        processingEnvironment.noteMessage {
            "scanMembers, currentTypeElement: ${currentTypeElement.simpleName}, " +
                "fields: ${fields.joinToString(",") { it.simpleName }}"
        }
        fields.forEach {
            scanMember(types, currentTypeElement, it)
        }
        val superTypeElement = asTypeElement(types, currentTypeElement.superclass)
        if (!superTypeElement.qualifiedName.contentEquals(Any::class.java.canonicalName)) {
            scanMembers(types, superTypeElement)
        }
    }

    abstract fun scanMember(types: Types, currentTypeElement: TypeElement, memberElement: VariableElement)

    open fun finalise() {
        /*NO-OP*/
    }

    /**
     * Convert the given [TypeMirror] to a [TypeElement]
     * @param typeMirror
     * @return
     * @throws ScrudModelProcessorException
     */
    @Throws(ScrudModelProcessorException::class)
    protected fun asTypeElement(types: Types, typeMirror: TypeMirror): TypeElement {
        if (typeMirror.kind != DECLARED) {
            throw ScrudModelProcessorException(
                "Method asTypeElement Was expecting TypeKind.DECLARED but was " + typeMirror.kind
            )
        }
        val element = (typeMirror as DeclaredType).asElement()
        if (!(element.kind.isClass || element.kind.isInterface)) {
            throw ScrudModelProcessorException("Method asTypeElement Was expecting class or interface but was " + element.kind)
        }
        return element as TypeElement
    }

    /**
     * Inspect member and get type if field or  getter, null otherwise.
     * @param types
     * @param scrudModelMember
     * @return
     * @throws ScrudModelProcessorException
     */
    protected fun getMemberType(types: Types, scrudModelMember: VariableElement): String {
        var typeMirror: TypeMirror
        // If member is a field
        if (scrudModelMember.kind == FIELD || scrudModelMember.kind == ElementKind.PARAMETER) {
            val ve = scrudModelMember as VariableElement
            typeMirror = ve.asType()
            // replace generic type variables with the current concrete type
            if (typeMirror is TypeVariable) {
                typeMirror = types.asMemberOf(typeElement.asType() as DeclaredType, ve)
            }
        } else if (scrudModelMember.kind == METHOD && scrudModelMember.simpleName.toString().startsWith("get")) {
            val ee = scrudModelMember as ExecutableElement
            typeMirror = ee.returnType
            // replace generic type variables with the current concrete type
            if (typeMirror is TypeVariable) {
                typeMirror = types.asMemberOf(typeElement.asType() as DeclaredType, ee)
            }
        } else {
            throw ScrudModelProcessorException(
                "Could not process member " + scrudModelMember + ", kind: " + scrudModelMember.kind
            )
        }

        return typeMirror.asTypeElement().asKotlinClassName().toString()
    }

    val stack: String
        get() = if (jpaEntity!!) STACK_JPA else ""

    companion object {
        const val STACK_JPA = "jpa"
    }
}