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
package com.restdude.hypermedia.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.restdude.domain.Model;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;

/**
 * Created by manos on 20/2/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ModelResource<T extends Model> extends Resource<T> {

	/**
	 * Equivalent to JSON API document type
	 */
	@Getter @Setter
	String pathFragment;

	/**
	 * {@inheritDoc}
	 */
	public ModelResource(@NonNull String pathFragment, @NonNull T content, Link... links) {
		super(content, links);
		this.pathFragment = pathFragment;
	}

	/**
	 * {@inheritDoc}
	 */
	public ModelResource(@NonNull String pathFragment, @NonNull T content, Iterable<Link> links) {
		super(content, links);
		this.pathFragment = pathFragment;
	}

}
