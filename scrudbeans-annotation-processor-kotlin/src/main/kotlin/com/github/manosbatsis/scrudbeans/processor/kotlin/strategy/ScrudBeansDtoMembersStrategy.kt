package com.github.manosbatsis.scrudbeans.processor.kotlin.strategy

import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.*
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo

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

}