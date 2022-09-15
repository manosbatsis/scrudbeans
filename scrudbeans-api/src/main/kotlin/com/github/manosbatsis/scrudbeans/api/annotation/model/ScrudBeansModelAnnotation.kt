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

import com.github.manosbatsis.scrudbeans.api.annotation.ScrudBeansAnnotation
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy

/**
 * Meta-annotation (annotations used on other annotations)
 * used for marking all annotations that are
 * part of Restdude applicable to models. Can be used for recognizing
 * model annotations generically, and in future also for
 * passing other generic annotation configuration.
 */
@ScrudBeansAnnotation
@Target(AnnotationTarget.ANNOTATION_CLASS)
@Retention(RetentionPolicy.RUNTIME)
annotation class ScrudBeansModelAnnotation { // for now, a pure tag annotation, no parameters
}
