package com.github.manosbatsis.scrudbeans.api.mdd.model

/**
 * Provides information and helper functions
 * related to the identifier of an entity (type).
 */
interface IdentifierAdapter<T : Any, S : Any> {

    val entityType: Class<T>
    val entityIdType: Class<S>
    val isCompositeId: Boolean

    fun getId(resource: Any?): S?

    fun getIdAsString(resource: Any?): String?

    fun buildIdFromString(from: String?): S?

    fun convertIdToString(resourceId: S?): String?
}
