package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.service.AbstractJpaPersistableModelServiceImpl
import mykotlinpackage.model.OrderLine
import mykotlinpackage.repository.OrderLineRepository
import mykotlinpackage.repository.ProductRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.*

@Service
class OrderLineService : AbstractJpaPersistableModelServiceImpl<OrderLine, UUID, OrderLineRepository>() {

    @Autowired
    lateinit var productRepository: ProductRepository

    /**
     * {@inheritDoc}
     * @param resource
     */
    override fun save(resource: OrderLine): OrderLine {
        return super.save(resource)
    }

}