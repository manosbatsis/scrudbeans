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
package com.github.manosbatsis.scrudbeans.hypermedia.hateoas;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;

import java.util.Collection;
import java.util.Map;

/**
 * Created by manos on 21/2/2017.
 */
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class PagedModelResources<M> extends PagedModel<ModelResource<M>> {


    @Getter
    private Map<String, String[]> urlParameters;

    public PagedModelResources() {
    }

    /**
     * Creates a new {@link PagedModel} from the given content, {@link PageMetadata}, URL parameters and {@link Link}s (optional).
     *
     * @param content            must not be {@literal null}.
     * @param paginationMetadata the pagination information
     * @param urlParameters      the original URL parameters used to construct the page
     * @param links
     */
	protected PagedModelResources(Collection<ModelResource<M>> content, PageMetadata paginationMetadata, Map<String, String[]> urlParameters, Link... links) {
		super(content, paginationMetadata, links);
		this.urlParameters = urlParameters;
    }

    /**
     * Creates a new {@link PagedModel} from the given content, {@link PageMetadata}, URL parameters and {@link Link}s (optional).
     *
     * @param content must not be {@literal null}.
     * @param paginationMetadata the pagination information
     * @param urlParameters the original URL parameters used to construct the page
	 * @param links
	 */
	public PagedModelResources(Collection<ModelResource<M>> content, PageMetadata paginationMetadata, Map<String, String[]> urlParameters, Iterable<Link> links) {
		super(content, paginationMetadata, links);
		this.urlParameters = urlParameters;
	}
}
