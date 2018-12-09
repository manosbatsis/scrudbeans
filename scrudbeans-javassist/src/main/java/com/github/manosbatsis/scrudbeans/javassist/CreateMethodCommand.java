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
package com.github.manosbatsis.scrudbeans.javassist;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class CreateMethodCommand {

	@Getter @Setter
	String name;

	@Getter @Setter
	Map<Class<?>, Map<String, Object>> methodAnotations;

	public CreateMethodCommand(String name) {
		super();
		this.name = name;
	}

	public CreateMethodCommand addMethodAnnotation(Class<?> annotation, Map<String, Object> members) {
		if (this.methodAnotations == null) {
			this.methodAnotations = new HashMap<>();
		}
		this.methodAnotations.put(annotation, members);
		return this;
	}

	public CreateMethodCommand addMethodAnnotation(Class<?> annotations) {
		return this.addMethodAnnotation(annotations, null);
	}

}
