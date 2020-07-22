package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manotbatsis.kotlin.utils.kapt.dto.strategy.CompositeDtoStrategy
import com.github.manotbatsis.kotlin.utils.kapt.dto.strategy.DtoStrategyComposition
import com.github.manotbatsis.kotlin.utils.kapt.dto.strategy.SimpleDtoStrategyComposition
import com.github.manotbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo

class ScrudBeansDtoStrategy(
        annotatedElementInfo: AnnotatedElementInfo,
        composition: DtoStrategyComposition =
                SimpleDtoStrategyComposition(annotatedElementInfo)
) : CompositeDtoStrategy(
        annotatedElementInfo,composition
), ProcessingEnvironmentAware, AnnotatedElementInfo by annotatedElementInfo {
}
