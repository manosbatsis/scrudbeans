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
package com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.support;

import java.io.Serializable;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.github.manosbatsis.scrudbeans.api.domain.Model;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiLink;
import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiResourceIdentifier;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

/**
 * A model wrapper that allows serializing as a Resource according to JSON API  1.1
 *
 * @see <a href="http://jsonapi.org/format/#document-resource-objects">JSON API Resources</a>
 *
 * @param <PK> the JSON API Resource model key type
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"id", "type", "meta"})
public class SimpleModelResourceIdentifier<T extends Model<PK>, PK extends Serializable> implements JsonApiResourceIdentifier<PK> {

	@JsonProperty("id")
	private PK identifier;

	private String type;

	@Getter @Setter
	private Map<String, JsonApiLink> links;

	private Map<String, Serializable> meta;

	protected SimpleModelResourceIdentifier() {
	}

	public SimpleModelResourceIdentifier(@NonNull T attributesModel, @NonNull String type) {
		this.identifier = attributesModel.getId();
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
