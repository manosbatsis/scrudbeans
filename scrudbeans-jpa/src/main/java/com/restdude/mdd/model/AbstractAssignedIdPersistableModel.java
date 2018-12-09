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

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Formula;

/**
 * Abstract base class for persistent entities with assigned id
 * @param <PK> The id Serializable
 */
@MappedSuperclass
public abstract class AbstractAssignedIdPersistableModel<PK extends Serializable> extends AbstractPersistableModel<PK> {

	private static final long serialVersionUID = 4340156130534111231L;

	@Id
	private PK id;

	@Formula(" (id) ")
	private PK savedPk;

	public AbstractAssignedIdPersistableModel() {

	}

	public AbstractAssignedIdPersistableModel(PK id) {
		this.id = id;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public PK getId() {
		return id;
	}

	/**
	 *{@inheritDoc}
	 */
	@Override
	public void setId(PK id) {
		this.id = id;
	}


	private PK getSavedPk() {
		return savedPk;
	}

	/**
	 * @inheritDoc}
	 */
	@Override
	public boolean isNew() {
		return null == getSavedPk();
	}

	/**
	 * {@inheritDoc}
	 */
	public void preSave() {

	}
}