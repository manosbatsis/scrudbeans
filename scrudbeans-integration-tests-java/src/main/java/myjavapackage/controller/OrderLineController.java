package myjavapackage.controller;


import com.github.manosbatsis.kotlin.utils.api.Dto;
import com.github.manosbatsis.scrudbeans.api.util.Mimes;
import com.github.manosbatsis.scrudbeans.controller.AbstractModelServiceBackedController;
import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.tags.Tag;
import myjavapackage.model.OrderLine;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * A controller for order lines that allows search, create and delete but removes support for update and patch.
 */
@RestController
@RequestMapping(value = {"/api/rest/orderLines"},
		consumes = {MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE},
		produces = {MimeTypeUtils.APPLICATION_JSON_VALUE, Mimes.MIME_APPLICATIOM_HAL_PLUS_JSON_VALUE})

@OpenAPIDefinition(tags = @Tag(name = "Order Lines", description = "Search, create and delete order lines"))
public class OrderLineController extends AbstractModelServiceBackedController<OrderLine, String, JpaPersistableModelService<OrderLine, String>, Dto<OrderLine>> {
/*

@Override
public OrderLine update(String id, OrderLine resource) {
	throw new NotImplementedException("Method is unsupported.");
}

	@Override
	public OrderLine patch(String id, OrderLine resource) {
		throw new NotImplementedException("Method is unsupported.");
	}
 */
}
