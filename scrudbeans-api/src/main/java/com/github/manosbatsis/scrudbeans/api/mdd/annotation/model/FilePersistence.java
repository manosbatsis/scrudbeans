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
package com.github.manosbatsis.scrudbeans.api.mdd.annotation.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark field as a manageable file
 */
@ScrudBeansModelAnnotation
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FilePersistence {
	/**
	 * Accepted MIME types
	 */
	String[] mimeTypeIncludes() default {};

	/**
	 * (Optional) Maximum width in pixels (images only)
	 */
	int maxWidth() default 0;

	/**
	 * (Optional) Maximum width in pixels (images only)
	 */
	int maxHeight() default 0;

	/**
	 * (Optional) Whether to clip  for an exact match for both dimensions  (images only)
	 */
	boolean clip() default true;

	/**
	 * (Optional) Whether to index for search
	 */
	boolean addToIndex() default false;

}
