package com.github.manosbatsis.scrudbeans.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean

/** Basic JPA Search+CRUD repository with support for [Specification]s */
@NoRepositoryBean
interface JpaEntityRepository<T, S> : JpaRepository<T, S>, JpaSpecificationExecutor<T>