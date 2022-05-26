package com.github.manosbatsis.scrudbeans.api.mdd.model

/**
 * Provides read/write access to the singular identifier of an (entity) type
 */
interface IdentifierAdapter<T, ID> {

    val entityType: Class<T>
    val entityIdType: Class<ID>
    val entityIdName: String
    fun getIdName(resource: T): String
    fun readId(resource: T): ID?
}