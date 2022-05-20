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
package com.github.manosbatsis.scrudbeans.util

import com.fasterxml.jackson.annotation.JsonIgnore
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.EntityPredicateFactory
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.IdentifierAdapterBean
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudRelatedBean
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry
import com.github.manosbatsis.scrudbeans.validation.CaseSensitive
import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.reflect.FieldUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.BeanWrapper
import org.springframework.beans.BeanWrapperImpl
import org.springframework.beans.factory.config.BeanDefinition
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider
import org.springframework.core.type.filter.AnnotationTypeFilter
import org.springframework.util.Assert
import org.springframework.util.ReflectionUtils
import java.beans.BeanInfo
import java.beans.IntrospectionException
import java.beans.Introspector
import java.io.Serializable
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import javax.persistence.Embeddable
import javax.persistence.Entity

object EntityUtil {
    private val LOGGER = LoggerFactory.getLogger(EntityUtil::class.java)
    private val fieldCaseSensitivity = ConcurrentHashMap<String, Boolean>()
    private var provider: ClassPathScanningCandidateComponentProvider? = null
    private val scrudBeanTypes: MutableMap<Class<*>, Boolean> = ConcurrentHashMap()
    fun isScrudBean(domainType: Class<*>): Boolean? {
        var isScrudBean = scrudBeanTypes[domainType]
        if (Objects.isNull(isScrudBean)) {
            isScrudBean = domainType.isAnnotationPresent(ScrudBean::class.java)
            scrudBeanTypes[domainType] = true
        }
        return isScrudBean
    }

    fun <T> getParentEntity(child: Any): T? {
        val anr = child.javaClass.getAnnotation(ScrudRelatedBean::class.java)
        Assert.notNull(anr, "Given child object has no @RelatedEntity annotation")
        val field = ReflectionUtils.findField(child.javaClass, anr.parentProperty)
        field!!.isAccessible = true
        val parent = ReflectionUtils.getField(field, child)
        return parent as T?
    }

    fun findPersistableModels(scanPackage: String?): Set<BeanDefinition> {
        createComponentScanner(Entity::class.java, Embeddable::class.java)
        return provider!!.findCandidateComponents(scanPackage!!)
    }

    fun findModelResources(scanPackage: String?): Set<BeanDefinition> {
        createComponentScanner(ScrudBean::class.java, ScrudRelatedBean::class.java)
        return provider!!.findCandidateComponents(scanPackage!!)
    }

    fun findEntities(vararg basePackages: String?): Set<BeanDefinition> {
        createComponentScanner(Entity::class.java)
        val entities: MutableSet<BeanDefinition> = HashSet()
        for (basePackage in basePackages) {
            entities.addAll(provider!!.findCandidateComponents(basePackage!!))
        }
        return entities
    }

    fun findAllHelpers(vararg basePackages: String?): Set<BeanDefinition> {
        createComponentScanner(EntityPredicateFactory::class.java, IdentifierAdapterBean::class.java)
        val predicateFactories: MutableSet<BeanDefinition> = HashSet()
        for (basePackage in basePackages) {
            predicateFactories.addAll(provider!!.findCandidateComponents(basePackage!!))
        }
        return predicateFactories
    }

    fun findAllModels(vararg basePackages: String?): Set<BeanDefinition> {
        createComponentScanner(Entity::class.java, ScrudBean::class.java, ScrudRelatedBean::class.java)
        val entities: MutableSet<BeanDefinition> = HashSet()
        for (basePackage in basePackages) {
            entities.addAll(provider!!.findCandidateComponents(basePackage!!))
        }
        return entities
    }

    fun findEntityPackageNames(vararg basePackages: String?): Set<String> {
        val ids: MutableSet<String> = HashSet()
        for (basePackage in basePackages) {
            val entityBeanDefs = findEntities(basePackage)
            for (beanDef in entityBeanDefs) {
                val entity = ClassUtils.getClass(beanDef.beanClassName)
                ids.add(entity.getPackage().name)
            }
        }
        return ids
    }

    fun createComponentScanner(vararg annotations: Class<out Annotation>) {
        if (provider == null) {
            provider = ClassPathScanningCandidateComponentProvider(false)
        } else {
            provider!!.resetFilters(false)
        }
        for (annotation in annotations) {
            provider!!.addIncludeFilter(AnnotationTypeFilter(annotation))
        }
    }

    fun getBeanInfo(beanType: Class<*>?): BeanInfo {
        return try {
            Introspector.getBeanInfo(beanType)
        } catch (e: IntrospectionException) {
            throw RuntimeException(e)
        }
    }

    fun getNullPropertyNames(source: Any): List<String> {
        val beanWrapper = BeanWrapperImpl(source)
        return beanWrapper.propertyDescriptors
            .filter { isNullOrUnreadableProperty(beanWrapper, it.name) }
            .map { it.name }.distinct()
    }

    fun isNullOrUnreadableProperty(source: BeanWrapper, name: String?): Boolean {
        var nullOrUnreadable = !source.isReadableProperty(name!!)
        if (!nullOrUnreadable) {
            val descriptor = source.getPropertyDescriptor(name)
            val getter = descriptor.readMethod
            if (getter == null || getter.isAnnotationPresent(JsonIgnore::class.java) || source.getPropertyValue(
                    name
                ) == null
            ) {
                nullOrUnreadable = true
            }
        }
        LOGGER.debug("isNullOrUnreadableProperty {}: {}", name, nullOrUnreadable)
        return nullOrUnreadable
    }

    fun isCaseSensitive(domainClass: Class<*>, propertyName: String?): Boolean {
        var caseSensitive = false
        val fieldKey = StringBuffer(domainClass.canonicalName).append('.').append(propertyName).toString()
        if (!fieldCaseSensitivity.containsKey(fieldKey)) {
            val field = FieldUtils.getField(domainClass, propertyName, true)
            if (field != null && String::class.java.isAssignableFrom(field.type)) {
                val annotation = field.getAnnotation(CaseSensitive::class.java)
                if (annotation != null) {
                    caseSensitive = annotation.value
                }
                fieldCaseSensitivity[fieldKey] = caseSensitive
            }
        } else {
            caseSensitive = fieldCaseSensitivity[fieldKey]!!
        }
        return caseSensitive
    }

    fun <PK : Serializable?> idOrNull(entity: Any?): PK? {
        return entity?.let {entity ->
                IdentifierAdaptersRegistry.getAdapterForClass(entity.javaClass)
                    .readId(entity) as PK
        }
    }

    fun idOrNEmpty(entity: Any?): String {
        return idOrNull(entity) ?: StringUtils.EMPTY
    }
}