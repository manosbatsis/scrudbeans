package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.OrderLineIdentifierAdapter
import mykotlinpackage.repository.OrderLineRepository
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager

@Service
class OrderLineService(
    orderLineRepository: OrderLineRepository,
    entityManager: EntityManager
) : AbstractJpaPersistableModelServiceImpl<OrderLine, UUID, OrderLineRepository>(
    orderLineRepository, OrderLine::class.java, UUID::class.java, entityManager
) {
    override val identifierAdapter = OrderLineIdentifierAdapter

}