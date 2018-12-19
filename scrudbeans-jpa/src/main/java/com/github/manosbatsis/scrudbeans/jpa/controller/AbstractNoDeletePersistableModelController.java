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
package com.github.manosbatsis.scrudbeans.jpa.controller;

import java.io.Serializable;
import java.util.List;

import com.github.manosbatsis.scrudbeans.api.domain.PersistableModel;
import com.github.manosbatsis.scrudbeans.api.mdd.service.PersistableModelService;
import com.github.manosbatsis.scrudbeans.common.exception.NotImplementedException;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Base class for model controllers not allowing HTTP DELETE
 * operations.
 */
public abstract class AbstractNoDeletePersistableModelController<T extends PersistableModel<PK>, PK extends Serializable, S extends PersistableModelService<T, PK>>
		extends AbstractPersistableModelController<T, PK, S> {

	private static final Logger LOGGER = LoggerFactory.getLogger(AbstractNoDeletePersistableModelController.class);


	@Override
	public void delete(@ApiParam(name = "id", required = true, value = "string") @PathVariable PK id) {
		throw new NotImplementedException("Method is unsupported.");
	}

	@Override
	public void deleteAll() {
		throw new NotImplementedException("Method is unsupported.");
	}

	// TODO
	@ApiOperation(hidden = true, value = "Delete an uploaded file")
	@RequestMapping(value = "{subjectId}/uploads/{propertyName}/{id}", method = RequestMethod.DELETE)
	public List deleteById(@PathVariable String subjectId, @PathVariable String propertyName, @PathVariable String id) {
		throw new NotImplementedException("Method is unsupported.");
	}

	@RequestMapping(value = "{subjectId}/metadata/{predicate}", method = RequestMethod.DELETE)
	@ApiOperation(hidden = true, value = "Remove metadatum")
	public void removeMetadatum(@PathVariable PK subjectId, @PathVariable String predicate) {
		throw new NotImplementedException("Method is unsupported.");
	}
}
