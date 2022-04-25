package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.CompositeDtoStrategy
import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.DtoStrategyComposition
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo

class ScrudBeansDtoStrategy(
        annotatedElementInfo: AnnotatedElementInfo,
        composition: DtoStrategyComposition =
                CompositeDtoStrategy(annotatedElementInfo)
) : CompositeDtoStrategy(
        annotatedElementInfo,composition
), ProcessingEnvironmentAware, AnnotatedElementInfo by annotatedElementInfo {
}
