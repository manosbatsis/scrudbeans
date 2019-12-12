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
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.model;


import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier;
import com.github.manosbatsis.scrudbeans.binding.EmbeddableCompositeIdDeserializer;
import com.github.manosbatsis.scrudbeans.binding.EmbeddableCompositeIdSerializer;
import com.github.manosbatsis.scrudbeans.binding.StringToEmbeddableCompositeIdConverterFactory;
import com.github.manosbatsis.scrudbeans.util.EntityUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for {@link Embeddable}s used as composite IDs in model based on a many-to-many table.
 * Conveniently mapped from/to JSON via {@link EmbeddableCompositeIdSerializer},
 * {@link EmbeddableCompositeIdDeserializer} and
 * {@link StringToEmbeddableCompositeIdConverterFactory}. Example:
 *
 * <code>
 * @Embeddable
 * public class FriendshipIdentifier
 * 		extends AbstractEmbeddableTripleIdentifier<User, String, SomeMiddleType, String, User, String>
 * 		implements Serializable {
 *
 *     @Override
 *     public User buildLeft(Serializable left) {
 *         return new User(left.toString());
 *     }
 *
 *     @Override
 *     public SomeMiddleType buildMiddle(Serializable middle) {
 *         return new SomeMiddleType(middle.toString());
 *     }
 *
 *     @Override
 *     public User buildRight(Serializable right) {
 *         return new User(right.toString());
 *     }
 * }
 * </code>
 *
 * @param <L>   The type of the left relationship entity
 * @param <LPK> The type of the left relationship entity ID
 * @param <M>   The type of the middle relationship entity
 * @param <MPK> The type of the middle relationship entity ID
 * @param <R>   The type of the right relationship entity
 * @param <RPK> The type of the right relationship entity ID
 * @see EmbeddableCompositeIdentifier
 * @see EmbeddableCompositeIdDeserializer
 * @see EmbeddableCompositeIdSerializer
 * @see StringToEmbeddableCompositeIdConverterFactory
 */
@MappedSuperclass
@JsonSerialize(using = EmbeddableCompositeIdSerializer.class)
@JsonDeserialize(using = EmbeddableCompositeIdDeserializer.class)
public abstract class AbstractEmbeddableTripleIdentifier<
        L extends Persistable<LPK>, LPK extends Serializable,
        M extends Persistable<MPK>, MPK extends Serializable,
        R extends Persistable<RPK>, RPK extends Serializable>
		implements Serializable, EmbeddableCompositeIdentifier {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEmbeddableTripleIdentifier.class);

	public static final String SPLIT_CHAR = "_";


    @NotNull
    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "left_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    private L left;

    @NotNull
    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "middle_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    private M middle;

    @NotNull
    //@ApiModelProperty(required = true, example = "{\"id\": \"[id]\"}")
    @JoinColumn(name = "right_id", nullable = false, updatable = false)
    @ManyToOne(optional = false)
    private R right;

	public AbstractEmbeddableTripleIdentifier() {
	}

	public AbstractEmbeddableTripleIdentifier(@NotNull String value) {
		init(value);
	}

	public abstract L buildLeft(Serializable left);

	public abstract M buildMiddle(Serializable middle);

	public abstract R buildRight(Serializable right);


	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(EntityUtil.idOrNull(this.getLeft()))
				.append(EntityUtil.idOrNull(this.getMiddle()))
				.append(EntityUtil.idOrNull(this.getRight())).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (AbstractEmbeddableTripleIdentifier.class.isAssignableFrom(obj.getClass())) {
			final AbstractEmbeddableTripleIdentifier other = (AbstractEmbeddableTripleIdentifier) obj;
			return new EqualsBuilder()
					.append(EntityUtil.idOrNull(this.getLeft()), EntityUtil.idOrNull(other.getLeft()))
					.append(EntityUtil.idOrNull(this.getMiddle()), EntityUtil.idOrNull(other.getMiddle()))
					.append(EntityUtil.idOrNull(this.getRight()), EntityUtil.idOrNull(other.getRight())).isEquals();
		}
		else {
			return false;
		}
	}

	@Override
	public void init(@NotNull String value) {
		String[] parts = value.split(SPLIT_CHAR);
		if (parts.length == 3 && StringUtils.isNoneBlank(parts[0], parts[1], parts[2])) {
			this.left = this.buildLeft(parts[0]);
			this.middle = this.buildMiddle(parts[1]);
			this.right = this.buildRight(parts[2]);
		}
		else {
			throw new IllegalArgumentException("Given value must have three non-blank parts separated by '_'.");
		}
	}

	@Override
	public String toStringRepresentation() {

		String left = EntityUtil.idOrNEmpty(this.getLeft());
		String middle = EntityUtil.idOrNEmpty(this.getMiddle());
		String right = EntityUtil.idOrNEmpty(this.getRight());

		StringBuffer s = new StringBuffer();
		if (StringUtils.isNotBlank(left)) {
			s.append(left);
		}
		s.append(SPLIT_CHAR);
		if (StringUtils.isNotBlank(middle)) {
			s.append(middle);
		}
		s.append(SPLIT_CHAR);
		if (StringUtils.isNotBlank(right)) {
			s.append(right);
		}
		String id = s.toString();

		return id.length() > 2 ? id : null;

	}

	@Override
	public String toString() {
		return this.toStringRepresentation();
	}

	public L getLeft() {
		return left;
	}

	public void setLeft(L left) {
		this.left = left;
	}

	public M getMiddle() {
		return middle;
	}

	public void setMiddle(M middle) {
		this.middle = middle;
	}

	public R getRight() {
		return right;
	}

	public void setRight(R right) {
		this.right = right;
	}

}
