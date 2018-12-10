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
package com.github.manosbatsis.scrudbeans.hypermedia.jsonapi;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support.SimpleModelResource;

/**
 * A Resource as defined in JSON API 1.1. Deserialized as a @link SimpleModelResource} by default
 *
 * @param <A> the attributes object type
 */
@JsonDeserialize(as = SimpleModelResource.class)
public interface JsonApiResource<A extends Serializable, ID extends Serializable> extends JsonApiLinksContainer {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	@JsonIgnoreProperties(value = {"id", "links"})
	A getAttributes();

	@JsonProperty
	void setAttributes(A attributes);
}
