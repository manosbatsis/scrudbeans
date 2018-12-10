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
package com.github.manosbatsis.scrudbeans.jpa.mdd.util;

import java.util.HashMap;
import java.util.Map;

import com.github.manosbatsis.scrudbeans.api.mdd.registry.ModelInfo;
import com.github.manosbatsis.scrudbeans.api.util.Mimes;
import com.github.manosbatsis.scrudbeans.javassist.JavassistBaseUtil;
import javassist.ClassClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import org.springframework.hateoas.ExposesResourceFor;
import org.springframework.hateoas.Identifiable;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
public class JavassistUtil extends JavassistBaseUtil {


	public static Class<?> getMappedModelControllerClass(Class<?> existingClass, ModelInfo modelInfo, String basePath, String defaultParentPath) {
		Class<?> newClass = null;
		if (StringUtils.isBlank(defaultParentPath)) {
			defaultParentPath = "";
		}
		try {
			ClassPool pool = ClassPool.getDefault();
			pool.appendClassPath(new ClassClassPath(existingClass));
			CtClass ctClass = pool.get(existingClass.getName());
			String beanName = StringUtils.uncapitalize(existingClass.getSimpleName());
			Map<Class<?>, Map<String, Object>> typeAnnotations = new HashMap<>();

			// @RestController
			Map<String, Object> restControllerMembers = new HashMap<>();
			restControllerMembers.put("value", beanName);
			typeAnnotations.put(RestController.class, restControllerMembers);

			// add HATEOAS links support?
			if (Identifiable.class.isAssignableFrom(modelInfo.getModelType())) {
				Map<String, Object> exposesResourceForMembers = new HashMap<>();
				exposesResourceForMembers.put("value", modelInfo.getModelType());
				typeAnnotations.put(ExposesResourceFor.class, exposesResourceForMembers);
			}

			// @RequestMapping
			String modelUriComponent = modelInfo.getUriComponent();
			String modelParentPath = modelInfo.getParentPath(defaultParentPath);
			String modelBasePath = modelInfo.getBasePath(basePath);
			String pattern = new StringBuffer("/")
					.append(modelBasePath)
					.append("/")
					.append(modelParentPath)
					.append("/")
					.append(modelUriComponent).toString();
			pattern = pattern.replaceAll("/{2,}", "/");
			log.debug("getMappedModelControllerClass adding pattern: {}", pattern);

			Map<String, Object> requestMappingMembers = new HashMap<>();
			requestMappingMembers.put("value", new String[] {pattern});
			// add JSON and HAL defaults
			String[] defaultMimes = {MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE};
			requestMappingMembers.put("consumes", defaultMimes);
			requestMappingMembers.put("produces", defaultMimes);
			typeAnnotations.put(RequestMapping.class, requestMappingMembers);

			// add annotations to class
			addTypeAnnotations(ctClass, typeAnnotations);
			ctClass.setName(existingClass.getPackage().getName() + ".RestdudeGenerated" + existingClass.getSimpleName());
			newClass = ctClass.toClass();
		}
		catch (Exception e) {
			throw new RuntimeException("Failed to activate ModelController annotated class " + existingClass.getCanonicalName() + ": " + e.getMessage(), e);
		}
		return newClass;
	}

}