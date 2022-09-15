package com.github.manosbatsis.scrudbeans.processor.kotlin.strategy

import com.github.manosbatsis.kotlin.utils.kapt.dto.strategy.composition.SimpleDtoTypeStrategy
import com.github.manosbatsis.kotlin.utils.kapt.processor.AnnotatedElementInfo
import com.github.manosbatsis.scrudbeans.api.domain.PersistenceHintsDto
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec

class ScrudBeansDtoTypeStrategy(
    annotatedElementInfo: AnnotatedElementInfo
) : SimpleDtoTypeStrategy(annotatedElementInfo) {

    override fun addSuperTypes(typeSpecBuilder: TypeSpec.Builder) {
        super.addSuperTypes(typeSpecBuilder)
        typeSpecBuilder.addSuperinterface(PersistenceHintsDto::class)
        typeSpecBuilder.addFunction(
            FunSpec.builder("isDetachedUpdate")
                .addModifiers(KModifier.OVERRIDE)
                .returns(Boolean::class)
                .addCode("return ${annotatedElementInfo.updateRequiresNewInstance}")
                .build()
        )
    }
}
