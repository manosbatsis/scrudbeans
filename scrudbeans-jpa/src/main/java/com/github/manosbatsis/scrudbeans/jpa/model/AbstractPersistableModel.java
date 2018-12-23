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

import java.io.Serializable;

import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlRootElement;

import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import com.github.manosbatsis.scrudbeans.jpa.validation.Unique;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Abstract entity class with basic auditing, unique constraints validation and authorization settings.
 * @param <PK> The id Serializable
 */
@XmlRootElement
@MappedSuperclass
@Unique
public abstract class AbstractPersistableModel<PK extends Serializable> implements PersistableModel<PK> {
	private static final long serialVersionUID = -6009587976502456848L;

	public AbstractPersistableModel() {
		super();
	}

	public AbstractPersistableModel(PK id) {
		this.setId(id);
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append(PK_FIELD_NAME, this.getId()).toString();
	}

	/**
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (null == obj) {
			return false;
		}

		if (this == obj) {
			return true;
		}
		if (!AbstractPersistableModel.class.isAssignableFrom(obj.getClass())) {
			return false;
		}
		AbstractPersistableModel other = (AbstractPersistableModel) obj;
		return new EqualsBuilder()
				.append(this.getId(), this.getId())
				.isEquals();
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
				.append(this.getId())
				.toHashCode();
	}

}