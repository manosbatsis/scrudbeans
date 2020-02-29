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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiLink;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiResourceIdentifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.io.Serializable;
import java.util.Map;

/**
 * A model wrapper that allows serializing as a EntityModel according to JSON API  1.1
 *
 * @param <PK> the JSON API EntityModel model key type
 * @see <a href="http://jsonapi.org/format/#document-resource-objects">JSON API CollectionModel</a>
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "type", "meta"})
public class SimpleModelResourceIdentifier<T, PK extends Serializable> implements JsonApiResourceIdentifier<PK> {

    @JsonProperty("id")
    private PK identifier;

    private String type;

    @Getter
    @Setter
    private Map<String, JsonApiLink> links;

    private Map<String, Serializable> meta;

	protected SimpleModelResourceIdentifier() {
	}

	public SimpleModelResourceIdentifier(@NonNull T attributesModel, @NonNull String type) {
        IdentifierAdapter<T, PK> idAdapter =
                (IdentifierAdapter<T, PK>) IdentifierAdaptersRegistry.getAdapterForClass(attributesModel.getClass());
        this.identifier = idAdapter.readId(attributesModel);
        this.type = type;
    }

	@JsonCreator
	public SimpleModelResourceIdentifier(
			@JsonProperty("id") PK identifier,
			@NonNull @JsonProperty("type") String type,
			@JsonProperty("meta") Map<String, Serializable> meta) {
		this.identifier = identifier;
		this.meta = meta;
		this.type = type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PK getIdentifier() {
		return identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setIdentifier(PK identifier) {
		this.identifier = identifier;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getType() {
		return type;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, Serializable> getMeta() {
		return this.meta;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setMeta(Map<String, Serializable> meta) {
		this.meta = meta;
	}

	public void setType(String type) {
		this.type = type;
	}

}
