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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.manosbatsis.scrudbeans.api.DtoMapper;


/**
 * <p>Marks a Model as candidate for mdd code generation (Controller, Service, Repository)</p>
 *
 *  <p>For example:</p>
 *
 *  <pre class="code">
 * &#064;ScrudBean(
 * 		pathFragment = "countries",
 * 		apiName = "Countries",
 * 		apiDescription = "Operations about countries",
 * 		controllerSuperClass = AbstractModelController.class
 * 	)
 * </pre>
 *
 *  <p>Will generate the following controller:</p>
 *
 *  <pre class="code">
 *
 *
 * &#064;RestController
 * &#064;Api( tags = "Countries", description = "Operations about countries" )
 * &#064;RequestMapping( pathFragment = "/api/rest/countries" )
 * public class CountryController extends AbstractModelController<Country, String, CountryService> {
 *
 * }
 * </pre>
 *
 */
@ScrudBeansModelAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ScrudBean {

	/** DTO classes to generate {@link DtoMapper}s for */
	Class[] dtoTypes() default Object.class;

	/** DTO class canonical names to generate {@link DtoMapper}s for */
	String[] dtoTypeNames() default "";

	/**
	 * The superclass for the generated controller
	 */
	String controllerSuperClass() default "";

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
	 * Hint to enable javers auditing
	 */
	boolean auditable() default false;

	boolean linkable() default true;

}
