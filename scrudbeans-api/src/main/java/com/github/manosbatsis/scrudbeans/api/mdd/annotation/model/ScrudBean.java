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

import java.lang.annotation.*;


/**
 * <p>Marks a Model as candidate for mdd code generation (Controller, Service, Repository)</p>
 */
@ScrudBeansModelAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ScrudBean {

    /**
     * Used to disable generation when used in {@link #controllerSuperClass()}
     */
    String NONE = "NONE";

    /**
     * The superclass for the generated controller
     */
	String controllerSuperClass() default "";

	Class serviceSuperInterface() default Object.class;

	Class serviceImplSuperClass() default Object.class;

	/**
	 * The default "parent" mapping part for annotated type
	 */
	String basePath() default "";

	/**
	 * The default "parent" mapping part for annotated type
	 */
	String parentPath() default "";

	/**
	 * The default URI component for the annotated type
	 */
	String pathFragment() default "";

	/**
	 *
	 * The API (grouping) name for the generated controller. Used for swagger documentation.
	 */
	String apiName() default "";

	/**
	 *
	 * The API description for the generated controller. Used for swagger documentation.
	 */
	String apiDescription() default "";

	/**
	 * May be used to determine the target transaction manager,
	 * matching the qualifier value (or the bean name) of a specific TransactionManager bean definition.
	 */
	String transactionManager() default "";


}
