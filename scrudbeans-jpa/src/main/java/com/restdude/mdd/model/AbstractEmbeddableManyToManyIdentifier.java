/**
 *
 * Restdude
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
package com.restdude.mdd.model;


import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.restdude.domain.PersistableModel;
import com.restdude.mdd.binding.EmbeddableManyToManyIdDeserializer;
import com.restdude.mdd.binding.EmbeddableManyToManyIdSerializer;
import com.restdude.mdd.binding.StringToEmbeddableManyToManyIdConverterFactory;
import com.restdude.mdd.util.EntityUtil;
import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class for {@link Embeddable}s used as composite IDs in entities based on a many-to-many table.
 * Conveniently mapped from/to JSON via {@link EmbeddableManyToManyIdSerializer},  {@link EmbeddableManyToManyIdDeserializer} and  {@link StringToEmbeddableManyToManyIdConverterFactory}
 *
 * @param <L>   The type of the left MenyToOne relationship entity
 * @param <LPK> The type of the left MenyToOne relationship entity ID
 * @param <R>   The type of the right MenyToOne relationship entity
 * @param <RPK> The type of the right MenyToOne relationship entity ID
 * @see EmbeddableManyToManyIdDeserializer
 * @see EmbeddableManyToManyIdSerializer
 * @see StringToEmbeddableManyToManyIdConverterFactory
 */
@MappedSuperclass
@JsonSerialize(using = EmbeddableManyToManyIdSerializer.class)
@JsonDeserialize(using = EmbeddableManyToManyIdDeserializer.class)
public abstract class AbstractEmbeddableManyToManyIdentifier<L extends PersistableModel<LPK>, LPK extends Serializable, R extends PersistableModel<RPK>, RPK extends Serializable> implements Serializable, EmbeddableManyToManyIdentifier<L, LPK, R, RPK> {


	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractEmbeddableManyToManyIdentifier.class);

	public static final String SPLIT_CHAR = "_";


	@NotNull
	@ApiModelProperty(required = true, example = "{id: '[id]'}")
	@JoinColumn(name = "owner_id", nullable = false, updatable = false)
	@ManyToOne(optional = false)
	private L left;

	@NotNull
	@ApiModelProperty(required = true, example = "{id: '[id]'}")
	@JoinColumn(name = "friend_id", nullable = false, updatable = false)
	@ManyToOne(optional = false)
	private R right;

	public AbstractEmbeddableManyToManyIdentifier() {
	}

	public AbstractEmbeddableManyToManyIdentifier(@NotNull String value) {
		init(value);
	}

	public AbstractEmbeddableManyToManyIdentifier(LPK left, @NotNull RPK right) {
		init(left, right);
	}


	public AbstractEmbeddableManyToManyIdentifier(L left, @NotNull R right) {
		init(left, right);
	}

	public abstract L buildLeft(Serializable left);

	public abstract R buildRight(Serializable right);


	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(EntityUtil.idOrNull(this.getLeft())).append(EntityUtil.idOrNull(this.getRight())).toHashCode();
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (AbstractEmbeddableManyToManyIdentifier.class.isAssignableFrom(obj.getClass())) {
			final AbstractEmbeddableManyToManyIdentifier other = (AbstractEmbeddableManyToManyIdentifier) obj;
			return new EqualsBuilder().append(EntityUtil.idOrNull(this.getLeft()), EntityUtil.idOrNull(other.getLeft()))
					.append(EntityUtil.idOrNull(this.getRight()), EntityUtil.idOrNull(other.getRight())).isEquals();
		}
		else {
			return false;
		}
	}

	@Override
	public void init(@NotNull String value) {
		String[] parts = value.split(SPLIT_CHAR);
		if (parts.length == 2) {
			this.left = this.buildLeft(parts[0]);
			this.right = this.buildRight(parts[1]);
		}
		else if (parts.length == 1) {
			this.right = this.buildRight(parts[0]);
		}
	}

	@Override
	public void init(LPK left, @NotNull RPK right) {
		if (left != null) {
			this.left = buildLeft(left);
		}
		this.right = buildRight(right);
	}

	@Override
	public void init(L left, @NotNull R right) {
		if (left != null) {
			this.left = left;
		}
		this.right = right;
	}

	@Override
	public String toStringRepresentation() {

		String sender = EntityUtil.idOrNEmpty(this.getLeft());
		String recipient = EntityUtil.idOrNEmpty(this.getRight());

		StringBuffer s = new StringBuffer(sender);
		if (StringUtils.isNoneBlank(sender, recipient)) {
			s.append(SPLIT_CHAR);
		}
		s.append(recipient);

		String id = s.toString();

		return StringUtils.isNotBlank(id) ? id : null;

	}

	@Override
	public String toString() {
		return this.toStringRepresentation();
	}


	@Override
	public L getLeft() {
		return left;
	}

	@Override
	public void setLeft(L left) {
		this.left = left;
	}

	@Override
	public R getRight() {
		return right;
	}

	@Override
	public void setRight(R right) {
		this.right = right;
	}

}