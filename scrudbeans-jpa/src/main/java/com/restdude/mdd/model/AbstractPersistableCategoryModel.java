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
 * Resource categories are hierarchical, have aliases and can be used as tags
 */
@MappedSuperclass
public abstract class AbstractPersistableCategoryModel<P extends AbstractPersistableCategoryModel, T extends AbstractPersistableCategoryModel> extends AbstractPersistableHierarchicalModel<P, T> {

	private static final long serialVersionUID = -1329254539598110186L;

	public AbstractPersistableCategoryModel() {
		super();
	}

	public AbstractPersistableCategoryModel(String name) {
		super(name);
	}

	public AbstractPersistableCategoryModel(String name, P parent) {
		super(name, parent);
	}

}
