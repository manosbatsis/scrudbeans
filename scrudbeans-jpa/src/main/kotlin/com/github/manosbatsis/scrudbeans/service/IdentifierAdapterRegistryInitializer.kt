package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdaptersRegistry
import org.springframework.beans.factory.InitializingBean


class IdentifierAdapterRegistryInitializer(
    val identifierAdapters: List<IdentifierAdapter<*,*>>
): InitializingBean {
    override fun afterPropertiesSet() {
        identifierAdapters.forEach {
            IdentifierAdaptersRegistry.addAdapterForClass(it.entityType, it)
        }
    }

}
