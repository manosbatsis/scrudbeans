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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NonNull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import org.springframework.util.Assert;

/**
 * An interface for with an almost complete default implementation of a JSON API Links object. Example:
 *
 * <pre>
 * {@code
 * import lombok.Getter;
 * import lombok.NonNull;
 * import com.github.manosbatsis.scrudbeans.hypermedia.jsonapi.JsonApiLinksContainer
 *
 *  public class MyClass implements JsonApiLinksContainer {
 *      @Getter @Setter private Map<String, JsonApiLink> links;
 *  }
 *
 * }
 * </pre>
 */
public interface JsonApiLinksContainer extends Serializable {

	@JsonInclude(JsonInclude.Include.NON_NULL)
	public static class Link implements JsonApiLink {
		@Getter private final String href;

		@Getter @JsonIgnore private final String rel;

		@Getter private final Map<String, Serializable> meta;


		public Link(@NonNull String href, @NonNull String rel) {
			this(href, rel, null);
		}

		public Link(@NonNull String href, @NonNull String rel, Map<String, Serializable> meta) {
			Assert.hasText(href, "Parameter href cannot be empty");
			Assert.hasText(rel, "Parameter rel cannot be empty");
			this.href = href;
			this.rel = rel;
			this.meta = meta;
		}

		@Override
		public Map<String, Serializable> getMeta() {
			return this.meta;
		}
	}


	Map<String, JsonApiLink> getLinks();

	void setLinks(Map<String, JsonApiLink> links);

	default void add(JsonApiLink link) {
		Map<String, JsonApiLink> _links = this.getLinks();
		if (_links == null) {
			this.setLinks(_links = new HashMap<String, JsonApiLink>());
		}
		_links.put(link.getRel(), link);
	}

	default void add(Collection<JsonApiLink> links) {
		Map<String, JsonApiLink> _links = this.getLinks();
		if (CollectionUtils.isNotEmpty(links) && _links == null) {
			this.setLinks(_links = new HashMap<String, JsonApiLink>());
		}
		for (JsonApiLink link : links) {
			_links.put(link.getRel(), link);
		}
	}

	default void add(String rel, String href) {
		this.add(rel, href, null);
	}

	default void add(@NonNull String href, @NonNull String rel, Map<String, Serializable> meta) {
		this.add(new Link(rel, href, meta));
	}

	default boolean hasLinks() {
		return MapUtils.isNotEmpty(this.getLinks());
	}

	default boolean hasLink(String rel) {
		return hasLinks() && this.getLinks().containsKey(rel);

	}

}
