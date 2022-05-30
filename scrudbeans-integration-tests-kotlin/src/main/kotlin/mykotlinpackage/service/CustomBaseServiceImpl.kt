package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import org.springframework.core.convert.ConversionService
import javax.persistence.EntityManager


abstract class CustomBaseServiceImpl<T: Any, S: Any, B: ModelRepository<T, S>>(
    repository: B,
    entityManager: EntityManager,
    identifierAdapter: IdentifierAdapter<T, S>,
    conversionService: ConversionService
) : AbstractJpaPersistableModelServiceImpl<T, S, B>(
    repository,
    entityManager,
    identifierAdapter,
    conversionService
), CustomBaseService<T, S> {

    override fun getFoo(): Boolean = true
}
