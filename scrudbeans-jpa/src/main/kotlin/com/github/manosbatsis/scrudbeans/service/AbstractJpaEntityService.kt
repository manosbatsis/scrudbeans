package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.kotlin.utils.api.Dto
import com.github.manosbatsis.scrudbeans.api.domain.PersistenceHintsDto
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.exceptions.EntityNotFoundException
import com.github.manosbatsis.scrudbeans.extensions.value
import com.github.manosbatsis.scrudbeans.logging.contextLogger
import com.github.manosbatsis.scrudbeans.repository.JpaEntityRepository
import com.github.manosbatsis.scrudbeans.util.ClassUtils
import io.github.perplexhub.rsql.RSQLJPASupport
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager

abstract class AbstractJpaEntityService<T : Any, S : Any, B : JpaEntityRepository<T, S>>(
    protected val repository: B,
    protected val entityManager: EntityManager,
    override val identifierAdapter: IdentifierAdapter<T, S>
) : JpaEntityService<T, S> {

    companion object {
        private val logger = contextLogger()
    }

    @Transactional(readOnly = true)
    override fun count(): Long = repository.count()

    @Transactional(readOnly = true)
    override fun count(filter: String): Long =
        if (filter.isBlank()) count() else count(RSQLJPASupport.toSpecification(filter))

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
    override fun findById(id: S): Optional<T> = repository.findById(id)

    @Transactional(readOnly = true)
    override fun findAllByIdIn(ids: Set<S>): Iterable<T> = repository.findAllById(ids)

    @Transactional(readOnly = true)
    override fun findChildById(id: S, child: String): Any? {
        val entity = getById(id)
        return ClassUtils.fieldByName(child, entity.javaClass)?.value(entity)
    }

    @Transactional(readOnly = true)
    override fun findAll(
        filter: String,
        sortBy: String?,
        sortDirection: Sort.Direction,
        pageNumber: Int,
        pageSize: Int
    ): Page<T> {
        return if (filter.isBlank()) repository.findAll(
            PageRequest.of(
                pageNumber, pageSize,
                sortBy
                    ?.let { Sort.by(sortDirection, sortBy) }
                    ?: Sort.unsorted()
            )
        )
        else findAll(
            RSQLJPASupport.toSpecification<T>(filter).let {
                if (sortBy != null)
                    it.and(RSQLJPASupport.toSort("$sortBy,${sortDirection.toString().lowercase()}"))
                else it
            },
            pageNumber, pageSize
        )
    }

    @Transactional(readOnly = true)
    override fun findAll(
        specification: Specification<T>,
        pageNumber: Int,
        pageSize: Int
    ): Page<T> = repository.findAll(specification, PageRequest.of(pageNumber, pageSize))

    @Transactional(readOnly = true)
    override fun getById(id: S): T = findById(id).orElseThrow { EntityNotFoundException() }

    @Transactional(readOnly = false, propagation = Propagation.NESTED)
    override fun save(entity: T): T = repository.save(entity)

    @Transactional(readOnly = false, propagation = Propagation.NESTED)
    override fun saveAll(resources: Iterable<T>): Iterable<T> =
        repository.saveAll(resources)

    @Transactional(readOnly = false, propagation = Propagation.NESTED)
    override fun partialUpdate(dto: Dto<T>, id: S): T {
        val persisted = getById(id)
        val patched = dto.toPatched(persisted)
        // TODO: maybe merge is enough?
        if (dto is PersistenceHintsDto && dto.isDetachedUpdate())
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
    override fun deleteAllById(ids: Iterable<S>) {
        repository.deleteAllById(ids)
    }
}