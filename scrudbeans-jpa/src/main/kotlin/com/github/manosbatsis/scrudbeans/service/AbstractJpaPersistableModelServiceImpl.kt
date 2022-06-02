package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.api.domain.PersistenceHintsDto
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.exceptions.EntityNotFoundException
import com.github.manosbatsis.scrudbeans.extensions.value
import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.util.ClassUtils
import io.github.perplexhub.rsql.RSQLJPASupport
import io.github.perplexhub.rsql.RSQLJPASupport.toSpecification
import org.slf4j.LoggerFactory
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager

abstract class AbstractJpaPersistableModelServiceImpl<T: Any, S: Any, B: ModelRepository<T, S>>(
	protected val repository: B,
	protected val entityManager: EntityManager,
	override val identifierAdapter: IdentifierAdapter<T, S>
) : JpaPersistableModelService<T, S> {

	companion object{
		val logger = LoggerFactory.getLogger(AbstractJpaPersistableModelServiceImpl::class.java)
	}



	@Transactional(readOnly = true)
	override fun count(): Long = repository.count()

	@Transactional(readOnly = true)
	override fun count(filter: String): Long =
		if(filter.isBlank()) count() else count(toSpecification(filter))

	@Transactional(readOnly = true)
	override fun count(specification: Specification<T>): Long =
		repository.count(specification)

	@Transactional(readOnly = true)
	override fun existsById(id: S): Boolean = repository.existsById(id)

	@Transactional(readOnly = true)
	override fun existsByIdAssert(id: S): Unit = if (existsById(id)) Unit else throw EntityNotFoundException()

	@Transactional(readOnly = true)
	override fun findAll(): Iterable<T> = repository.findAll()

	@Transactional(readOnly = true)
	override fun <P> findAllProjectedBy(projection: Class<P>): Iterable<P> =
		repository.findBy(projection)

	@Transactional(readOnly = true)
	override fun findById(id: S): Optional<T> = repository.findById(id)

	@Transactional(readOnly = true)
	override fun <P> findByIdProjectedBy(id: S, projection: Class<P>): Optional<P> =
		repository.findById(id, projection)

	@Transactional(readOnly = true)
	override fun findAllByIdIn(ids: Set<S>): Iterable<T> = repository.findAllById(ids)

	@Transactional(readOnly = true)
	override fun <P> findAllByIdInProjectedBy(ids: Set<S>, projection: Class<P>): Iterable<P> =
		repository.findAllByIdIn(ids, projection)

	@Transactional(readOnly = true)
	override fun findChildById(id: S, child: String): Any? {
		val entity = getById(id)
		return ClassUtils.fieldByName(child, entity.javaClass)?.value(entity)
	}

	@Transactional(readOnly = true)
	override fun findAll(
		filter: String,
		sortBy: String,
		sortDirection: Sort.Direction,
		pageNumber: Int,
		pageSize: Int
	): Page<T> =
		if(filter.isBlank()) repository.findAll(PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortBy)))
		else findAll(
			toSpecification<T>(filter).and(RSQLJPASupport.toSort("$sortBy,${sortDirection.toString().lowercase()}")),
			pageNumber, pageSize)

	@Transactional(readOnly = true)
	override fun <P> findAllProjectedBy(
		filter: String,
		sortBy: String,
		sortDirection: Sort.Direction,
		pageNumber: Int,
		pageSize: Int,
		projection: Class<P>
	): Page<P> = try {
		if (filter.isBlank()) repository.findBy(
			PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortBy)),
			projection
		)
		else findAllProjectedBy(
			toSpecification<T>(filter).and(RSQLJPASupport.toSort("$sortBy,${sortDirection.toString().lowercase()}")),
			pageNumber,
			pageSize,
			projection
		)
	}catch (e: Throwable){
		e.printStackTrace()
		throw e
	}

	@Transactional(readOnly = true)
	override fun findAll(
		specification: Specification<T>,
		pageNumber: Int,
		pageSize: Int
	): Page<T> = repository.findAll(specification, PageRequest.of(pageNumber, pageSize))

	@Transactional(readOnly = true)
	override fun <P> findAllProjectedBy(
		specification: Specification<T>,
		pageNumber: Int,
		pageSize: Int,
		projection: Class<P>
	): Page<P> = repository.findBy(specification, PageRequest.of(pageNumber, pageSize), projection)

	@Transactional(readOnly = true)
	override fun getById(id: S): T = findById(id).orElseThrow { EntityNotFoundException() }

	@Transactional(readOnly = true)
	override fun <P> getByIdProjectedBy(id: S, projection: Class<P>): P =
		findByIdProjectedBy(id, projection).orElseThrow { EntityNotFoundException() }

	@Transactional(readOnly = false, propagation = Propagation.NESTED)
	override fun save(entity: T): T = repository.save(entity)

	@Transactional(readOnly = false, propagation = Propagation.NESTED)
	override fun saveAll(resources: Iterable<T>): Iterable<T> =
		repository.saveAll(resources)

	@Transactional(readOnly = false, propagation = Propagation.NESTED)
	override fun partialUpdate(dto: Dto<T>, id: S): T{
		val persisted = getById(id)
		val patched = dto.toPatched(persisted)
		// TODO: maybe merge is enough?
		if(dto is PersistenceHintsDto && dto.isDetachedUpdate())
			entityManager.detach(persisted)
		return repository.save(patched)
	}

	@Transactional(readOnly = false, propagation = Propagation.NESTED)
	override fun delete(resource: T) = repository.delete(resource)

	@Transactional(readOnly = false, propagation = Propagation.NESTED)
	override fun deleteById(id: S) {
		existsByIdAssert(id)
		repository.deleteById(id)
	}

	@Transactional(readOnly = false, propagation = Propagation.NESTED)
	override fun deleteAllById(ids: Iterable<S> ) {
		repository.deleteAllById(ids)
	}
}