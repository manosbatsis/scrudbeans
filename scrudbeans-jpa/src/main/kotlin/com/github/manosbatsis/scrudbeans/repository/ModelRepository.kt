package com.github.manosbatsis.scrudbeans.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.repository.NoRepositoryBean
import java.util.*

@NoRepositoryBean
interface ModelRepository<T, S> : JpaRepository<T, S>, JpaSpecificationExecutor<T> {

    /** Find all resources by type and project results as [P] */
    fun <P> findBy(projection: Class<P>): Iterable<P>
    /** Find all resources by type and project results as [P] */
    fun <P> findBy(pageable: Pageable, projection: Class<P>): Page<P>
    /** Find resources page-by-page using a JPA specification, projected as [P] */
    fun <P> findBy(specification: Specification<T>, pageable: Pageable, projection: Class<P>): Page<P>
    /** Find the resource matching the given ID, projected as [P] if it exists */
    fun <P> findById(id: S, projection: Class<P>): Optional<P>
    /** Find resources matching any of the given IDs, projected as [P] */
    fun <P> findAllByIdIn(ids: Set<S>, projection: Class<P>): Iterable<P>
}