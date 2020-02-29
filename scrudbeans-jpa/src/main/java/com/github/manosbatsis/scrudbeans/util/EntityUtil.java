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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.IdentifierAdapterBean;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudRelatedBean;
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry;
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

import javax.persistence.Embeddable;
import javax.persistence.Entity;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class EntityUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityUtil.class);

    private static ConcurrentHashMap<String, Boolean> fieldCaseSensitivity = new ConcurrentHashMap<>();
    private static ClassPathScanningCandidateComponentProvider provider = null;
    private static Map<Class<?>, Boolean> scrudBeanTypes = new ConcurrentHashMap<>();

    public static Boolean isScrudBean(Class<?> domainType) {
        Boolean isScrudBean = scrudBeanTypes.get(domainType);
        if (Objects.isNull(isScrudBean)) {
            isScrudBean = domainType.isAnnotationPresent(ScrudBean.class);
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
        createComponentScanner(Entity.class);
        Set<BeanDefinition> entities = new HashSet<>();
        for (String basePackage : basePackages) {
            entities.addAll(provider.findCandidateComponents(basePackage));
        }
        return entities;
    }

    public static Set<BeanDefinition> findAllHelpers(String... basePackages) {
        createComponentScanner(EntityPredicateFactory.class, IdentifierAdapterBean.class);
        Set<BeanDefinition> predicateFactories = new HashSet<>();
        for (String basePackage : basePackages) {
            predicateFactories.addAll(provider.findCandidateComponents(basePackage));
        }
        return predicateFactories;
    }

    public static Set<BeanDefinition> findAllModels(String... basePackages) {
        createComponentScanner(Entity.class, ScrudBean.class, ScrudRelatedBean.class);
        Set<BeanDefinition> entities = new HashSet<>();
        for (String basePackage : basePackages) {
            entities.addAll(provider.findCandidateComponents(basePackage));
        }
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
        PropertyDescriptor[] pds = src.getPropertyDescriptors();

        Set<String> emptyNames = new HashSet<String>();
        for (PropertyDescriptor pd : pds) {
            if (isNullOrUnreadableProperty(src, pd.getName())) {
                emptyNames.add(pd.getName());
            }
        }
        String[] result = new String[emptyNames.size()];
        return emptyNames.toArray(result);
    }


    public static boolean isNullOrUnreadableProperty(BeanWrapper source, String name) {
        boolean nullOrUnreadable = !source.isReadableProperty(name);
        if (!nullOrUnreadable) {
            PropertyDescriptor descriptor = source.getPropertyDescriptor(name);
            Method getter = descriptor.getReadMethod();
            if (getter == null
                    || getter.isAnnotationPresent(JsonIgnore.class)
                    || source.getPropertyValue(name) == null) {
                nullOrUnreadable = true;
            }
        }
        LOGGER.debug("isNullOrUnreadableProperty {}: {}", name, nullOrUnreadable);
        return nullOrUnreadable;
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

    public static <PK extends Serializable> PK idOrNull(Object entity) {
        IdentifierAdapter identifierAdapter = IdentifierAdaptersRegistry.getAdapterForClass(entity.getClass());
        return entity != null ? (PK) identifierAdapter.readId(entity) : null;
    }

    public static String idOrNEmpty(Object entity) {
        IdentifierAdapter identifierAdapter = IdentifierAdaptersRegistry.getAdapterForClass(entity.getClass());
        return entity != null ? identifierAdapter.readId(entity).toString() : StringUtils.EMPTY;
    }
}
