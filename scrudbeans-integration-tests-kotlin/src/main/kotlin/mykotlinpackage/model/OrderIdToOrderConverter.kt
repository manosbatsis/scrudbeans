package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.exceptions.EntityNotFoundException
import com.github.manosbatsis.scrudbeans.logging.loggerFor
import mykotlinpackage.service.OrderService
import org.springframework.core.convert.ConversionService
import org.springframework.core.convert.converter.Converter
import java.util.*


//@Component
class OrderIdToOrderConverter(
    val orderService: OrderService,
    val conversionService: ConversionService
): Converter<UUID, Order> {

    companion object{
        val logger = loggerFor<OrderIdToOrderConverter>()
    }

    init {
        logger.info("Converter initialized: ${this.javaClass.canonicalName}")
    }

    override fun convert(source: UUID): Order {
        val identifier = conversionService
            .convert(source, orderService.identifierAdapter.entityIdType)
            ?: throw EntityNotFoundException()
        return orderService.getById(identifier)
    }
}