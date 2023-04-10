package com.github.manosbatsis.scrudbeans.model

import jakarta.persistence.Column
import jakarta.persistence.EntityListeners
import jakarta.persistence.MappedSuperclass
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

/**
 * An auditable base class that provides a "Version-Property" state
 * detection strategy for Spring to detect whether an entity is new or not.
 *
 * The strategy also provides for optimistic locking
 * and allows a non-nullable [id], so we find it preferable
 * compared to alternatives, i.e. implementing [org.springframework.data.domain.Persistable].
 *
 * See https://docs.spring.io/spring-data/jpa/docs/current-SNAPSHOT/reference/html/#jpa.entity-persistence.saving-entites.strategies
 */
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class AbstractAuditableVersionedEntity<ID>(
    version: Long? = null,

    @CreatedDate
    @Column(name = "created", nullable = false)
    var created: LocalDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    var updated: LocalDateTime? = null,
) : AbstractVersionedEntity<ID>(version) {

    override fun equals(other: Any?): Boolean {
        if (other == null) return false
        if (other === this) return true
        if (!AbstractAuditableVersionedEntity::class.isInstance(other)) return false
        other as AbstractAuditableVersionedEntity<ID>
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
