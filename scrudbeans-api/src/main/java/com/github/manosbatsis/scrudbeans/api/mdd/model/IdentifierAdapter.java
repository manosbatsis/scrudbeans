package com.github.manosbatsis.scrudbeans.api.mdd.model;

/**
 * Provides read/write access to the singular identifier of an (entity) type
 */
public interface IdentifierAdapter<T, ID> {
    String getIdName(T resource);

    ID readId(T resource);

    void writeId(T resource, ID id);
}
