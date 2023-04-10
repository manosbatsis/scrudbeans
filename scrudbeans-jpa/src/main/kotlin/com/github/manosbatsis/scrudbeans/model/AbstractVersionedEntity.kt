package com.github.manosbatsis.scrudbeans.model

import jakarta.persistence.*
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder

/**
 * Base class that implements a "Version-Property" state detection strategy
 * for Spring to detect whether an entity is new or not.
 *
 * The strategy also provides for optimistic locking
 * and allows a non-nullable [id], so we find it preferable
 * compared to alternatives, i.e. implementing [org.springframework.data.domain.Persistable].
 *
 * See https://docs.spring.io/spring-data/jpa/docs/current-SNAPSHOT/reference/html/#jpa.entity-persistence.saving-entites.strategies
 */
@MappedSuperclass
abstract class AbstractVersionedEntity<ID>(version: Long? = null) : BaseEntity<ID> {

    @Version
    var version: Long? = version

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            !AbstractVersionedEntity::class.isInstance(other) -> false
            else -> {
                other as AbstractVersionedEntity<ID>
                id == other.id && version == other.version
            }
        }
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(21, 41)
            .appendSuper(super.hashCode())
            .append(id)
            .append(version)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("id", id)
            .append("version", version)
            .build()
    }
}
