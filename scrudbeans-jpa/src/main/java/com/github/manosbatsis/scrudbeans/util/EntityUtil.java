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
package com.github.manosbatsis.scrudbeans.util;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

import com.github.manosbatsis.scrudbeans.api.domain.Persistable;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudRelatedBean;
import com.github.manosbatsis.scrudbeans.validation.CaseSensitive;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StopWatch;

public class EntityUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityUtil.class);

    private static ConcurrentHashMap<String, Boolean> fieldCaseSensitivity = new ConcurrentHashMap<>();
    private static ClassPathScanningCandidateComponentProvider provider = null;
    private static Map<Class<?>, Boolean> scrudBeanTypes = new ConcurrentHashMap<>();

    public static Boolean isScrudBean(Class<?> domainType) {
        Boolean isScrudBean = scrudBeanTypes.get(domainType);
        if (Objects.isNull(isScrudBean)) {
            isScrudBean = Persistable.class.isAssignableFrom(domainType) || domainType.isAnnotationPresent(ScrudBean.class);
            scrudBeanTypes.put(domainType, true);
        }
        return isScrudBean;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getParentEntity(Object child) {
        ScrudRelatedBean anr = child.getClass().getAnnotation(ScrudRelatedBean.class);
        Assert.notNull(anr, "Given child object has no @RelatedEntity annotation");
        Field field = ReflectionUtils.findField(child.getClass(), anr.parentProperty());
        field.setAccessible(true);
        Object parent = ReflectionUtils.getField(field, child);
        return (T) parent;
    }

	public static Set<BeanDefinition> findPersistableModels(String scanPackage) {
        createComponentScanner(Entity.class, Embeddable.class);
        return provider.findCandidateComponents(scanPackage);
    }

	public static Set<BeanDefinition> findModelResources(String scanPackage) {
        createComponentScanner(ScrudBean.class, ScrudRelatedBean.class);
        return provider.findCandidateComponents(scanPackage);
    }

	public static Set<BeanDefinition> findEntities(String... basePackages) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.setKeepTaskList(true);
        stopWatch.start("findEntities init");
        createComponentScanner(Entity.class);
        stopWatch.stop();
        print(stopWatch.getLastTaskInfo());
        Set<BeanDefinition> entities = new HashSet<>();
        for (String basePackage : basePackages) {
            stopWatch.start("findEntities " + basePackage);
            entities.addAll(provider.findCandidateComponents(basePackage));
            stopWatch.stop();
            print(stopWatch.getLastTaskInfo());
        }
        stopWatch.prettyPrint();
        return entities;
    }

    public static void print(StopWatch.TaskInfo task) {
        StringBuilder sb = new StringBuilder();
        sb.append("; [").append(task.getTaskName()).append("] took ").append(task.getTimeMillis());
        LOGGER.debug(task.toString());
    }

    public static Set<BeanDefinition> findAllPredicateFactories(String... basePackages) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.setKeepTaskList(true);
        stopWatch.start("findAllPredicateFactories init");
        createComponentScanner(EntityPredicateFactory.class);
        stopWatch.stop();
        print(stopWatch.getLastTaskInfo());
        Set<BeanDefinition> predicateFactories = new HashSet<>();
        for (String basePackage : basePackages) {
            stopWatch.start("findAllPredicateFactories in " + basePackage);
            predicateFactories.addAll(provider.findCandidateComponents(basePackage));
            stopWatch.stop();
            LOGGER.debug(stopWatch.prettyPrint());
        }
        stopWatch.prettyPrint();
        return predicateFactories;
    }
	public static Set<BeanDefinition> findAllModels(String... basePackages) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.setKeepTaskList(true);
        stopWatch.start("findAllModels init");
        createComponentScanner(Entity.class, ScrudBean.class, ScrudRelatedBean.class);

        stopWatch.stop();
        print(stopWatch.getLastTaskInfo());
        Set<BeanDefinition> entities = new HashSet<>();
        for (String basePackage : basePackages) {
            stopWatch.start("findAllModels in " + basePackage);
            entities.addAll(provider.findCandidateComponents(basePackage));
            stopWatch.stop();
            print(stopWatch.getLastTaskInfo());
        }
        stopWatch.prettyPrint();
        return entities;
    }


	public static Set<String> findEntityPackageNames(String... basePackages) {
		Set<String> ids = new HashSet<String>();
        for (String basePackage : basePackages) {
            Set<BeanDefinition> entityBeanDefs = EntityUtil.findEntities(basePackage);
            for (BeanDefinition beanDef : entityBeanDefs) {
                Class<?> entity = ClassUtils.getClass(beanDef.getBeanClassName());
                ids.add(entity.getPackage().getName());
            }
        }
        return ids;
    }

    public static void createComponentScanner(Class... annotations) {

        if (provider == null) {
            provider = new ClassPathScanningCandidateComponentProvider(false);
        } else {
            provider.resetFilters(false);
        }
        for (Class annotation : annotations) {
            provider.addIncludeFilter(new AnnotationTypeFilter(annotation));
        }
    }

    public static BeanInfo getBeanInfo(Class<?> beanType) {

        try {
            return Introspector.getBeanInfo(beanType);
        }
		catch (IntrospectionException e) {
			throw new RuntimeException(e);
		}
	}

	public static String[] getNullPropertyNames(Object source) {
		final BeanWrapper src = new BeanWrapperImpl(source);
		java.beans.PropertyDescriptor[] pds = src.getPropertyDescriptors();

		Set<String> emptyNames = new HashSet<String>();
		for (java.beans.PropertyDescriptor pd : pds) {
			Object srcValue = src.getPropertyValue(pd.getName());
			if (srcValue == null) emptyNames.add(pd.getName());
		}
		String[] result = new String[emptyNames.size()];
		return emptyNames.toArray(result);
	}

	public static boolean isCaseSensitive(Class domainClass, String propertyName) {
		boolean caseSensitive = false;
		String fieldKey = new StringBuffer(domainClass.getCanonicalName()).append('.').append(propertyName).toString();


		if (!fieldCaseSensitivity.containsKey(fieldKey)) {
			Field field = FieldUtils.getField(domainClass, propertyName, true);

			if (field != null && String.class.isAssignableFrom(field.getType())) {

				CaseSensitive annotation = field.getAnnotation(CaseSensitive.class);
				if (annotation != null) {
					caseSensitive = annotation.value();
				}
                fieldCaseSensitivity.put(fieldKey, caseSensitive);
            }
        } else {
            caseSensitive = fieldCaseSensitivity.get(fieldKey);
        }

        return caseSensitive;
    }

    public static <PK extends Serializable> PK idOrNull(Persistable<PK> user) {
        return user != null ? user.getScrudBeanId() : null;
    }

    public static String idOrNEmpty(Persistable entity) {
        return entity != null ? entity.getScrudBeanId().toString() : StringUtils.EMPTY;
    }
}
