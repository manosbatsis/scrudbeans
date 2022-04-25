package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.exceptions.EntityNotFoundException
import com.github.manosbatsis.scrudbeans.extensions.value
import com.github.manosbatsis.scrudbeans.model.BaseEntity
import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import io.github.perplexhub.rsql.RSQLJPASupport
import io.github.perplexhub.rsql.RSQLJPASupport.toSpecification
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.ReflectionUtils
import java.lang.reflect.Field
import java.util.*
import javax.persistence.EntityManager
import javax.persistence.criteria.CriteriaUpdate
import javax.persistence.criteria.Root

open class AbstractJpaPersistableModelServiceImpl<T: BaseEntity<S>, S: Any, B: ModelRepository<T, S>>(
) : JpaPersistableModelService<T, S> {

	// TODO: add target entity type via annotation processor

	// TODO: support multiple datasources
	@Autowired
	lateinit var repository: B
	@Autowired
	lateinit var entityManager: EntityManager

	override fun count(): Long = repository.count()

	override fun count(filter: String): Long =
		if(filter.isBlank()) count() else count(toSpecification(filter))

	override fun count(specification: Specification<T>): Long =
		repository.count(specification)

	override fun existsById(id: S): Boolean = repository.existsById(id)

	override fun existsByIdAssert(id: S): Unit = if (existsById(id)) Unit else throw EntityNotFoundException()

	override fun findAll(): Iterable<T> = repository.findAll()

	override fun <P> findAllProjectedBy(projection: Class<P>): Iterable<P> =
		repository.findBy(projection)

	override fun findById(id: S): Optional<T> = repository.findById(id)

	override fun <P> findByIdProjectedBy(id: S, projection: Class<P>): Optional<P> =
		repository.findById(id, projection)

	override fun findAllByIdIn(ids: Set<S>): Iterable<T> = repository.findAllById(ids)

	override fun <P> findAllByIdInProjectedBy(ids: Set<S>, projection: Class<P>): Iterable<P> =
		repository.findAllByIdIn(ids, projection)

	override fun findChildById(id: S, child: String): Any? {
		findById(id)?.let {entity ->
			val field = entity.javaClass.declaredFields.find { it.name.equals(child) }
			field?.let {
				it.isAccessible = true
				return it.value(entity)
			}
		}
		return null
	}

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

	override fun <P> findAllProjectedBy(
		filter: String,
		sortBy: String,
		sortDirection: Sort.Direction,
		pageNumber: Int,
		pageSize: Int,
		projection: Class<P>
	): Page<P> =
		if(filter.isBlank()) repository.findBy(PageRequest.of(pageNumber, pageSize, Sort.by(sortDirection, sortBy)), projection)
		else findAllProjectedBy(
			toSpecification<T>(filter).and(RSQLJPASupport.toSort("$sortBy,${sortDirection.toString().lowercase()}")),
			pageNumber, pageSize, projection)

	override fun findAll(
		specification: Specification<T>,
		pageNumber: Int,
		pagesize: Int
	): Page<T> = repository.findAll(specification, PageRequest.of(pageNumber, pagesize))

	override fun <P> findAllProjectedBy(
		specification: Specification<T>,
		pageNumber: Int,
		pagesize: Int,
		projection: Class<P>
	): Page<P> = repository.findBy(specification, PageRequest.of(pageNumber, pagesize), projection)

	override fun getById(id: S): T = findById(id).orElseThrow { EntityNotFoundException() }

	override fun <P> getByIdProjectedBy(id: S, projection: Class<P>): P =
		findByIdProjectedBy(id, projection).orElseThrow { EntityNotFoundException() }

	@Transactional(readOnly = false)
	override fun save(entity: T): T = repository.save(entity)

	@Transactional(readOnly = false)
	override fun saveAll(resources: Iterable<T>): Iterable<T> =
		repository.saveAll(resources)

	@Transactional(readOnly = false)
	override fun update(dto: Dto<T>, id: S): T{
		return repository.getById(id)
			?.let { save(dto.toPatched(it)) }
	}

	@Transactional(readOnly = false)
	override fun delete(resource: T) = repository.delete(resource)

	@Transactional(readOnly = false)
	override fun deleteById(id: S) {
		existsByIdAssert(id)
		repository.deleteById(id)
	}

	@Transactional(readOnly = false)
	override fun deleteAllById(ids: Iterable<S> ) {
		repository.deleteAllById(ids)
	}

	private fun handleEmbedded(field: Field, criteria: CriteriaUpdate<T>, root: Root<T>, entity: T) {
		val value = field.value(entity)
		value?.let { safeValue ->
			val fields: MutableList<Any> = mutableListOf()
			safeValue::class.java.declaredFields.forEach {
				it?.let {
					ReflectionUtils.makeAccessible(it)
					fields.add(it)
				}
			}
			criteria.set(root.get(field.name), value)
		}
	}
}