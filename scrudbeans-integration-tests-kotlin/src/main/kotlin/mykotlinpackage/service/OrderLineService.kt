package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.service.PersistableModelService
import com.github.manosbatsis.scrudbeans.service.AbstractPersistableModelServiceImpl
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.Product
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class OrderLineService : AbstractPersistableModelServiceImpl<OrderLine, String, ModelRepository<OrderLine, String>>(), PersistableModelService<OrderLine, String> {

    @Autowired
    lateinit var productRepository: ModelRepository<Product, String>

    /**
     * {@inheritDoc}
     * @param resource
     */
    override fun create(resource: OrderLine): OrderLine {
        return super.create(resource)!!
    }

}