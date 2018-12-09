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
package com.restdude.mdd.annotation.model;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * <p>Marks a Model as candidate for mdd code generation (Controller, Service, Repository)</p>
 *
 *  <p>For example:</p>
 *
 *  <pre class="code">
 * &#064;ModelResource(
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
 * import org.slf4j.Logger;
 * import org.slf4j.LoggerFactory;
 * import org.springframework.stereotype.Controller;
 * import org.springframework.web.bind.annotation.RequestMapping;
 *
 * import gr.abiss.restdude.model.geography.Country;
 * import com.restdude.domain.geography.service.CountryService;
 * import com.restdude.domain.base.controller.AbstractModelController;
 * import io.swagger.annotations.Api;
 *
 * &#064;Controller
 * &#064;Api(tags = "Countries", description = "Operations about countries")
 * &#064;RequestMapping(
 *  pathFragment = "/api/rest/countries",
 * 	produces = { "application/json", "application/xml" }
 * )
 * public class CountryController extends AbstractModelController<Country, String, CountryService> {
 *
 * 		private static final Logger LOGGER = LoggerFactory.getLogger(CountryController.class);
 *
 * }
 * </pre>
 *
 */
@RestdudeModelAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface ModelResource {

	/**
	 * The superclass for the generated controller
	 */
	Class<?> controllerSuperClass() default Object.class;

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
	String pathFragment();

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
	 *
	 * Hint to enable (javers auditing b
	 */
	boolean auditable() default false;

	boolean rootLevel() default true;

	boolean linkable() default true;

}
