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
import java.util.Collection;

import com.restdude.domain.Model;

/**
 * A Document that may contain multiple model-based Resources according to JSON API 1.1
 *
 * @param <T> the JSON API Resource model type
 * @param <PK> the JSON API Resource model key type
 *
 */
public interface JsonApiModelResourceCollectionDocument<T extends Model<PK>, PK extends Serializable> extends JsonApiResourceCollectionDocument<Collection<JsonApiModelResource<T, PK>>, JsonApiModelResource<T, PK>> {

}
