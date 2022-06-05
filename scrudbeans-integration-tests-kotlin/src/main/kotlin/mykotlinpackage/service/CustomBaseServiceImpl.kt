package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.repository.JpaEntityRepository
import com.github.manosbatsis.scrudbeans.service.AbstractJpaEntityService
import javax.persistence.EntityManager

abstract class CustomBaseServiceImpl<T : Any, S : Any, B : JpaEntityRepository<T, S>>(
    repository: B,
    entityManager: EntityManager,
    identifierAdapter: IdentifierAdapter<T, S>
) : AbstractJpaEntityService<T, S, B>(
    repository,
    entityManager,
    identifierAdapter
),
    CustomBaseService<T, S> {

    override fun getFoo(): Boolean = true
}
