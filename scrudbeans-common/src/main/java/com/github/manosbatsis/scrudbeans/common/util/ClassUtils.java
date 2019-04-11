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
package com.github.manosbatsis.scrudbeans.common.util;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Iterator;

import lombok.NonNull;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.GenericTypeResolver;
import org.springframework.util.Assert;

public class ClassUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassUtils.class);

	private static final String DOT = ".";
	/**
	 * Returns the (initialized) class represented by <code>className</code>
	 * using the current thread's context class loader and wraps any exceptions
	 * in a RuntimeException.
	 *
	 * This implementation
	 * supports the syntaxes "<code>java.util.Map.Entry[]</code>",
	 * "<code>java.util.Map$Entry[]</code>", "<code>[Ljava.util.Map.Entry;</code>",
	 * and "<code>[Ljava.util.Map$Entry;</code>".
	 *
	 * @param className  the class name
	 * @return the class represented by <code>className</code> using the current thread's context class loader
	 * @throws ClassNotFoundException if the class is not found
	 */
	public static Class<?> getClass(String className) {
		Class<?> clazz;
		try {
			clazz = org.apache.commons.lang3.ClassUtils.getClass(className);
		}
		catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
		return clazz;
	}

	/**
	 * Break input into a pair consisting of the package and classname
	 */
	public static Pair<String, String> getPackageAndSimpleName(@NonNull String className) {
		int lastDotIndex = className.lastIndexOf(DOT);
		String simpleName = className.substring(lastDotIndex + 1);
		String packageName = className.substring(0, className.length() - (simpleName.length() + 1));
		return Pair.of(packageName, simpleName);
	}

	public static <T extends Object> T newInstance(Class<T> clazz) {
		Assert.notNull(clazz, "clazz cannot be null");
		try {
			return clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException("Failed instantiating new instance for class: " + clazz.getCanonicalName(), e);
		}
	}

	public static Class<?> getBeanPropertyType(Class clazz, String fieldName, boolean silent) {
		Assert.notNull(clazz, "clazz parameter cannot be null");
		Assert.notNull(fieldName, "fieldName parameter cannot be null");
		Class beanPropertyType = null;
		String[] steps = fieldName.contains(".") ? fieldName.split("\\.") : new String[] {fieldName};
		LOGGER.debug("getBeanPropertyType called for {}#{}, steps({}): {}", clazz.getCanonicalName(), fieldName, steps.length, steps);

		try {

			Iterator<String> stepNames = Arrays.asList(steps).listIterator();
			Class tmpClass = clazz;
			String tmpFieldName = null;
			PropertyDescriptor[] propertyDescriptors = null;
			for (int i = 0; stepNames.hasNext() && tmpClass != null; i++) {
				tmpFieldName = stepNames.next();
				LOGGER.debug("getBeanPropertyType for path {}, tmpClass: {} tmpFieldName: {}", i, tmpClass.getName(), tmpFieldName);
				propertyDescriptors = Introspector.getBeanInfo(tmpClass).getPropertyDescriptors();
				for (PropertyDescriptor pd : propertyDescriptors) {
					if (tmpFieldName.equals(pd.getName())) {
						Method getter = pd.getReadMethod();
						if (getter != null) {
							tmpClass = GenericTypeResolver.resolveReturnType(getter, tmpClass);
							LOGGER.debug("getBeanPropertyType for {} found getter for tmpFieldName: {}, return type: {}", i, tmpFieldName, tmpClass);
						}
						else {
							LOGGER.warn("getBeanPropertyType for {} no getter exists for tmpFieldName: {}", i, tmpFieldName);
							tmpClass = null;
						}
						break;
					}
				}
			}
			beanPropertyType = tmpClass;

		}
		catch (IntrospectionException e) {
			if (silent) {
				LOGGER.error("getBeanPropertyType, failed getting type bean property: {}#{}", clazz.getCanonicalName(), fieldName, e);
			}
			else {
				throw new RuntimeException("failed getting bean property type", e);

			}

		}
		LOGGER.debug("getBeanPropertyType found for {}#{}: {}", clazz.getCanonicalName(), fieldName, beanPropertyType);
		return beanPropertyType;
	}

}
