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
package com.restdude.hypermedia.jsonapi;

import java.io.Serializable;

/**
 * A Document that may contain up to a single Resource according to JSON API 1.1
 *
 * @param <D> the JSON API Resource type
 * @param <A> the JSON API Resource attributes object type
 * @param <ID> the JSON API Resource id  type
 *
 */
public interface JsonApiResourceDocument<D extends JsonApiResource<A, ID>, A extends Serializable, ID extends Serializable> extends JsonApiDocument<D> {

}
