package mykotlinpackage.test

import com.fasterxml.jackson.databind.JsonNode
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.logging.RequestResponseLoggingInterceptor
import com.github.manosbatsis.scrudbeans.test.TestableParamsAwarePage
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
import org.springframework.http.client.SimpleClientHttpRequestFactory
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.OffsetDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.*

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    classes = [ScrudBeansSampleApplication::class],
    webEnvironment = RANDOM_PORT
)
@EnableJpaAuditing
class RestServicesIT(

    @Autowired val restTemplateOrig: TestRestTemplate,
    @Autowired val productService: ProductService,
    @Autowired val orderService: OrderService
) {


    companion object {
        val log = LoggerFactory.getLogger(RestServicesIT.javaClass)
    }
    init {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    val restTemplate: TestRestTemplate by lazy {
        restTemplateOrig.restTemplate.requestFactory = BufferingClientHttpRequestFactory(SimpleClientHttpRequestFactory())
        restTemplateOrig.restTemplate.interceptors.add(RequestResponseLoggingInterceptor())
        restTemplateOrig
    }

    @Test
    fun testScrud() {
        val orderIdAdapter = OrderIdentifierAdapter
        // Test Search
        //============================
        // Get the lord of the rings trilogy as a page of results
        // using a prefixed wildcard search
        val products = restTemplate.exchange(
            "/api/rest/products?filter=name=like=LOTR ", HttpMethod.GET,
            null,
            ProductsPage::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // expecting three books
        assertTrue(products.content.size == 3)
        // Test Create
        //============================
        // We have no auth mechanism by default, so we'll
        // create and use an actual order as a shopping basket
        val email = "foo@bar.baz"
        var order: Order = Order(email = email)
        val orderId = order.id
        order = restTemplate.exchange(
            "/api/rest/orders", HttpMethod.POST,
            HttpEntity(order),
            Order::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.id).isEqualTo(orderId)
            it.body!!
        }
        restTemplate.exchange(
            "/api/rest/orders/${order.id}", HttpMethod.GET,
            null,
            Order::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            assertThat(it.body!!.version).isEqualTo(order.version)
            it.body!!
        }
        // Test Update
        //============================
        order.email = order.email.toString() + "_updated"
        // 1) test update result...
        order = restTemplate.exchange(
            "/api/rest/orders/${order.id}", HttpMethod.PUT,
            HttpEntity(order),
            Order::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertEquals(email + "_updated", order.email)
        // 1) test reloaded resource...
        order = restTemplate.exchange(
            "/api/rest/orders/${order.id}", HttpMethod.GET,
            null,
            Order::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertEquals(email + "_updated", order.email)

        // Test Patch
        //============================
        // Prepare the patch
        val orderMap: MutableMap<String, Any> = HashMap()
        orderMap.put("id", order.id!!)
        orderMap["email"] = order.email.toString() + "_patched"
        // Submit the patch
        order = restTemplate.exchange(
            "/api/rest/orders/${orderIdAdapter.readId(order)}", HttpMethod.PUT,
            HttpEntity(orderMap),
            Order::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertEquals(email + "_updated_patched", order.email)
        // Test Read
        //============================
        // verify order was created and can be retrieved

        order = restTemplate.exchange(
            "/api/rest/orders/${orderIdAdapter.readId(order)}", HttpMethod.GET,
            null,
            Order::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertEquals(email + "_updated_patched", order.email)
        // Add order items (lines)
        val quantity = 2
        val orderLineProducs = products.content
        for (p in orderLineProducs) {

            val orderLine: OrderLine = OrderLine(order = order, product = p, quantity = quantity)
            log.debug("Saving order line: $orderLine for order $order")

            restTemplate.exchange(
                "/api/rest/orderLines", HttpMethod.POST,
                HttpEntity(orderLine),
                OrderLine::class.java
            ).let {
                assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
                assertThat(it.body).isNotNull
                it.body!!
            }
        }

        // Get a page of order lines
        log.debug("Search for order lines")
        var orderLines = restTemplate.exchange(
            "/api/rest/orderLines", HttpMethod.GET,
            null,
            OrderLinesPage::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // Load a page of orders made today
        val localDate = LocalDate.now(ZoneId.systemDefault())
        var startOfDay = OffsetDateTime.of(localDate.atStartOfDay(), ZoneOffset.UTC)
        var endOfDay = OffsetDateTime.of(localDate.atTime(23, 59, 59), ZoneOffset.UTC)

        // Test RSQL Search
        //============================

        log.debug("Search for orders")
        var ordersOfTheDay = restTemplate.exchange(
            "/api/rest/orders?filter=created=ge=${startOfDay};created=le=${endOfDay}", HttpMethod.GET,
            null,
            JsonNode::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // expecting 2 orders, one created on startup and one from this test
        //assertEquals(2, ordersOfTheDay.totalElements)
        // try the same dates for the following year
        ordersOfTheDay = restTemplate.exchange(
            "/api/rest/orders?filter=created=ge=${startOfDay.plusYears(1)};created=le=${endOfDay.plusYears(1)}", HttpMethod.GET,
            null,
            JsonNode::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // expecting 0 orders as the date range is set to the future
        //assertEquals(0, ordersOfTheDay.totalElements)

        // Test child endpoint
        //============================
        val firstOrderLine = orderLines.first()
        val firstOrderLineProduct = restTemplate.exchange(
            "/api/rest/orderLines/${firstOrderLine.id}/product", HttpMethod.GET,
            null,
            Product::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        assertEquals(firstOrderLine.product, firstOrderLineProduct)

        // Test Service component API
        //============================
        val orderInfosPage: Page<OrderInfo> = orderService.findAll(
            filter = "created=ge=${startOfDay};created=le=${endOfDay}",
            sortBy = "created",
            sortDirection = Sort.Direction.ASC,
            pageNumber = 0,
            pageSize = 10,
            //projection = OrderInfo::class.java
        ).map { OrderInfo(it.email, it.comment) }
        assertThat(orderInfosPage.content).isNotEmpty
        assertEquals(2, orderInfosPage.totalElements)
        assertThat(orderInfosPage.content.last().email).isEqualTo(order.email)
    }

    @Test
    fun testScrudForEmbeddedId() { // Get LOTR books
        val products = restTemplate.exchange(
            "/api/rest/products?filter=name=like=LOTR ", HttpMethod.GET,
            null,
            ProductsPage::class.java
        ).let {
            assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
            assertThat(it.body).isNotNull
            it.body!!
        }
        // Create ProductRelationship for each combination
        val relIdAdapter: IdentifierAdapter<ProductRelationship, ProductRelationshipIdentifier> = ProductRelationshipIdentifierAdapter
        for (leftProduct in products) {
            for (rightProduct in products) {
                if (leftProduct != rightProduct) { // Test Create
                    val id = ProductRelationshipIdentifier()
                        .apply {
                            left = leftProduct
                            right = rightProduct
                        }
                    val description = "Part of LOTR trilogy"
                    var relationship = ProductRelationship(id = id, description = description)
                    try {
                        log.debug("Saving product relationship: $relationship")
                    }catch (e: Throwable){
                        e.printStackTrace()
                        throw e
                    }

                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships", HttpMethod.POST,
                        HttpEntity(relationship),
                        ProductRelationship::class.java
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.CREATED)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }

                    // Test Update
                    relationship.description = "${relationship.description}_updated"
                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships/${relationship.id.toStringRepresentation()}", HttpMethod.PUT,
                        HttpEntity(relationship),
                        ProductRelationship::class.java
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }
                    // Test Patch
                    val patch: MutableMap<String, Any> = HashMap()
                    patch["description"] = "${relationship.description}_patched"
                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships/${relIdAdapter.readId(relationship)}", HttpMethod.PUT,
                        HttpEntity(patch),
                        ProductRelationship::class.java
                    ).let {
                        assertThat(it.statusCode).isEqualTo(HttpStatus.OK)
                        assertThat(it.body).isNotNull
                        it.body!!
                    }
                    // Test Read
                    relationship = restTemplate.exchange(
                        "/api/rest/productRelationships/${relIdAdapter.readId(relationship)}", HttpMethod.GET,
                        null,
                        ProductRelationship::class.java
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


    // Help RestAssured's mapping
    class ProductsPage : TestableParamsAwarePage<Product>()

    class OrdersPage : TestableParamsAwarePage<Order>()

    class OrderLinesPage : TestableParamsAwarePage<OrderLine>()
}