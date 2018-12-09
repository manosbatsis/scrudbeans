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

import javax.validation.constraints.NotNull;

import com.restdude.domain.PersistableModel;

/**
 * Created by manos on 25/12/2016.
 */
public interface EmbeddableManyToManyIdentifier<L extends PersistableModel<LPK>, LPK extends Serializable, R extends PersistableModel<RPK>, RPK extends Serializable> {
	void init(@NotNull String value);

	void init(LPK left, @NotNull RPK right);

	void init(L left, @NotNull R right);

	String toStringRepresentation();

	L getLeft();

	void setLeft(L left);

	R getRight();

	void setRight(R right);
}
