package com.github.manosbatsis.scrudbeans.service

class IdentifierAdapterRegistry(
    private val entityServices: Map<String, JpaEntityService<*, *>>
) {

    private val serviceKeysByEntityType: Map<Class<*>, String> by lazy {
        entityServices.map {
            listOf(
                it.value.identifierAdapter.entityType to it.key,
                it.value.identifierAdapter.entityIdType to it.key
            )
        }.flatten().toMap()
    }

    fun getServices() = entityServices.values

    fun <T : Any> findServiceFor(entityType: Class<T>): JpaEntityService<T, *>? {
        val service = serviceKeysByEntityType[entityType]
            ?.let {
                @Suppress("UNCHECKED_CAST")
                entityServices[it] as JpaEntityService<T, *>
            }
        return service
    }

    fun getServiceFor(entityType: Class<*>): JpaEntityService<*, *> =
        findServiceFor(entityType)
            ?: throw IllegalArgumentException("No service for entity type ${entityType.canonicalName}")
}
