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
package com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceCollectionDocument;
import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;
import java.util.Collection;

/**
 * {@value #CLASS_DESCRIPTION}
 *
 * @param <T>  the JSON API EntityModel model type
 * @param <PK> the JSON API EntityModel model key type
 * @see SimpleModelResourceDocument
 * @see <a href="http://jsonapi.org/format/upcoming/#document-structure">JSON API Documents</a>
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "Collection Document (JSON-API)", description = SimpleModelResourceDocument.CLASS_DESCRIPTION)
public class SimpleModelResourceCollectionDocument<T, PK extends Serializable>
        extends AbstractJsonApiDocument<Collection<JsonApiModelResource<T, PK>>>
        implements JsonApiModelResourceCollectionDocument<T, PK> {

    public static final String CLASS_DESCRIPTION = "A JSON API Document that may contain multiple model-based Resources";

    private Collection<JsonApiModelResource<T, PK>> data;

    public SimpleModelResourceCollectionDocument() {
        super();
    }

	public SimpleModelResourceCollectionDocument(Collection<JsonApiModelResource<T, PK>> data) {
		super(data);
	}

}
