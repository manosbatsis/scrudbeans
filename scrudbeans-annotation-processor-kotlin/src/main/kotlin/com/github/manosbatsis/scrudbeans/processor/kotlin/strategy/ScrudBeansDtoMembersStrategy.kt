package com.github.manosbatsis.scrudbeans.processor.kotlin.strategy

import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.*
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo
import javax.lang.model.element.VariableElement

/** Simple implementation of [DtoMembersStrategy] */
open class ScrudBeansDtoMembersStrategy(
    annotatedElementInfo: AnnotatedElementInfo,
    dtoNameStrategy: DtoNameStrategy,
    dtoTypeStrategy: DtoTypeStrategy
) : SimpleDtoMembersStrategy(annotatedElementInfo, dtoNameStrategy, dtoTypeStrategy) {

    /** Alt constructor using a "root" strategy  */
    constructor(
        rootDtoStrategy: DtoStrategyLesserComposition
    ) : this(rootDtoStrategy.annotatedElementInfo, rootDtoStrategy, rootDtoStrategy) {
        this.rootDtoStrategy = rootDtoStrategy
    }

    override fun useMutableIterables(): Boolean = true

    override fun toPatchStatement(fieldIndex: Int, variableElement: VariableElement, commaOrEmpty: String): DtoMembersStrategy.Statement? {
        val propertyName = rootDtoMembersStrategy.toPropertyName(variableElement)
        val maybeNamedParam = if(annotatedElementInfo.primaryTargetTypeElement.isKotlin()) "$propertyName = " else ""
        return if(annotatedElementInfo.nonUpdatableProperties.contains(propertyName)){
            return DtoMembersStrategy.Statement("      $maybeNamedParam" +
                    "errNonUpdatableOrOriginalValue(%S, $propertyName, original.$propertyName)$commaOrEmpty", listOf(propertyName)
            )
        }
        else {
            val assignmentContext = assignmentCtxForToPatched(propertyName)
            return DtoMembersStrategy.Statement("      ${maybeNamedParam}this.$propertyName${assignmentContext.fallbackValue}$commaOrEmpty")
        }

    }

}