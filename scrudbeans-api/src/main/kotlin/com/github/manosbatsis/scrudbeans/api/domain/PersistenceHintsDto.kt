package com.github.manosbatsis.scrudbeans.api.domain

/**
 * Provides persistence hints like whether the entity instance
 * resulting from the DTO is detached,
 * i.e. created by constructor VS mutated
 */
interface PersistenceHintsDto {
    fun isDetachedUpdate(): Boolean
}