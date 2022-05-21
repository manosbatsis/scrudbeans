package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import javax.persistence.EntityManager


abstract class CustomBaseServiceImpl<T: Any, S: Any, B: ModelRepository<T, S>>(
    repository: B,
    entityType: Class<T>,
    entityIdType: Class<S>,
    entityManager: EntityManager
) : AbstractJpaPersistableModelServiceImpl<T, S, B>(
    repository,
    entityType,
    entityIdType,
    entityManager
), CustomBaseService<T, S> {

    override fun getFoo(): Boolean = true
}
