package mykotlinpackage.controller

import com.github.manosbatsis.scrudbeans.api.util.Mimes
import com.github.manosbatsis.scrudbeans.controller.AbstractModelServiceBackedController
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.tags.Tag
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.OrderLineDto
import mykotlinpackage.service.OrderLineService
import org.springframework.util.MimeTypeUtils
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

/**
 * A controller for order lines that allows search, create and delete but removes support for update and patch.
 */
@RestController
@RequestMapping(value = ["/api/rest/orderLines"], consumes = [MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE], produces = [MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE])
@OpenAPIDefinition(tags = [Tag(name = "Order Lines", description = "Search, create and delete order lines")])
class OrderLineController : AbstractModelServiceBackedController<OrderLine, UUID, OrderLineService, OrderLineDto>() {

}