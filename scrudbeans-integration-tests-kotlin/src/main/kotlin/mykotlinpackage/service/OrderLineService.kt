package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.OrderLineIdentifierAdapter
import mykotlinpackage.repository.OrderLineRepository
import org.springframework.core.convert.ConversionService
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager

@Service
class OrderLineService(
    orderLineRepository: OrderLineRepository,
    entityManager: EntityManager,
    identifierAdapter: OrderLineIdentifierAdapter,
    conversionService: ConversionService
) : AbstractJpaPersistableModelServiceImpl<OrderLine, UUID, OrderLineRepository>(
    orderLineRepository, entityManager, identifierAdapter, conversionService
) {

}