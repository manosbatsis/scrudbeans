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
package com.github.manosbatsis.scrudbeans.hypermedia.jsonapi;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * A Document according to JSON API 1.1
 *
 * @param <D> the JSON API Document data type\
 * @see <a href="http://jsonapi.org/format/upcoming/#document-structure">JSON API Resources</a>
 *
 */
@JsonPropertyOrder({"data", "errors", "meta", "jsonapi", "links", "included"})
public interface JsonApiDocument<D extends Object> extends JsonApiLinksContainer {

	/**
	 * Get the Document data, i.e. embedded resource(s)
	 * @return
	 */
	D getData();

	/**
	 * Set the Document data, i.e. embedded resource(s)
	 * @return
	 */
	void setData(D data);

	/**
	 * Get the included resources
	 * @return
	 */
	Collection<JsonApiResource> getIncluded();

	/**
	 * Get the included resources
	 * @return
	 */
	void setIncluded(Collection<JsonApiResource> included);
// TODO
//    /**
//     * Get the errors resulting from processing the request
//     * @return
//     */
//    Collection<ErrorModel> getErrors();
//
//    /**
//     * Set the errors resulting from processing the request
//     * @return
//     */
//    void setErrors(Collection<ErrorModel> errors);

	/**
	 * Get the associated metadata
	 * @return
	 */
	Map<String, Serializable> getMeta();

	/**
	 * Set the associated metadata
	 * @return
	 */
	void setMeta(Map<String, Serializable> meta);

}
