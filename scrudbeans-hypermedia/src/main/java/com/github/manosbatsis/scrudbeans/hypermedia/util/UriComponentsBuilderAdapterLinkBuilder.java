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
package com.github.manosbatsis.scrudbeans.hypermedia.util;

import org.springframework.hateoas.LinkBuilder;
import org.springframework.hateoas.core.LinkBuilderSupport;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * An implementation of {@link LinkBuilder} that can be used on top of a {@link UriComponentsBuilder}
 */
public class UriComponentsBuilderAdapterLinkBuilder extends LinkBuilderSupport<UriComponentsBuilderAdapterLinkBuilder> {

	/**
	 * Creates a new {@link UriComponentsBuilderAdapterLinkBuilder} using the given {@link UriComponentsBuilder}.
	 *
	 * @param builder must not be {@literal null}.
	 */
	public UriComponentsBuilderAdapterLinkBuilder(UriComponentsBuilder builder) {
		super(builder);
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	protected UriComponentsBuilderAdapterLinkBuilder getThis() {
		return this;
	}

	/**
	 * Creates a new instance
	 *
	 * @param builder will never be {@literal null}.
	 * @return
	 */
	@Override
	protected UriComponentsBuilderAdapterLinkBuilder createNewInstance(UriComponentsBuilder builder) {
		return new UriComponentsBuilderAdapterLinkBuilder(builder);
	}
}
