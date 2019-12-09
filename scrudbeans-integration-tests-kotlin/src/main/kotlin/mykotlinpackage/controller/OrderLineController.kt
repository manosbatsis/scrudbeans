package mykotlinpackage.controller

import com.github.manosbatsis.scrudbeans.api.exception.NotImplementedException
import com.github.manosbatsis.scrudbeans.api.util.Mimes
import com.github.manosbatsis.scrudbeans.jpa.controller.AbstractPersistableModelController
import io.swagger.annotations.Api
import mykotlinpackage.model.OrderLine
import mykotlinpackage.service.OrderLineService
import org.springframework.hateoas.ExposesResourceFor
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * A controller for order lines that allows search, create and delete but removes support for update and patch.
 */
@RestController
@RequestMapping(value = ["/api/rest/orderLines"], consumes = [MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE], produces = [MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE])
@ExposesResourceFor(OrderLine::class)
@Api(tags = ["Order Lines"], description = "Search, create and delete order lines")
class OrderLineController : AbstractPersistableModelController<OrderLine, String, OrderLineService>() {
    /** Disable update  */
    override fun update(id: String, resource: OrderLine): OrderLine {
        throw NotImplementedException("Method is unsupported.")
    }

    /** Disable patch  */
    override fun patch(id: String, resource: OrderLine): OrderLine {
        throw NotImplementedException("Method is unsupported.")
    }
}