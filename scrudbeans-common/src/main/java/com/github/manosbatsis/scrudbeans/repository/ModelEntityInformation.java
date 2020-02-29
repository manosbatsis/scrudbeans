package com.github.manosbatsis.scrudbeans.repository;

import org.springframework.data.jpa.repository.support.JpaEntityInformation;

public interface ModelEntityInformation<T, PK> extends JpaEntityInformation<T, PK> {

    boolean isNew(T entity);

    PK getId(T entity);
}
