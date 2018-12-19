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
package com.github.manosbatsis.scrudbeans.jpa.service;


import java.io.Serializable;

import com.github.manosbatsis.scrudbeans.api.mdd.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractAssignedIdPersistableModel;

import org.springframework.security.access.method.P;
import org.springframework.stereotype.Service;

/**
 * Provides SCRUD and utility operations for {@link T} model
 * @author manos
 *
 * @param <T> the entity type
 * @param <PK> the entity PK type
 */
@Deprecated
@Service
public interface AbstractAssignedIdModelService<T extends AbstractAssignedIdPersistableModel<PK>, PK extends Serializable>
		extends PersistableModelService<T, PK> {

	/**
	 * Return the entity matching the PK of the given resource if any, or the newly persisted instance otherwise
	 * @param resource
	 * @return
	 */
	public T findOrCreate(@P("resource") T resource);

}