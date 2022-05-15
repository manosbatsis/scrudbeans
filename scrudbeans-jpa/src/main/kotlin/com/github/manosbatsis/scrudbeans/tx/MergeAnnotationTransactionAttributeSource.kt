package com.github.manosbatsis.scrudbeans.tx

import com.github.manosbatsis.scrudbeans.logging.loggerFor
import org.springframework.aop.support.AopUtils
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.annotation.AnnotationTransactionAttributeSource
import org.springframework.transaction.interceptor.DefaultTransactionAttribute
import org.springframework.transaction.interceptor.RuleBasedTransactionAttribute
import org.springframework.transaction.interceptor.TransactionAttribute
import org.springframework.util.ClassUtils
import java.lang.reflect.Method
import java.lang.reflect.Modifier


/**
 * Implements a merge policy for transaction attributes (see [Transactional] annotation)
 * with following priorities (high to low):
 *
 *  1. specific method;
 *  1. declaring class of the specific method;
 *  1. target class;
 *  1. method in the declaring class/interface;
 *  1. declaring class/interface.
 *
 * The merge policy means that all transaction attributes which are not
 * explicitly set [1] on a specific definition place (see above) will be inherited
 * from the place with the next lower priority.
 *
 * On the contrary, the Spring default [AbstractFallbackTransactionAttributeSource] implements a fallback policy,
 * where all attributes are read from the first found definition place (essentially in the above order), and all others are ignored.
 *
 * See analysis in [Inherited @Transactional methods use wrong TransactionManager](https://github.com/spring-projects/spring-framework/issues/24291).
 *
 * [1] If the value of an attribute is equal to its default value, the current implementation
 * cannot distinguish, whether this value has been set explicitly or implicitly,
 * and considers such attribute as "not explicitly set". Therefore it's currently impossible to override a non-default value with a default value.
 *
 * See https://github.com/spring-projects/spring-framework/issues/24291#issuecomment-618343343
 *
 * @author Eugen Labun
 */
class MergeAnnotationTransactionAttributeSource : AnnotationTransactionAttributeSource() {

    companion object{
        private val logger = loggerFor<MergeAnnotationTransactionAttributeSource>()
    }
    init {
        logger.info("MergeAnnotationTransactionAttributeSource constructor")
    }

    override fun computeTransactionAttribute(method: Method, targetClass: Class<*>?): TransactionAttribute? {
        // Don't allow no-public methods as required.
        if (allowPublicMethodsOnly() && !Modifier.isPublic(method.modifiers)) {
            return null
        }

        // The method may be on an interface, but we also need attributes from the target class.
        // If the target class is null, the method will be unchanged.
        val specificMethod = AopUtils.getMostSpecificMethod(method, targetClass)

        // 1st priority is the specific method.
        var txAttr = findTransactionAttribute(specificMethod)

        // 2nd priority is the declaring class of the specific method.
        val declaringClass = specificMethod.declaringClass
        val userLevelMethod = ClassUtils.isUserLevelMethod(method)
        if (userLevelMethod) {
            txAttr = merge(txAttr, findTransactionAttribute(declaringClass))
        }

        // 3rd priority is the target class
        if (targetClass != null && targetClass != declaringClass && userLevelMethod) {
            txAttr = merge(txAttr, findTransactionAttribute(targetClass))
        }
        if (method !== specificMethod) {
            // 4th priority is the method in the declaring class/interface.
            txAttr = merge(txAttr, findTransactionAttribute(method))

            // 5th priority is the declaring class/interface.
            txAttr = merge(txAttr, findTransactionAttribute(method.declaringClass))
        }
        return txAttr
    }

    /**
     * Set empty and default properties of "primary" object from "secondary" object.
     *
     * Parameter objects should not be used after the call to this method,
     * as they can be changed here or/and returned as a result.
     */
    private fun merge(
        primaryObj: TransactionAttribute?,
        secondaryObj: TransactionAttribute?
    ): TransactionAttribute? {
        if (primaryObj == null) return secondaryObj
        if (secondaryObj == null) return primaryObj

        if (primaryObj is DefaultTransactionAttribute && secondaryObj is DefaultTransactionAttribute) {
            val primary = primaryObj
            val secondary = secondaryObj

            if (primary.qualifier.isNullOrBlank()) primary.qualifier = secondary.qualifier
            if (primary.descriptor.isNullOrBlank()) primary.descriptor = secondary.descriptor
            if (primary.name.isNullOrBlank() && !secondary.name.isNullOrBlank()) primary.setName(secondary.name!!)


            // The following properties have default values in DefaultTransactionDefinition;
            // we cannot distinguish here, whether these values have been set explicitly or implicitly;
            // but it seems to be logical to handle default values like empty values.
            if (primary.propagationBehavior == TransactionDefinition.PROPAGATION_REQUIRED) {
                primary.propagationBehavior = secondary.propagationBehavior
            }
            if (primary.isolationLevel == TransactionDefinition.ISOLATION_DEFAULT) {
                primary.isolationLevel = secondary.isolationLevel
            }
            if (primary.timeout == TransactionDefinition.TIMEOUT_DEFAULT) {
                primary.timeout = secondary.timeout
            }
            if (!primary.isReadOnly) {
                primary.isReadOnly = secondary.isReadOnly
            }
        }
        if (primaryObj is RuleBasedTransactionAttribute && secondaryObj is RuleBasedTransactionAttribute) {
            val primary = primaryObj
            if (primary.rollbackRules == null || primary.rollbackRules.isEmpty()) {
                primary.rollbackRules = secondaryObj.rollbackRules
            }
        }
        return primaryObj
    }
}