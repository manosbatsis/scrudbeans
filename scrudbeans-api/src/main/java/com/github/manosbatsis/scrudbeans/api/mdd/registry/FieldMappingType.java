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
package com.github.manosbatsis.scrudbeans.api.mdd.registry;

import lombok.Getter;

public enum FieldMappingType {
	NONE(false, false, true, false, false, false, false),
	ID(true, false, true, false, false, false, true),
	SIMPLE(false, false, false, false, false, false, true),
	VALUES(false, true, false, false, false, false, false),
	ONE_TO_ONE(false, false, false, false, true, false, false),
	ONE_TO_MANY(false, false, false, true, false, false, false),
	MANY_TO_ONE(false, false, false, false, true, false, false),
	MANY_TO_MANY(false, false, false, true, false, false, false),
	CALCULATED_NONE(false, false, true, false, false, true, false),
	CALCULATED_ID(true, false, false, false, false, true, true),
	CALCULATED_SIMPLE(false, false, false, false, false, true, true),
	CALCULATED_VALUES(false, true, false, false, false, true, false),
	CALCULATED_ONE_TO_ONE(false, false, false, false, true, true, false),
	CALCULATED_ONE_TO_MANY(false, false, false, true, false, true, false),
	CALCULATED_MANY_TO_ONE(false, false, false, false, true, true, false),
	CALCULATED_MANY_TO_MANY(false, false, false, true, false, true, false);


	@Getter private final boolean id;

	@Getter private final boolean values;

	@Getter private final boolean tranzient;

	@Getter private final boolean toMany;

	@Getter private final boolean toOne;

	@Getter private final boolean formula;

	@Getter private final boolean simple;

	private FieldMappingType(boolean id, boolean values, boolean tranzient, boolean toMany, boolean toOne, boolean formula, boolean simple) {
		this.id = id;
		this.values = values;
		this.tranzient = tranzient;
		this.toMany = toMany;
		this.toOne = toOne;
		this.formula = formula;
		this.simple = simple;
	}

}

