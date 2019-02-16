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

import java.util.List;

import lombok.NonNull;

/**
 * Provides metadata for all Model types and generates missing model-based components
 *  i.e. <code>Repository</code>, <code>Service</code> and
 * <code>Controller</code> mdd
 *
 */
public interface ModelInfoRegistry {
	ModelInfo getEntryFor(Class<?> modelClass);

	List<ModelInfo> getEntries();

	List<Class> getTypes();

	Class<?> getHandlerModelType(@NonNull Class<?> handlerType);
}
