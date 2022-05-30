package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import java.util.*

interface JpaPersistableModelService<T : Any, S> {
	val identifierAdapter: IdentifierAdapter<T, S>
	/** Count resources by type */
	fun count(): Long
	/** Count resources by type using an RSQL filter */
	fun count(filter: String): Long
	/** Count resources by type using a JPA specification */
	fun count(specification: Specification<T>): Long
	/** Check if a resource matching the given ID exists */
	fun existsById(id: S): Boolean
	/** Throw an error if a resource matching the given ID does not exist */
	fun existsByIdAssert(id: S)
	/** Find all resources by type */
	fun findAll(): Iterable<T>
	/** Find all resources by type and project results as [P] */
	fun <P> findAllProjectedBy(projection: Class<P>): Iterable<P>
	/** Find the resource matching the given ID if it exists */
	fun findById(id: S): Optional<T>
	/** Find the resource matching the given ID, projected as [P] if it exists */
	fun <P> findByIdProjectedBy(id: S, projection: Class<P>): Optional<P>
	/** Find resources matching any of the given IDs */
	fun findAllByIdIn(ids: Set<S>): Iterable<T>
	/** Find resources matching any of the given IDs, projected as [P] */
	fun <P> findAllByIdInProjectedBy(ids: Set<S>, projection: Class<P>): Iterable<P>
	/** Find member by property name for the entity matching the given ID */
	fun findChildById(id: S, child: String): Any?
	/** Find resources page-by-page using an RSQL filter */
	fun findAll(
		filter: String,
		sortBy: String,
		sortDirection: Sort.Direction,
		pageNumber: Int = 0,
		pageSize: Int = 10,
	): Page<T>
	/** Find resources page-by-page using an RSQL filter, projected as [P] */
	fun <P> findAllProjectedBy(
		filter: String,
		sortBy: String,
		sortDirection: Sort.Direction,
		pageNumber: Int = 0,
		pageSize: Int = 10,
		projection: Class<P>
	): Page<P>
	/** Find resources page-by-page using a JPA specification */
	fun findAll(
		specification: Specification<T>,
		pageNumber: Int = 0,
		pageSize: Int = 10,
	): Page<T>
	/** Find resources page-by-page using a JPA specification, projected as [P] */
	fun <P> findAllProjectedBy(
		specification: Specification<T>,
		pageNumber: Int = 0,
		pageSize: Int = 10,
		projection: Class<P>
	): Page<P>
	/** Get the resource matching the given ID, throw an error if no match is found */
	fun getById(id: S): T
	/** Get the resource matching the ID given as a string, throw an error if no match is found */
	fun getByIdAsString(id: String): T
	/** Get the resource matching the given ID, projected as [P], throw an error otherwise */
	fun <P> getByIdProjectedBy(id: S, projection: Class<P>): P
	/** Save, i.e. create or update, the given resource as given */
	fun save(entity: T): T
	/** Save, i.e. create or update, the given resources as given */
	fun saveAll(resources: Iterable<T>): Iterable<T>
	/** Update the resource matching the given [id] using the input [dto]'s non-null members as delta */
	fun partialUpdate(dto: Dto<T>, id: S): T
	/** Delete an existing resource. */
	fun delete(resource: T)
	/** Delete the resource matching the given ID */
	fun deleteById(id: S)
	/** Delete the resources matching the given IDs */
	fun deleteAllById(ids: Iterable<S> )
}