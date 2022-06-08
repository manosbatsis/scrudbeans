package com.github.manosbatsis.scrudbeans.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import java.util.*

/** Extends [JpaEntityService] with projection support to query functions */
interface JpaEntityProjectorService<T : Any, S : Any> : JpaEntityService<T, S> {

    /** Find all resources by type and project results as [P] */
    fun <P> findAllProjectedBy(projection: Class<P>): Iterable<P>

    /** Find resources page-by-page using an RSQL filter, projected as [P] */
    fun <P> findAllProjectedBy(
        filter: String,
        sortBy: String,
        sortDirection: Sort.Direction,
        pageNumber: Int = 0,
        pageSize: Int = 10,
        projection: Class<P>
    ): Page<P>

    /** Find resources page-by-page using a JPA specification, projected as [P] */
    fun <P> findAllProjectedBy(
        specification: Specification<T>,
        pageNumber: Int = 0,
        pageSize: Int = 10,
        projection: Class<P>
    ): Page<P>

    /** Get the resource matching the given ID, projected as [P], throw an error otherwise */
    fun <P> getByIdProjectedBy(id: S, projection: Class<P>): P

    /** Find the resource matching the given ID, projected as [P] if it exists */
    fun <P> findByIdProjectedBy(id: S, projection: Class<P>): Optional<P>

    /** Find resources matching any of the given IDs, projected as [P] */
    fun <P> findAllByIdInProjectedBy(ids: Set<S>, projection: Class<P>): Iterable<P>
}
