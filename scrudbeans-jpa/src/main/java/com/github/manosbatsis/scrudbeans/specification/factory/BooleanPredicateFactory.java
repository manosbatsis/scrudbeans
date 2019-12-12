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
package com.github.manosbatsis.scrudbeans.specification.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BooleanPredicateFactory extends AbstractPredicateFactory<Boolean> {


	private static final Logger LOGGER = LoggerFactory.getLogger(BooleanPredicateFactory.class);

	public BooleanPredicateFactory() {
	}


	@Override
	public Class<?> getValueType() {
		return Boolean.class;
	}
}