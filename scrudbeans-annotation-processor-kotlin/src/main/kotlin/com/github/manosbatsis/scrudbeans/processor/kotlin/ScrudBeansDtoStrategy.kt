package com.github.manosbatsis.scrudbeans.processor.kotlin

import com.github.manotbatsis.kotlin.utils.kapt.dto.strategy.CompositeDtoStrategy
import com.github.manotbatsis.kotlin.utils.kapt.processor.SimpleAnnotatedElementInfo
import javax.annotation.processing.ProcessingEnvironment

class ScrudBeansDtoStrategy(
        elementInfo: SimpleAnnotatedElementInfo,
        processingEnvironment: ProcessingEnvironment = elementInfo.processingEnvironment
): CompositeDtoStrategy(elementInfo, processingEnvironment) {
}
