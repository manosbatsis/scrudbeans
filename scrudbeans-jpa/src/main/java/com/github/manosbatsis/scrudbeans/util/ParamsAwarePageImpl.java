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
package com.github.manosbatsis.scrudbeans.util;


import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.manosbatsis.scrudbeans.api.util.ParamsAwarePage;
import lombok.NonNull;

import org.springframework.data.domain.Pageable;

/**
 *
 * Extends spring's ParamsAwarePageImpl to add the HTTP query string parameters and a JsonCreator to makes it easy for Jackson to use for de-serialization.
 *
 * @param <T>
 */
public class ParamsAwarePageImpl<T> extends org.springframework.data.domain.PageImpl<T> implements ParamsAwarePage<T> {

	private Map<String, String[]> parameters;

	/**
	 * {@link JsonCreator} that creates a new {@link ParamsAwarePageImpl} with the given content.
	 *
	 * @param content must not be {@literal null}.
	 */
	@JsonCreator
	public ParamsAwarePageImpl(@NonNull @JsonProperty("content") List<T> content) {
		super(content);
	}

	/**
	 * Constructor used by MDD components
	 *
	 * @param parameters the HTTP URL parameters that was used to retrieve the page content, must not be {@literal null}.
	 * @param content the content of this page, must not be {@literal null}.
	 * @param pageable the paging information, can be {@literal null}.
	 * @param total the total amount of items available. The total might be adapted considering the length of the content
	 *          given, if it is going to be the content of the last page. This is in place to mitigate inconsistencies
	 */
	public ParamsAwarePageImpl(@NonNull Map<String, String[]> parameters, List<T> content, Pageable pageable, long total) {
		super(content, pageable, total);
		this.parameters = parameters;
	}

	@Override
	public Map<String, String[]> getParameters() {
		return parameters;
	}

	// TODO
	//@Override
	//public List<Link> buildLinks(HttpServletRequest request) {
	//	return null;
	//}

	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}
}