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
package com.restdude.mdd.model;

import javax.persistence.MappedSuperclass;

/**
 * Abstract base persistent class for dynamic bean properties
 */
@MappedSuperclass
public abstract class AbstractPersistablePredicateModel<V> extends AbstractSystemUuidPersistableModel {

	private static final long serialVersionUID = -1468517690700208260L;

	private String type;

	private String name;

	private String caption;

	private String placeholder;

	private String options;

}