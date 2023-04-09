package mykotlinpackage.controller

import com.github.manosbatsis.scrudbeans.controller.AbstractJpaEntityController
import io.swagger.v3.oas.annotations.OpenAPIDefinition
import io.swagger.v3.oas.annotations.tags.Tag
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.OrderLineDto
import mykotlinpackage.service.OrderLineService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

/**
 * A controller for order lines that allows search, create and delete but removes support for update and patch.
 */
@RestController
@RequestMapping(value = ["/api/rest/orderLines"])
@OpenAPIDefinition(tags = [Tag(name = "Order Lines", description = "Search, create and delete order lines")])
class OrderLineController : AbstractJpaEntityController<OrderLine, UUID, OrderLineService, OrderLineDto>()
