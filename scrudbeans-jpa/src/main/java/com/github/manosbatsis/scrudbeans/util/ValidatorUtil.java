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
package com.github.manosbatsis.scrudbeans.util;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Column;

import org.apache.commons.lang3.reflect.FieldUtils;

/**
 */
public class ValidatorUtil {

	public static final ConcurrentHashMap<Class, List<String>> uniqueFieldNames = new ConcurrentHashMap<Class, List<String>>();

	public static List<String> getUniqueFieldNames(Class beanClass) {
		List<String> names;

		// if names have not been initialized for bean classz
		if (uniqueFieldNames.containsKey(beanClass)) {
			names = uniqueFieldNames.get(beanClass);
		}
		else {
			// init unique names
			names = new LinkedList<String>();
			Field[] fields = FieldUtils.getFieldsWithAnnotation(beanClass, Column.class);
			if (fields.length > 0) {
				for (int i = 0; i < fields.length; i++) {
					Field field = fields[i];
					Column column = field.getAnnotation(Column.class);

					// if unique or not-null field
					if (!field.getName().equals("id")) {
						if (column.unique()) {
							names.add(field.getName());
						}
					}
				}
			}

			// cache unique names for bean class
			uniqueFieldNames.put(beanClass, names);
		}

		return names;
	}

}