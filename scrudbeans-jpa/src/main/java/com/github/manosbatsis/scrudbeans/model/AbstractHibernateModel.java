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

import com.github.manosbatsis.scrudbeans.validation.Unique;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Formula;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;

/**
 * Abstract entity class with basic auditing and unique constraints validation
 *
 * @param <PK> The id Serializable
 */
@MappedSuperclass
@Unique
@EqualsAndHashCode
@ToString
public abstract class AbstractHibernateModel<PK extends Serializable> extends AbstractPersistableModel<PK> {
    @Formula(" true ")
	private boolean persisted = false;

    public AbstractHibernateModel() {
        super();
    }

	@Override
	public boolean isNew() {
		return !persisted;
	}
}
