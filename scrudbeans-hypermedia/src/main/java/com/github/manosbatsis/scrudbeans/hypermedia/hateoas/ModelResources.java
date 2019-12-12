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
package com.github.manosbatsis.scrudbeans.hypermedia.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.manosbatsis.scrudbeans.api.domain.Persistable;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.CollectionModel;

/**
 * Created by manos on 20/2/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModelResources<M extends Persistable> extends CollectionModel<ModelResource<M>> {


    /**
     * {@inheritDoc}
     */
    public ModelResources() {
    }

    /**
     * {@inheritDoc}
	 */
	public ModelResources(Iterable<ModelResource<M>> content, Link... links) {
		super(content, links);
	}

	/**
	 * {@inheritDoc}
	 */
	public ModelResources(Iterable<ModelResource<M>> content, Iterable<Link> links) {
		super(content, links);
	}
}
