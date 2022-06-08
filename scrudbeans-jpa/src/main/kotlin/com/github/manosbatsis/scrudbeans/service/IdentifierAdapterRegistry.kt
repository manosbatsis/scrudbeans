package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.scrudbeans.logging.loggerFor

class IdentifierAdapterRegistry(
    private val entityServices: Map<String, JpaEntityService<*, *>>
) {
    companion object {
        private val logger = loggerFor<IdentifierAdapterRegistry>()
    }

    init {
        logger.info("Initializing with services: ${entityServices.keys.joinToString(", ")}")
    }

    private val serviceKeysByEntityType: Map<String, String> by lazy {
        entityServices
            .map { it.value.identifierAdapter.entityType.canonicalName to it.key }
            .toMap()
    }

    private val serviceKeysByCompositeIdType: Map<String, String> by lazy {
        entityServices
            .filter { it.value.identifierAdapter.isCompositeId }
            .map { it.value.identifierAdapter.entityIdType.canonicalName to it.key }
            .toMap()
    }

    fun getServices() = entityServices.values

    fun <T : Any> findServiceForEntityType(entityType: Class<T>): JpaEntityService<T, *>? =
        serviceKeysByEntityType[entityType.canonicalName]
            ?.let {
                @Suppress("UNCHECKED_CAST")
                entityServices[it] as JpaEntityService<T, *>
            }

    fun <T : Any> getServiceForEntityType(entityType: Class<T>): JpaEntityService<T, *> =
        findServiceForEntityType(entityType)
            ?: throw IllegalArgumentException(
                "No service for entity type ${entityType.canonicalName}, " +
                    "known types: ${serviceKeysByEntityType.keys.joinToString(", ")}"
            )

    fun <T : Any> findServiceForCompositeIdType(idType: Class<T>): JpaEntityService<*, T>? =
        serviceKeysByCompositeIdType[idType.canonicalName]
            ?.let {
                @Suppress("UNCHECKED_CAST")
                entityServices[it] as JpaEntityService<*, T>
            }

    fun <T : Any> getServiceForCompositeIdType(idType: Class<T>): JpaEntityService<*, T> =
        findServiceForCompositeIdType(idType)
            ?: throw IllegalArgumentException(
                "No service for composite ID type ${idType.canonicalName}, " +
                    "known types: ${serviceKeysByEntityType.keys.joinToString(", ")}"
            )
}
