package com.github.manosbatsis.scrudbeans.tx

import com.github.manosbatsis.scrudbeans.logging.loggerFor
import org.springframework.beans.BeansException
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
import org.springframework.core.PriorityOrdered
import org.springframework.transaction.interceptor.TransactionAttributeSource


/**
 * Replaces the default "transactionAttributeSource" bean (defined in [ProxyTransactionManagementConfiguration])
 * with instance of [MergeAnnotationTransactionAttributeSource].
 *
 * See https://github.com/spring-projects/spring-framework/issues/24291#issuecomment-618343343
 *
 * @author Eugen Labun
 */
// TODO: is this needed at all?
//@Component
class AnnotationTransactionAttributeSourceReplacer : InstantiationAwareBeanPostProcessor,
    PriorityOrdered /*this is important*/ {

    companion object{
        private val logger = loggerFor<AnnotationTransactionAttributeSourceReplacer>()
    }

    init {
        // to check that the replacer is created before instantiation of the "transactionAttributeSource" bean
        logger.trace("AnnotationTransactionAttributeSourceReplacer - constructor")
    }

    @Throws(BeansException::class)
    override fun postProcessBeforeInstantiation(beanClass: Class<*>, beanName: String): Any? {
        // log.trace("postProcessBeforeInstantiation - beanName: {}, beanClass: {}", beanName, beanClass);
        return if (beanName == "transactionAttributeSource"
            && TransactionAttributeSource::class.java.isAssignableFrom(beanClass)
        ) {
            MergeAnnotationTransactionAttributeSource().also {
                logger.debug("instantiating bean {} as {}", beanName, it::class.java.name)
            }
        } else {
            null
        }
    }

    override fun getOrder(): Int {
        return 0
    }
}