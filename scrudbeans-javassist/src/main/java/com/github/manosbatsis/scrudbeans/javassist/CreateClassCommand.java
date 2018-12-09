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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

public class CreateClassCommand {

	@Getter @Setter
	String source;

	@Getter @Setter
	Class<?> baseImpl;

	@Getter @Setter
	Collection<Class<?>> interfaces;

	@Getter @Setter
	Collection<Class<?>> genericTypes;

	@Getter @Setter
	Collection<CreateMethodCommand> methods;

	Map<Class<?>, Map<String, Object>> typeAnnotations;

	public CreateClassCommand(String name, Class<?> baseImpl) {
		super();
		this.source = name;
		this.baseImpl = baseImpl;
	}

	public CreateClassCommand(String name, Class<?> baseImpl,
			Collection<Class<?>> interfaces, Collection<Class<?>> genericTypes) {
		this(name, baseImpl);
		this.interfaces = interfaces;
		this.genericTypes = genericTypes;
	}


	public Map<Class<?>, Map<String, Object>> getTypeAnnotations() {
		return typeAnnotations;
	}

	public void setTypeAnnotations(Map<Class<?>, Map<String, Object>> typeAnnotations) {
		this.typeAnnotations = typeAnnotations;
	}

	public CreateClassCommand addGenericType(Class<?> genericType) {
		if (this.genericTypes == null) {
			this.genericTypes = new LinkedList<Class<?>>();
		}
		this.genericTypes.add(genericType);
		return this;
	}

	public CreateClassCommand addInterface(Class<?> interfaze) {
		if (this.interfaces == null) {
			this.interfaces = new LinkedList<Class<?>>();
		}
		this.interfaces.add(interfaze);
		return this;
	}

	public CreateClassCommand addTypeAnnotation(Class<?> annotation, Map<String, Object> members) {
		if (this.typeAnnotations == null) {
			this.typeAnnotations = new HashMap<>();
		}
		this.typeAnnotations.put(annotation, members);
		return this;
	}

	public CreateClassCommand addTypeAnnotation(Class<?> annotation) {
		return this.addTypeAnnotation(annotation, null);
	}

	public CreateClassCommand addMethod(CreateMethodCommand method) {
		if (this.methods == null) {
			this.methods = new HashSet<CreateMethodCommand>();
		}
		this.methods.add(method);
		return this;
	}

}
