package com.github.manosbatsis.scrudbeans.repository;

import com.github.manosbatsis.scrudbeans.api.domain.KPersistable;
import org.springframework.data.jpa.repository.support.JpaMetamodelEntityInformation;
import org.springframework.lang.Nullable;

import javax.persistence.metamodel.Metamodel;
import java.io.Serializable;

/**
 * A clone of {@link org.springframework.data.jpa.repository.support.JpaPersistableEntityInformation}
 * that uses {@link KPersistable}.
 */
public class KPersistableModelEntityInformation<T extends KPersistable<PK>, PK extends Serializable>
        extends JpaMetamodelEntityInformation<T, PK> {
    /**
     * Creates a new instance for the given domain class and {@link Metamodel}.
     *
     * @param domainClass must not be {@literal null}.
     * @param metamodel   must not be {@literal null}.
     */
    public KPersistableModelEntityInformation(Class<T> domainClass, Metamodel metamodel) {
        super(domainClass, metamodel);
    }

    @Override
    public boolean isNew(T entity) {
        return entity.isNew();
    }

    @Nullable
    @Override
    public PK getId(T entity) {
        return entity.getId();
    }
}