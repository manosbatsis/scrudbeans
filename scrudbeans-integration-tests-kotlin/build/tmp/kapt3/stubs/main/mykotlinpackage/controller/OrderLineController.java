package mykotlinpackage.controller;

import java.lang.System;

/**
 * A controller for order lines that allows search, create and delete but removes support for update and patch.
 */
@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\b\u0017\u0018\u00002\u001a\u0012\u0004\u0012\u00020\u0002\u0012\u0004\u0012\u00020\u0003\u0012\u0004\u0012\u00020\u0004\u0012\u0004\u0012\u00020\u00050\u0001B\u0005\u00a2\u0006\u0002\u0010\u0006\u00a8\u0006\u0007"}, d2 = {"Lmykotlinpackage/controller/OrderLineController;", "Lcom/github/manosbatsis/scrudbeans/controller/AbstractJpaEntityController;", "Lmykotlinpackage/model/OrderLine;", "Ljava/util/UUID;", "Lmykotlinpackage/service/OrderLineService;", "error/NonExistentClass", "()V", "scrudbeans-integration-tests-kotlin"})
@io.swagger.v3.oas.annotations.OpenAPIDefinition(tags = {@io.swagger.v3.oas.annotations.tags.Tag(name = "Order Lines", description = "Search, create and delete order lines")})
@org.springframework.web.bind.annotation.RequestMapping(value = {"/api/rest/orderLines"})
@org.springframework.web.bind.annotation.RestController()
public class OrderLineController extends com.github.manosbatsis.scrudbeans.controller.AbstractJpaEntityController<mykotlinpackage.model.OrderLine, java.util.UUID, mykotlinpackage.service.OrderLineService, error.NonExistentClass> {
    
    public OrderLineController() {
        super();
    }
}