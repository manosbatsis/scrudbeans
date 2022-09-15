package com.github.manosbatsis.scrudbeans.model

import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class AbstractAuditableEntity(
    id: UUID? = null,

    @CreatedDate
    @Column(name = "created", nullable = false)
    var created: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    var updated: LocalDateTime? = null

) : AbstractBaseEntity(id) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (!AbstractAuditableEntity::class.isInstance(other)) return false
        other as AbstractAuditableEntity
        return EqualsBuilder()
            .appendSuper(super.equals(other))
            .append(created, other.created)
            .append(updated, other.updated)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(13, 33)
            .appendSuper(super.hashCode())
            .append(created)
            .append(updated)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("created", created)
            .append("updated", updated)
            .build()
    }
}

/**
 * Provides an "Version-Property" state detection strategy
 * for Spring to detect whether an entity is new or not.
 *
 * The strategy also provides for optimistic locking
 * and allows a non-nullable [id], so we find it preferable
 * to implementing org.springframework.data.domain.Persistable.
 *
 * See https://docs.spring.io/spring-data/jpa/docs/current-SNAPSHOT/reference/html/#jpa.entity-persistence.saving-entites.strategies
 */
@MappedSuperclass
abstract class AbstractBaseEntity(id: UUID? = null) : UuidIdEntity {

    @Id @Column(name = "id", length = 16, unique = true, nullable = false)
    override var id: UUID = id ?: UUID.randomUUID()

    @Version
    var version: Long? = null

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            !AbstractBaseEntity::class.isInstance(other) -> false
            else -> {
                other as AbstractBaseEntity
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
