/**
 *
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.api.domain

import java.util.*
import javax.persistence.Column
import javax.persistence.Id
import javax.persistence.MappedSuperclass
import javax.persistence.Version

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
abstract class AbstractBaseEntityWithUuidId(givenId: UUID? = null) {

    @Id @Column(name = "id", length = 16, unique = true, nullable = false)
    var id: UUID = givenId ?: UUID.randomUUID()

    @Version
    var version: Long? = null

    override fun equals(other: Any?): Boolean {
        return when {
            this === other -> true
            other == null -> false
            other !is AbstractBaseEntityWithUuidId -> false
            else -> id == other.id
        }
    }

    override fun hashCode(): Int = id.hashCode()
}