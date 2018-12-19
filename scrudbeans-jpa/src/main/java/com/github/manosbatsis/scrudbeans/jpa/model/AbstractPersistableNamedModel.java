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
package com.github.manosbatsis.scrudbeans.jpa.model;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;

import com.github.manosbatsis.scrudbeans.jpa.domain.AbstractBasicAuditedModel;
import org.apache.commons.lang3.builder.EqualsBuilder;


/**
 * A base class for value-like resource model: files, folders, categories etc.
 */
@MappedSuperclass
public abstract class AbstractPersistableNamedModel extends AbstractBasicAuditedModel {


	private static final long serialVersionUID = 1L;

	@Column(name = "name", nullable = false)
	private String name;

	public AbstractPersistableNamedModel() {
		super();
	}

	public AbstractPersistableNamedModel(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!AbstractPersistableNamedModel.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		AbstractPersistableNamedModel other = (AbstractPersistableNamedModel) obj;
		EqualsBuilder builder = new EqualsBuilder();
		builder.appendSuper(super.equals(obj));
		builder.append(this.getName(), other.getName());
		return builder.isEquals();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}