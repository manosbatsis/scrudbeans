package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.repository.JpaEntityProjectorRepository
import com.github.manosbatsis.scrudbeans.service.AbstractJpaEntityProjectorService
import javax.persistence.EntityManager

abstract class CustomBaseServiceImpl<T : Any, S : Any, B : JpaEntityProjectorRepository<T, S>>(
    repository: B,
    entityManager: EntityManager,
    identifierAdapter: IdentifierAdapter<T, S>
) : AbstractJpaEntityProjectorService<T, S, B>(
    repository,
    entityManager,
    identifierAdapter
),
    CustomBaseService<T, S> {

    override fun getFoo(): Boolean = true
}
