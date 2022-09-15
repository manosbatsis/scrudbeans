// -------------------- DO NOT EDIT -------------------
//  This file is automatically generated by scrudbeans,
//  see https://manosbatsis.github.io/scrudbeans
//  To edit this file, copy it to the appropriate package 
//  in your src/main/kotlin folder and edit there. 
// ----------------------------------------------------
package mykotlinpackage.controller

import java.util.UUID
import mykotlinpackage.model.Product
import mykotlinpackage.model.ProductDto
import mykotlinpackage.service.ProductService
import org.springframework.web.bind.`annotation`.RequestMapping
import org.springframework.web.bind.`annotation`.RestController

@RestController(value = "productController")
@RequestMapping(value = ["/api/rest/products"])
public class ProductController :
    CustomJpaEntityController<Product, UUID, ProductService, ProductDto>()
