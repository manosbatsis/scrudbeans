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


import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResource;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiModelResourceDocument;
import io.swagger.annotations.ApiModel;

/**
 * {@value #CLASS_DESCRIPTION}
 *
 * @see SimpleModelResourceCollectionDocument
 * @see <a href="http://jsonapi.org/format/upcoming/#document-structure">JSON API Documents</a>
 *
 * @param <T> the JSON API Resource model type
 * @param <PK> the JSON API Resource model key type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@ApiModel(value = "Document (JSON-API)", description = SimpleModelResourceDocument.CLASS_DESCRIPTION)
public class SimpleModelResourceDocument<T extends Model<PK>, PK extends Serializable> extends AbstractJsonApiDocument<JsonApiModelResource<T, PK>> implements JsonApiModelResourceDocument<T, PK> {

	public static final String CLASS_DESCRIPTION = "A Document that may contain up to a single Resource model (as defined by JSON API 1.1)";

	private SimpleModelResource<T, PK> data;

	public SimpleModelResourceDocument() {
		super();
	}

	public SimpleModelResourceDocument(SimpleModelResource data) {
		super(data);
	}

}
