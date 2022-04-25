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
 * extends AbstractEmbeddableQuintupleIdentifier<
 * User, String,
 * SomeInnerLeftType, String,
 * SomeMiddleType, String,
 * SomeInnerRightType, String,
 * User, String>
 * implements Serializable {
 *
 * @Override
 * public User buildLeft(Serializable left) {
 * return new User(left.toString());
 * }
 *
 * @Override
 * public User buildInnerLeft(Serializable innerLeft) {
 * return new SomeInnerLeftType(innerLeft.toString());
 * }
 *
 * @Override
 * public User buildMiddle(Serializable middle) {
 * return new SomeMiddleType(middle.toString());
 * }
 *
 * @Override
 * public User buildInnerRight(Serializable innerRight) {
 * return new SomeInnerRightType(innerRight.toString());
 * }
 *
 * @Override
 * public User buildRight(Serializable right) {
 * return new User(right.toString());
 * }
 * }
` *
 *
 * @param <L>   The type of the left relationship entity
 * @param <LPK> The type of the left relationship entity ID
 * @param <IL>   The type of the inner left relationship entity
 * @param <ILPK> The type of the inner left relationship entity ID
 * @param <M>   The type of the middle relationship entity
 * @param <MPK> The type of the middle relationship entity ID
 * @param <IR>   The type of the inner right relationship entity
 * @param <IRPK> The type of the inner right relationship entity ID
 * @param <R>   The type of the right relationship entity
 * @param <RPK> The type of the right relationship entity ID
 * @see EmbeddableCompositeIdentifier
 *
 * @see EmbeddableCompositeIdDeserializer
 *
 * @see EmbeddableCompositeIdSerializer
</RPK></R></IRPK></IR></MPK></M></ILPK></IL></LPK></L> */
@MappedSuperclass
@JsonSerialize(using = EmbeddableCompositeIdSerializer::class)
@JsonDeserialize(using = EmbeddableCompositeIdDeserializer::class)
abstract class AbstractEmbeddableQuintupleIdentifier<L, LPK : Serializable?, IL, ILPK : Serializable?, M, MPK : Serializable?, IR, IRPK : Serializable?, R, RPK : Serializable?> :
    Serializable, EmbeddableCompositeIdentifier {
    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "left_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var left: @NotNull L? = null
        private set

    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "inner_left_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var innerLeft: @NotNull IL? = null
        private set

    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "middle_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var middle: @NotNull M? = null
        private set

    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "inner_right_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var innerRight: @NotNull IR? = null
        private set

    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "right_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    var right: @NotNull R? = null
        private set

    constructor() {}
    constructor(value: @NotNull String?) {
        init(value)
    }

    abstract fun buildLeft(left: Serializable?): L
    abstract fun buildInnerLeft(innerLeft: Serializable?): IL
    abstract fun buildMiddle(middle: Serializable?): M
    abstract fun buildInnerRight(innerRight: Serializable?): IR
    abstract fun buildRight(right: Serializable?): R
    override fun hashCode(): Int {
        return HashCodeBuilder()
            .append(EntityUtil.idOrNEmpty(left))
            .append(EntityUtil.idOrNEmpty(innerLeft))
            .append(EntityUtil.idOrNEmpty(middle))
            .append(EntityUtil.idOrNEmpty(innerRight))
            .append(EntityUtil.idOrNEmpty(right)).toHashCode()
    }

    override fun equals(obj: Any?): Boolean {
        if (obj == null) {
            return false
        }
        return if (AbstractEmbeddableQuintupleIdentifier::class.java.isAssignableFrom(obj.javaClass)) {
            val other = obj as AbstractEmbeddableQuintupleIdentifier<*, *, *, *, *, *, *, *, *, *>
            EqualsBuilder()
                .append(EntityUtil.idOrNEmpty(left), EntityUtil.idOrNull(other.left))
                .append(EntityUtil.idOrNEmpty(innerLeft), EntityUtil.idOrNull(other.innerLeft))
                .append(EntityUtil.idOrNEmpty(middle), EntityUtil.idOrNull(other.middle))
                .append(EntityUtil.idOrNEmpty(innerRight), EntityUtil.idOrNull(other.innerRight))
                .append(EntityUtil.idOrNEmpty(right), EntityUtil.idOrNull(other.right)).isEquals
        } else {
            false
        }
    }

    override fun init(value: @NotNull String?) {
        val parts = value!!.split(SPLIT_CHAR.toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray()
        if (parts.size == 5 && StringUtils.isNoneBlank(parts[0], parts[1], parts[2], parts[3])) {
            left = buildLeft(parts[0])
            innerLeft = buildInnerLeft(parts[1])
            middle = buildMiddle(parts[2])
            innerRight = buildInnerRight(parts[3])
            right = buildRight(parts[4])
        } else {
            throw IllegalArgumentException("Given value must have five non-blank parts separated by '_'.")
        }
    }

    override fun toStringRepresentation(): String? {
        val left = EntityUtil.idOrNEmpty(left)
        val innerLeft = EntityUtil.idOrNEmpty(innerLeft)
        val middle = EntityUtil.idOrNEmpty(middle)
        val innerRight = EntityUtil.idOrNEmpty(innerRight)
        val right = EntityUtil.idOrNEmpty(right)
        val s = StringBuffer()
        if (StringUtils.isNotBlank(left)) {
            s.append(left)
        }
        s.append(SPLIT_CHAR)
        if (StringUtils.isNotBlank(innerLeft)) {
            s.append(innerLeft)
        }
        s.append(SPLIT_CHAR)
        if (StringUtils.isNotBlank(middle)) {
            s.append(middle)
        }
        s.append(SPLIT_CHAR)
        if (StringUtils.isNotBlank(innerRight)) {
            s.append(innerRight)
        }
        s.append(SPLIT_CHAR)
        if (StringUtils.isNotBlank(right)) {
            s.append(right)
        }
        val id = s.toString()
        return if (id.length > 3) id else null
    }



    fun setLeft(left: L) {
        this.left = left
    }

    fun setInnerLeft(innerLeft: IL) {
        this.innerLeft = innerLeft
    }

    fun setMiddle(middle: M) {
        this.middle = middle
    }

    fun setInnerRight(innerRight: IR) {
        this.innerRight = innerRight
    }

    fun setRight(right: R) {
        this.right = right
    }

    override fun toString(): String {
        return "${toStringRepresentation()}"
    }

    companion object {
        const val SPLIT_CHAR = "_"
    }
}