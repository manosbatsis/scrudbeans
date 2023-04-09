package mykotlinpackage.test

import com.github.manosbatsis.scrudbeans.logging.RequestResponseLoggingInterceptor
import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistry
import com.github.manosbatsis.scrudbeans.test.parameterizedTypeReference
import com.github.manosbatsis.scrudbeans.util.RestResponsePage
import mykotlinpackage.ScrudBeansSampleApplication
import mykotlinpackage.model.*
import mykotlinpackage.service.OrderService
import mykotlinpackage.service.ProductService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.data.domain.Page
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.client.BufferingClientHttpRequestFactory
import org.springframework.http.client.OkHttp3ClientHttpRequestFactory
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [ScrudBeansSampleApplication::class],
    webEnvironment = RANDOM_PORT,
)
@EnableJpaAuditing
class RestServicesIT {

    companion object {
        private val logger = LoggerFactory.getLogger(RestServicesIT::class.java)
    }
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Autowired lateinit var restTemplateOrig: TestRestTemplate

    @Autowired lateinit var productService: ProductService

    @Autowired lateinit var orderService: OrderService

    @Autowired lateinit var identifierAdapterRegistry: IdentifierAdapterRegistry

    val restTemplate: TestRestTemplate by lazy {
        // Buffer for logging and add support for PATCH
        restTemplateOrig.restTemplate.requestFactory = BufferingClientHttpRequestFactory(OkHttp3ClientHttpRequestFactory())
        restTemplateOrig.restTemplate.interceptors.add(RequestResponseLoggingInterceptor())
        restTemplateOrig
    }

    @Test
    fun testScrud() {
        val orderIdAdapter = identifierAdapterRegistry.getServiceForEntityType(Order::class.java).identifierAdapter
        // Test Search
        // ============================
        // Get the lord of the rings trilogy as a page of results
        // using a prefixed wildcard search
        val products = restTemplate.exchange(
            "/api/rest/products?filter=name=like=LOTR ",
            HttpMethod.GET,
            null,
            parameterizedTypeReference<RestResponsePage<Product>>(),
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // expecting three books
        assertTrue(products.content.size == 3)

        // Test Create
        // ============================
        // We have no auth mechanism by default, so we'll
        // create and use an actual order as a shopping basket
        val email = "foo@bar.baz"
        val transientOrder = Order(email = email)
        restTemplate.exchange(
            "/api/rest/orders",
            HttpMethod.POST,
            HttpEntity(transientOrder),
            Order::class.java,
        ).let {
            logger.info("testScrud 1, POST response: {}", it.body)
            assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.id).isEqualTo(transientOrder.id)
            it.body!!
        }
        val createdOrder: Order = restTemplate.exchange(
            "/api/rest/orders/${transientOrder.id}",
            HttpMethod.GET,
            null,
            Order::class.java,
        ).let {
            logger.info("testScrud 2, GET response: {}", it.body)
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.version).isEqualTo(0)
            it.body!!
        }
        // Test Update
        // ============================
        // 1) Update email and test result
        createdOrder.email = createdOrder.email + "_updated"
        restTemplate.exchange(
            "/api/rest/orders/${createdOrder.id}",
            HttpMethod.PUT,
            HttpEntity(createdOrder),
            Order::class.java,
        ).let {
            logger.info("testScrud 3, PUT response: {}", it.body)
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            assertThat(it.body).isNotNull
            assertThat(it.body!!.version).isGreaterThan(createdOrder.version)
            assertThat(it.body!!.created).isEqualTo(createdOrder.created)
            assertThat(it.body!!.updated).isAfter(createdOrder.updated)
            assertThat(it.body!!.email).isEqualTo(email + "_updated")
            it.body!!
        }

