package com.github.manosbatsis.scrudbeans.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.OffsetDateTime
import java.util.*
import javax.persistence.*


@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class AbstractAuditableEntity(
    id: UUID? = null,

    @CreatedDate
    @Column(name = "created", nullable = false)
    var created: OffsetDateTime? = null,

    @LastModifiedDate
    @Column(name = "updated", nullable = false)
    var updated: OffsetDateTime? = null

): AbstractBaseEntity(id){
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
abstract class AbstractBaseEntity(id: UUID? = null): UuidIdEntity {

    @Id @Column(name = "id", length = 16, unique = true, nullable = false)
    override var id: UUID = id ?: UUID.randomUUID()

    @Version
    var version: Long? = null

    @Transient
    @JsonProperty("@type")
    private val clazz = "jpa"

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is AbstractBaseEntity -> false
            else -> id == other.id
        }
    }

    override fun hashCode(): Int = id.hashCode()
}