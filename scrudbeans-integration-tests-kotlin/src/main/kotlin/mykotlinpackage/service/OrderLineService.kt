package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.AbstractJpaEntityProjectorService
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.OrderLineIdentifierAdapter
import mykotlinpackage.repository.OrderLineRepository
import org.springframework.context.`annotation`.Lazy
import org.springframework.stereotype.Service
import java.util.*
import javax.persistence.EntityManager

@Service
class OrderLineService(
    orderLineRepository: OrderLineRepository,
    entityManager: EntityManager,
    @Lazy identifierAdapter: OrderLineIdentifierAdapter
) : AbstractJpaEntityProjectorService<OrderLine, UUID, OrderLineRepository>(
    orderLineRepository, entityManager, identifierAdapter
)
