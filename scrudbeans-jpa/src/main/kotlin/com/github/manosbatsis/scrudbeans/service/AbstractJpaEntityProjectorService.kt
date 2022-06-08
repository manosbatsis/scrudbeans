package com.github.manosbatsis.scrudbeans.service

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.exceptions.EntityNotFoundException
import com.github.manosbatsis.scrudbeans.logging.contextLogger
import com.github.manosbatsis.scrudbeans.repository.JpaEntityProjectorRepository
import io.github.perplexhub.rsql.RSQLJPASupport
import io.github.perplexhub.rsql.RSQLJPASupport.toSpecification
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.transaction.annotation.Transactional
import java.util.*
import javax.persistence.EntityManager

abstract class AbstractJpaEntityProjectorService<T : Any, S : Any, B : JpaEntityProjectorRepository<T, S>>(
    repository: B,
    entityManager: EntityManager,
    identifierAdapter: IdentifierAdapter<T, S>
) : AbstractJpaEntityService<T, S, B>(repository, entityManager, identifierAdapter),
    JpaEntityProjectorService<T, S> {

    companion object {
        private val logger = contextLogger()
    }

    @Transactional(readOnly = true)
    override fun <P> findAllProjectedBy(projection: Class<P>): Iterable<P> =
        repository.findBy(projection)

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
    } catch (e: Throwable) {
        e.printStackTrace()
        throw e
    }

    @Transactional(readOnly = true)
    override fun <P> findAllProjectedBy(
        specification: Specification<T>,
        pageNumber: Int,
        pageSize: Int,
        projection: Class<P>
    ): Page<P> = repository.findBy(specification, PageRequest.of(pageNumber, pageSize), projection)

    @Transactional(readOnly = true)
    override fun <P> findByIdProjectedBy(id: S, projection: Class<P>): Optional<P> =
        repository.findById(id, projection)

    @Transactional(readOnly = true)
    override fun <P> getByIdProjectedBy(id: S, projection: Class<P>): P =
        findByIdProjectedBy(id, projection).orElseThrow { EntityNotFoundException() }

    @Transactional(readOnly = true)
    override fun <P> findAllByIdInProjectedBy(ids: Set<S>, projection: Class<P>): Iterable<P> =
        repository.findAllByIdIn(ids, projection)
}
