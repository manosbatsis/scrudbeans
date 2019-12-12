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
package com.github.manosbatsis.scrudbeans.binding;

import com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

/**
 * Converter factory targeting {@link EmbeddableCompositeIdentifier} instances
 */
public class StringToEmbeddableCompositeIdConverterFactory implements ConverterFactory<String, EmbeddableCompositeIdentifier> {

	@Override
	public <T extends EmbeddableCompositeIdentifier> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEmbeddableManyToManyIdConverter<T>(targetType);
	}


	/**
	 * Converter for {@link EmbeddableCompositeIdentifier} identifier instances
	 * @param <T> the identifier implementation type
	 */
	@SuppressWarnings("rawtypes")
	private final class StringToEmbeddableManyToManyIdConverter<T extends EmbeddableCompositeIdentifier> implements Converter<String, T> {

		private Class targetType;

		public StringToEmbeddableManyToManyIdConverter(Class<?> targetType) {
			this.targetType = targetType;
		}

		@Override
		public T convert(String id) {
			T object;
			try {
				object = (T) targetType.newInstance();
				object.init(id);
			}
			catch (InstantiationException | IllegalAccessException e) {
				throw new RuntimeException(new StringBuffer("Failed to deserialize with id: ").append(id).append(", class: ").append(targetType.toString()).toString(), e);
			}
			return object;
		}
	}
}
