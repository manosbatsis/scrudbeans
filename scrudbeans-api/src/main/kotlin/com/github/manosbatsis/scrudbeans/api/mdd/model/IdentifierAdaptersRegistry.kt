package com.github.manosbatsis.scrudbeans.api.mdd.model

import org.springframework.util.Assert

object IdentifierAdaptersRegistry {
    internal val adaptersMap = HashMap<String, IdentifierAdapter<*, *>>()

    /**
     * Register an appropriate predicate factory for the given class
     */
    fun addAdapterForClass(clazz: Class<*>, adapter: IdentifierAdapter<*, *>) {
        Assert.notNull(clazz, "clazz cannot be null")
        Assert.notNull(adapter, "adapter cannot be null")
        adaptersMap[clazz.canonicalName] = adapter
    }

    /**
     * Register an appropriate predicate factory for the given class
     */
    fun addAdapterForClass(clazz: Class<*>, adapterClass: Class<IdentifierAdapter<*, *>?>) {
        Assert.notNull(clazz, "clazz cannot be null")
        Assert.notNull(adapterClass, "adapterClass cannot be null")
        try {
            adaptersMap[clazz.canonicalName] = adapterClass.newInstance() as IdentifierAdapter<*, *>
        } catch (e: Exception) {
            throw RuntimeException("Failed creating identifier adapter instance", e)
        }
    }

    /**
     * Get an appropriate [IdentifierAdapter] factory for the given class
     */
    @JvmStatic
    fun <T> getAdapterForClass(clazz: Class<T>): IdentifierAdapter<T, *>? {
        return adaptersMap[clazz.canonicalName] as IdentifierAdapter<T, *>
    }

    /**
     * Get an appropriate [IdentifierAdapter] factory for the given class
     */
    fun containsAdapterForClass(clazz: Class<*>): Boolean {
        return getAdapterForClass(clazz) != null
    }
}