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
package com.restdude.mdd.uischema.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Provides form schema fragments for a specific field, e.g:
 *
 * <pre>
 * &#064;FormSchemaEntry(json = "{
 * 		create = &quot;{ \&quot;validators\&quot;: [\&quot;required\&quot;, \&quot;email\&quot;] }&quot;,
 * 		update = &quot;{ \&quot;validators\&quot;: [\&quot;required\&quot;, \&quot;email\&quot;] }&quot;,
 * 		search = &quot;{ \&quot;validators\&quot;: [\&quot;email\&quot;] }&quot;
 *        }"
 * )
 * private String email;
 * </pre>
 *
 * Documentation of the schema format can be found at <a
 * href="https://github.com/powmedia/backbone-forms‎"> powmedia/backbone-forms
 * </a>
 */
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FormSchemas {
	FormSchemaEntry[] value() default {@FormSchemaEntry};

}
