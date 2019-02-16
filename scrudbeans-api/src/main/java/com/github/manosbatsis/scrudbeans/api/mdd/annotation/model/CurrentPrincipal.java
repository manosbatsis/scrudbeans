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
package com.github.manosbatsis.scrudbeans.api.mdd.annotation.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Used to enforce a criterion based on the current session userDetails, 
 * i.e. the loggedin in user. Enforced at service/specification level. For enforcing 
 * at controller level see CurrentPrincipalField.
 * @see CurrentPrincipalField
 */
@ScrudBeansModelAnnotation
@Target(value = ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentPrincipal {

	/**
	 * (Optional) The names of roles to exclude from the
	 * current principal criterion
	 *
	 * <p> By default no roles are excluded.
	 */
	String[] ignoreforRoles() default {"ROLE_ANONYMOUS"};

}
