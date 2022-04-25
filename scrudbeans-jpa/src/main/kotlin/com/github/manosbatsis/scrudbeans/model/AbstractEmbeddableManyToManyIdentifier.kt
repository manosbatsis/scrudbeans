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
package com.github.manosbatsis.scrudbeans.model

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.annotation.JsonSerialize
import com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier
import com.github.manosbatsis.scrudbeans.binding.EmbeddableCompositeIdDeserializer
import com.github.manosbatsis.scrudbeans.binding.EmbeddableCompositeIdSerializer
import com.github.manosbatsis.scrudbeans.util.EntityUtil
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import java.io.Serializable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.MappedSuperclass
import javax.validation.constraints.NotNull

/**
 * A base class for [Embeddable]s used as composite IDs in model based on a many-to-many table.
 * Conveniently mapped from/to JSON via [EmbeddableCompositeIdSerializer],
 * [EmbeddableCompositeIdDeserializer]. Example:
 *
 * `
 * @Embeddable
 * public class FriendshipIdentifier
 * extends AbstractEmbeddableManyToManyIdentifier<User></User>, String, User, String>
 * implements Serializable {
 *
 * @Override
 * public User buildLeft(Serializable left) {
 * return new User(left.toString());
 * }
 *
 * @Override
 * public User buildRight(Serializable right) {
 * return new User(right.toString());
 * }
 * }
` *
 *
 * @param <L>   The type of the left MenyToOne relationship entity
 * @param <LPK> The type of the left MenyToOne relationship entity ID
 * @param <R>   The type of the right MenyToOne relationship entity
 * @param <RPK> The type of the right MenyToOne relationship entity ID
 * @see EmbeddableCompositeIdentifier
 *
 * @see EmbeddableCompositeIdDeserializer
 *
 * @see EmbeddableCompositeIdSerializer
</RPK></R></LPK></L> */
@MappedSuperclass
@JsonSerialize(using = EmbeddableCompositeIdSerializer::class)
@JsonDeserialize(using = EmbeddableCompositeIdDeserializer::class)
abstract class AbstractEmbeddableManyToManyIdentifier<L, LPK : Serializable?, R, RPK : Serializable?> : Serializable,
    EmbeddableCompositeIdentifier {
    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "left_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var left: @NotNull L? = null

    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "right_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var right: @NotNull R? = null

    constructor() {}
    constructor(value: @NotNull String) {
        init(value)
    }

    abstract fun buildLeft(left: Serializable?): L
    abstract fun buildRight(right: Serializable?): R
    override fun hashCode(): Int {
        return HashCodeBuilder().append(EntityUtil.idOrNEmpty(left)).append(EntityUtil.idOrNEmpty(right)).toHashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        return if (AbstractEmbeddableManyToManyIdentifier::class.java.isAssignableFrom(obj.javaClass)) {
            val other = obj as AbstractEmbeddableManyToManyIdentifier<*, *, *, *>
            EqualsBuilder().append(
                EntityUtil.idOrNEmpty(left), EntityUtil.idOrNull(
                    other.left
                )
            )
                .append(EntityUtil.idOrNEmpty(right), EntityUtil.idOrNull(other.right)).isEquals
        } else {
            false
        }
    }

    override fun init(value: @NotNull String) {
        val parts = value.split(SPLIT_CHAR.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (parts.size == 2) {
            left = buildLeft(parts[0])
            right = buildRight(parts[1])
        } else if (parts.size == 1) {
            right = buildRight(parts[0])
        }
    }

    override fun toStringRepresentation(): String? {
        val sender = EntityUtil.idOrNEmpty(left)
        val recipient = EntityUtil.idOrNEmpty(right)
        val s = StringBuffer(sender)
        if (StringUtils.isNoneBlank(sender, recipient)) {
            s.append(SPLIT_CHAR)
        }
        s.append(recipient)
        val id = s.toString()
        return if (StringUtils.isNotBlank(id)) id else null
    }

    override fun toString(): String {
        return "${toStringRepresentation()}"
    }

    companion object {
        const val SPLIT_CHAR = "_"
    }
}