        // 1) test reloaded resource...
        val updatedOrder = restTemplate.exchange(
            "/api/rest/orders/${createdOrder.id}",
            HttpMethod.GET,
            null,
            Order::class.java,
        ).let {
            logger.info("testScrud 4, GET response: {}", it.body)
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.version).isGreaterThan(createdOrder.version)
            assertThat(it.body!!.created).isEqualTo(createdOrder.created)
            assertThat(it.body!!.updated).isAfter(createdOrder.updated)
            assertThat(it.body!!.email).isEqualTo(email + "_updated")
            it.body!!
        }

        // Test Patch
        // ============================
        // Prepare the patch
        val orderMap: MutableMap<String, Any> = HashMap()
        orderMap.put("id", updatedOrder.id)
        orderMap["email"] = updatedOrder.email + "_patched"
        // Submit the patch
        restTemplate.exchange(
            "/api/rest/orders/${orderIdAdapter.getId(updatedOrder)}",
            HttpMethod.PATCH,
            HttpEntity(orderMap),
            Order::class.java,
        ).let {
            logger.info("testScrud 5, PATCH response: {}", it.body)
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.email).isEqualTo(email + "_updated_patched")
            it.body!!
        }
        // Test Read
        // ============================
        // verify order was created and can be retrieved

        val patchedOrder = restTemplate.exchange(
            "/api/rest/orders/${orderIdAdapter.getId(updatedOrder)}",
            HttpMethod.GET,
            null,
            Order::class.java,
        ).let {
            logger.info("testScrud 6, GET response: {}", it.body)
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.email).isEqualTo(email + "_updated_patched")
            it.body!!
        }
        // Add order items (lines)
        val quantity = 2
        val orderLineProducs = products.content
        for (p in orderLineProducs) {
            val orderLine: OrderLine = OrderLine(order = patchedOrder, product = p, quantity = quantity)
            logger.debug("Saving order line: $orderLine for order $patchedOrder")

            restTemplate.exchange(
                "/api/rest/orderLines",
                HttpMethod.POST,
                HttpEntity(orderLine),
                OrderLine::class.java,
            ).let {
                assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
                assertThat(it.body).isNotNull
                it.body!!
            }
        }

        // Get a page of order lines
        logger.debug("Search for order lines")
        var orderLines = restTemplate.exchange(
            "/api/rest/orderLines",
            HttpMethod.GET,
            null,
            parameterizedTypeReference<RestResponsePage<OrderLine>>(),
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // Load a page of orders made today
        val localDate = LocalDate.now(ZoneId.systemDefault())
        var startOfDay = OffsetDateTime.of(localDate.atStartOfDay(), ZoneOffset.UTC).toLocalDateTime()
        var endOfDay = OffsetDateTime.of(localDate.atTime(23, 59, 59), ZoneOffset.UTC).toLocalDateTime()

        // Test RSQL Search
        // ============================

        logger.debug("Search for orders")
        var ordersOfTheDay = restTemplate.exchange(
            "/api/rest/orders?filter=created=ge=$startOfDay;created=le=$endOfDay",
            HttpMethod.GET,
            null,
            parameterizedTypeReference<RestResponsePage<Order>>(),
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // expecting 2 orders, one created on startup and one from this test
        assertEquals(2, ordersOfTheDay.totalElements)
        // try the same dates for the following year
        ordersOfTheDay = restTemplate.exchange(
            "/api/rest/orders?filter=created=ge=${startOfDay.plusYears(1)};created=le=${endOfDay.plusYears(1)}",
            HttpMethod.GET,
            null,
            parameterizedTypeReference<RestResponsePage<Order>>(),
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // expecting 0 orders as the date range is set to the future
        assertEquals(0, ordersOfTheDay.totalElements)

        // Test child endpoint
        // ============================
        val firstOrderLine = orderLines.first()
        val firstOrderLineProduct = restTemplate.exchange(
            "/api/rest/orderLines/${firstOrderLine.id}/product",
            HttpMethod.GET,
            null,
            Product::class.java,
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertEquals(firstOrderLine.product, firstOrderLineProduct)

        // Test Service component API
        // ============================
        val orderInfosPage: Page<OrderInfo> = orderService.findAll(
            filter = "created=ge=$startOfDay;created=le=$endOfDay",
            sortBy = "created",
            sortDirection = Sort.Direction.ASC,
            pageNumber = 0,
            pageSize = 10,
            // projection = OrderInfo::class.java
        ).map { OrderInfo(it.email, it.comment) }
        assertThat(orderInfosPage.content).isNotEmpty
        assertEquals(2, orderInfosPage.totalElements)
        assertThat(orderInfosPage.content.last().email).isEqualTo(patchedOrder.email)
    }

    @Test
    fun testScrudForIdType() {
        logger.info("Create some customers")
        for (i in 1..3) {
            val customer = Customer(name = "User$i Name$i", phoneNumber = "1234556789$i", address = "Foo Av. $i Bar, Baz")

            logger.debug("Saving customer: $customer")

            restTemplate.exchange(
                "/api/rest/customers",
                HttpMethod.POST,
                HttpEntity(customer),
                Customer::class.java,
            ).let {
                assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
                assertThat(it.body).isNotNull
                it.body!!
            }
        }

        logger.info("Search customers")
        val customersPage = restTemplate.exchange(
            "/api/rest/customers?filter=name=like=Name1",
            HttpMethod.GET,
            null,
            parameterizedTypeReference<RestResponsePage<Customer>>(),
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertThat(customersPage.content.size).isEqualTo(1)
        var customer1 = customersPage.first()
        assertThat(customer1.address).isEqualTo("Foo Av. 1 Bar, Baz")
        val customerIdentifierAdapter = identifierAdapterRegistry.getServiceForEntityType(Customer::class.java)
        customer1 = restTemplate.exchange(
            "/api/rest/customers/${customerIdentifierAdapter.identifierAdapter.getIdAsString(customer1)}",
            HttpMethod.GET,
            null,
            Customer::class.java,
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // Update a customer
        val updatedAddress = "${customer1.address} updated"
        customer1.address = updatedAddress
        customer1 = restTemplate.exchange(
            "/api/rest/customers/${customerIdentifierAdapter.identifierAdapter.getIdAsString(customer1)}",
            HttpMethod.PUT,
            HttpEntity(customer1),
            Customer::class.java,
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // Validate customer update
        customer1 = restTemplate.exchange(
            "/api/rest/customers/${customerIdentifierAdapter.identifierAdapter.getIdAsString(customer1)}",
            HttpMethod.GET,
            null,
            Customer::class.java,
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertThat(customer1.address).isEqualTo(updatedAddress)
    }

    @Test
    fun testScrudForEmbeddedId() {
        // Get LOTR books
        val products = restTemplate.exchange(
            "/api/rest/products?filter=name=like=LOTR ",
            HttpMethod.GET,
            null,
            parameterizedTypeReference<RestResponsePage<Product>>(),
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!.content
        }
        // Create ProductRelationship for each combination
        val relIdAdapter = identifierAdapterRegistry.getServiceForEntityType(ProductRelationship::class.java).identifierAdapter
        for (leftProduct in products) {
            for (rightProduct in products) {
                if (leftProduct != rightProduct) { // Test Create
                    val id = ProductRelationshipIdentifier(
                        left = leftProduct,
                        right = rightProduct,
                    )
                    val description = "Part of LOTR trilogy"
                    var relationship = ProductRelationship(id = id, description = description)
                    try {
                        logger.debug("Saving product relationship: $relationship")
                    } catch (e: Throwable) {
                        e.printStackTrace()
                        throw e
                    }

                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships",
                        HttpMethod.POST,
                        HttpEntity(relationship),
                        ProductRelationship::class.java,
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }

                    // Test Update
                    relationship.description = "${relationship.description}_updated"
                    logger.debug("Look up relationship $relationship")
                    val relationshipId = relIdAdapter.getIdAsString(relationship)
                    logger.debug("Look up relationship for ID: $relationshipId")
                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships/$relationshipId",
                        HttpMethod.PUT,
                        HttpEntity(relationship),
                        ProductRelationship::class.java,
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }
                    // Test Patch
                    val patch: MutableMap<String, Any> = HashMap()
                    patch["description"] = "${relationship.description}_patched"
                    restTemplate.exchange(
                        "/api/rest/productRelationships/$relationshipId",
                        HttpMethod.PATCH,
                        HttpEntity(patch),
                        ProductRelationship::class.java,
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }
                    // Test Read
                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships/$relationshipId",
                        HttpMethod.GET,
                        null,
                        ProductRelationship::class.java,
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }
                    assertEquals(description + "_updated_patched", relationship.description)
                }
            }
        }
    }
}
