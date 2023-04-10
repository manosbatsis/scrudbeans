package com.github.manosbatsis.scrudbeans.model

import jakarta.persistence.*
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.domain.Persistable

/**
 * A base class for [Persistable], mainly for entities with manually assigned identifiers.
 *
 * See https://docs.spring.io/spring-data/jpa/docs/current-SNAPSHOT/reference/html/#jpa.entity-persistence.saving-entites.strategies
 */
@MappedSuperclass
abstract class AbstractPersistableEntity<ID> : Persistable<ID> {

    @Transient
    private var isNew = true

    override fun isNew(): Boolean = isNew

    @PrePersist
    @PostLoad
    open fun markNotNew() {
        isNew = false
    }

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            !AbstractVersionedEntity::class.isInstance(other) -> false
            else -> {
                other as AbstractVersionedEntity<ID>
                id == other.id
            }
        }
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(21, 41)
            .appendSuper(super.hashCode())
            .append(id)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("id", id)
            .build()
    }
}
