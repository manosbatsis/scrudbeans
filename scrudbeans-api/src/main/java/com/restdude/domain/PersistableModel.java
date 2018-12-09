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
package com.restdude.domain;

import java.io.Serializable;

/**
 * Base interface for persistable entity models
 * @param <PK> The primary key type, Serializable
 */
public interface PersistableModel<PK extends Serializable> extends Model<PK> {

	/**
	 * Equivalent of a method annotated with @{@link javax.persistence.PrePersist} and/or
	 *
	 * @{@link javax.persistence.PreUpdate}, only applied before validation
	 */
	void preSave();


	/**
	 * Equivalent of {@link }org.springframework.data.domain.Persistable#isNew()}
	 */
	boolean isNew();

}