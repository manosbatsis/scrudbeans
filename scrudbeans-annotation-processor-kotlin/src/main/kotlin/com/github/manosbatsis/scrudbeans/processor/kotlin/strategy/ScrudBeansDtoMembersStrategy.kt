package com.github.manosbatsis.scrudbeans.processor.kotlin.strategy

import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.*
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementFieldInfo
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo
import com.squareup.kotlinpoet.TypeSpec
import org.springframework.data.domain.Persistable

/** Simple implementation of [DtoMembersStrategy] */
open class ScrudBeansDtoMembersStrategy(
    annotatedElementInfo: AnnotatedElementInfo,
    dtoNameStrategy: DtoNameStrategy,
    dtoTypeStrategy: DtoTypeStrategy,
) : SimpleDtoMembersStrategy(annotatedElementInfo, dtoNameStrategy, dtoTypeStrategy) {

    /** Alt constructor using a "root" strategy  */
    constructor(
        rootDtoStrategy: DtoStrategyLesserComposition,
    ) : this(rootDtoStrategy.annotatedElementInfo, rootDtoStrategy, rootDtoStrategy) {
        this.rootDtoStrategy = rootDtoStrategy
    }

    override fun useMutableIterables(): Boolean = true

    override fun processFields(
        typeSpecBuilder: TypeSpec.Builder,
        annotatedElementInfo: AnnotatedElementInfo,
        fields: List<AnnotatedElementFieldInfo>,
    ) {
        val isPersistable = annotatedElementInfo.primaryTargetTypeElement
            .isAssignableTo(Persistable::class.java, true)
        annotatedElementInfo.processingEnvironment.noteMessage {
            "processFields, primaryTargetTypeElement: ${primaryTargetTypeElement.simpleName}, isPersistable: $isPersistable"
        }

        val filteredFields = if (isPersistable) {
            fields.filter {
                annotatedElementInfo.processingEnvironment.noteMessage {
                    "processFields, field: ${it.simpleName}"
                }
                it.simpleName != "isNew"
            }
        } else {
            fields
        }
        super.processFields(typeSpecBuilder, annotatedElementInfo, filteredFields)
    }
}
