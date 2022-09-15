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
 * along with this program. If not, see <http:></http:>//www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.api.annotation.model

import java.lang.annotation.Documented
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import kotlin.reflect.KClass

/**
 *
 * Marks a Model as candidate for mdd code generation (Controller, Service, Repository)
 */
@ScrudBeansModelAnnotation
@Retention(RetentionPolicy.RUNTIME)
@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Documented
annotation class ScrudBean(
    /**
     * The superclass for the generated controller
     */
    val controllerSuperClass: String = "",
    val serviceSuperInterface: KClass<*> = Any::class,
    val serviceImplSuperClass: KClass<*> = Any::class,
    /**
     * The default "parent" mapping part for annotated type
     */
    val basePath: String = "",
    /**
     * The default "parent" mapping part for annotated type
     */
    val parentPath: String = "",
    /**
     * The default URI component for the annotated type
     */
    val pathFragment: String = "",
    /**
     *
     * The API (grouping) name for the generated controller. Used for swagger documentation.
     */
    val apiName: String = "",
    /**
     *
     * The API description for the generated controller. Used for swagger documentation.
     */
    val apiDescription: String = "",
    /**
     * May be used to determine the target transaction manager,
     * matching the qualifier value (or the bean name) of a specific TransactionManager bean definition.
     */
    val transactionManager: String = ""
) {
    companion object {
        /**
         * Used to disable generation when used in [.controllerSuperClass]
         */
        var NONE = "NONE"
    }
}
