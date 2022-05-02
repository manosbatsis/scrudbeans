package com.github.manosbatsis.scrudbeans.processor.kotlin.strategy

import com.github.manosbatsis.kotlin.utils.ProcessingEnvironmentAware
import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.CompositeDtoStrategy
import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.DtoStrategyComposition
import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.SimpleDtoNameStrategy
import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.SimpleDtoTypeStrategy
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo

class ScrudBeansDtoStrategy(
        annotatedElementInfo: AnnotatedElementInfo,
        composition: DtoStrategyComposition = annotatedElementInfo.let {
                val dtoNameStrategy = SimpleDtoNameStrategy(annotatedElementInfo)
                val dtoTypeStrategy = SimpleDtoTypeStrategy(annotatedElementInfo)
                CompositeDtoStrategy(
                        annotatedElementInfo = annotatedElementInfo,
                        dtoNameStrategy = dtoNameStrategy,
                        dtoTypeStrategy = dtoTypeStrategy,
                        dtoMembersStrategy = ScrudBeansDtoMembersStrategy(
                                annotatedElementInfo, dtoNameStrategy, dtoTypeStrategy)
                )
        }

) : CompositeDtoStrategy(
        annotatedElementInfo,composition
), ProcessingEnvironmentAware, AnnotatedElementInfo by annotatedElementInfo {

}
