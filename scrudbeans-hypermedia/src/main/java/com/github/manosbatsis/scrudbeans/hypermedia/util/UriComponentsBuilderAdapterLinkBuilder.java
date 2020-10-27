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

import org.springframework.hateoas.Affordance;
import org.springframework.hateoas.server.LinkBuilder;
import org.springframework.hateoas.server.core.LinkBuilderSupport;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

/**
 * An implementation of {@link LinkBuilder} that can be used on top of a {@link UriComponentsBuilder}
 */
public class UriComponentsBuilderAdapterLinkBuilder extends LinkBuilderSupport<UriComponentsBuilderAdapterLinkBuilder> {

    /**
     * Creates a new {@link UriComponentsBuilderAdapterLinkBuilder} using the given {@link UriComponentsBuilder}.
     *
     * @param components must not be {@literal null}.
     */
    public UriComponentsBuilderAdapterLinkBuilder(UriComponents components, List<Affordance> affordances) {
        super(components, affordances);
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
     * @param components will never be {@literal null}.
     * @return
     */
    @Override
    protected UriComponentsBuilderAdapterLinkBuilder createNewInstance(UriComponents components, List<Affordance> affordances) {
        return new UriComponentsBuilderAdapterLinkBuilder(components, affordances);
	}

}
