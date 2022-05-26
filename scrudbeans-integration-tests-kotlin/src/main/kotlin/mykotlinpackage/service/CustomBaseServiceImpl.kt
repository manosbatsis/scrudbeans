package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import javax.persistence.EntityManager


abstract class CustomBaseServiceImpl<T: Any, S: Any, B: ModelRepository<T, S>>(
    repository: B,
    entityManager: EntityManager,
    identifierAdapter: IdentifierAdapter<T, S>,
) : AbstractJpaPersistableModelServiceImpl<T, S, B>(
    repository,
    entityManager,
    identifierAdapter
), CustomBaseService<T, S> {

    override fun getFoo(): Boolean = true
}
