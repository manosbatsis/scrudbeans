package mykotlinpackage.service

import com.github.manosbatsis.scrudbeans.common.repository.ModelRepository
import com.github.manosbatsis.scrudbeans.common.service.PersistableModelService
import com.github.manosbatsis.scrudbeans.jpa.service.AbstractPersistableModelServiceImpl
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
        // Load the target product
        val lineProduct = productRepository.getOne(resource.product!!.id!!)
        resource.copy(product = lineProduct, name = lineProduct.name, description = lineProduct.description, price = lineProduct.price)
        return super.create(resource)!!
    }

}