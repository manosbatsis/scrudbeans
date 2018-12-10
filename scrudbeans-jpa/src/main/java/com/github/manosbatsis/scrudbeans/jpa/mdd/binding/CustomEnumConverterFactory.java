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
package com.github.manosbatsis.scrudbeans.jpa.mdd.binding;

import javax.inject.Named;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;

@Named("CcstomEnumConverterFactory")
public class CustomEnumConverterFactory implements ConverterFactory<String, Enum> {

	public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
		return new StringToEnumConverter(targetType);
	}

	private final class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

		private Class<T> enumType;

		public StringToEnumConverter(Class<T> enumType) {
			this.enumType = enumType;
		}

		public T convert(String source) {
			return (T) Enum.valueOf(this.enumType, source.trim());
		}
	}
}