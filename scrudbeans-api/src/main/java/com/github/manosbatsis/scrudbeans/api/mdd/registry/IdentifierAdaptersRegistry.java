package com.github.manosbatsis.scrudbeans.api.mdd.registry;

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import org.springframework.util.Assert;

import java.util.HashMap;

public class IdentifierAdaptersRegistry {

    protected static final HashMap<String, IdentifierAdapter> adaptersMap = new HashMap<>();

    /**
     * Register an appropriate predicate factory for the given class
     */
    public static void addAdapterForClass(Class clazz, IdentifierAdapter adapter) {
        Assert.notNull(clazz, "clazz cannot be null");
        Assert.notNull(adapter, "adapter cannot be null");
        adaptersMap.put(clazz.getCanonicalName(), adapter);
    }

    /**
     * Register an appropriate predicate factory for the given class
     */
    public static void addAdapterForClass(Class clazz, Class<IdentifierAdapter> adapterClass) {
        Assert.notNull(clazz, "clazz cannot be null");
        Assert.notNull(adapterClass, "adapterClass cannot be null");
        try {
            adaptersMap.put(clazz.getCanonicalName(), adapterClass.newInstance());
        } catch (Exception e) {
            throw new RuntimeException("Failed creating identifier adapter instance", e);
        }
    }

    /**
     * Get an appropriate {@link IdentifierAdapter} factory for the given class
     */
    public static <T> IdentifierAdapter<T, ?> getAdapterForClass(Class<T> clazz) {
        return adaptersMap.get(clazz.getCanonicalName());
    }

    /**
     * Get an appropriate {@link IdentifierAdapter} factory for the given class
     */
    public static boolean containsAdapterForClass(Class<?> clazz) {
        return getAdapterForClass(clazz) != null;
    }
}
