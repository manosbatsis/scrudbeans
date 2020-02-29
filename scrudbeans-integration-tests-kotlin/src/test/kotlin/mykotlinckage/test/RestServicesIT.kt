package mykotlinpackage.test

import com.fasterxml.jackson.databind.JsonNode
import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry
import com.github.manosbatsis.scrudbeans.test.AbstractRestAssuredIT
import com.github.manosbatsis.scrudbeans.test.TestableParamsAwarePage
import io.restassured.RestAssured
import mykotlinpackage.ScrudBeansSampleApplication
import mykotlinpackage.model.Order
import mykotlinpackage.model.OrderLine
import mykotlinpackage.model.Product
import mykotlinpackage.model.ProductRelationship
import mykotlinpackage.model.ProductRelationshipIdentifier
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.util.HashMap

@ExtendWith(SpringExtension::class)
@SpringBootTest(classes = [ScrudBeansSampleApplication::class], webEnvironment = RANDOM_PORT)
class RestServicesIT : AbstractRestAssuredIT() {

    companion object {
        val log = LoggerFactory.getLogger(RestServicesIT.javaClass)
    }

    @Test
    fun testGetAll() { // Get all products, i.e. without paging
        val products: Array<Product> = RestAssured.given()
                .spec(defaultSpec())
                .queryParam("page", "no")["/api/rest/products"]
                .then()
                .statusCode(200).extract().`as`<Array<Product>>(Array<Product>::class.java)
        // expecting at least one order created on application startup
        Assertions.assertNotNull(products)
        Assertions.assertTrue(products.size > 0)
    }

    @Test
    fun testScrud() {
        val orderIdAdapter = IdentifierAdaptersRegistry.getAdapterForClass(Order::class.java)
        // Test Search
        //============================
        // Get the lord of the rings trilogy as a page of results
        // using a prefixed wildcard search
        val products = RestAssured.given()
                .spec(defaultSpec())
                .queryParam("name", "LOTR %")["/api/rest/products"]
                .then()
                .statusCode(200).extract().`as`(ProductsPage::class.java)
        // expecting three books
        Assertions.assertTrue(products.content.size == 3)
        // Test Create
        //============================
        // We have no auth mechanism by default, so we'll
        // create and use an actual order as a shopping basket
        val email = "foo@bar.baz"
        var order: Order = Order(email = email)
        order = RestAssured.given()
                .spec(defaultSpec())
                .body(order)
                .post("/api/rest/orders")
                .then()
                .statusCode(201).extract().`as`(Order::class.java)
        // Test Update
        //============================
        order.email = order.email.toString() + "_updated"
        order = RestAssured.given()
                .spec(defaultSpec())
                .body(order)
                .put("/api/rest/orders/" + order.id)
                .then()
                .statusCode(200).extract().`as`(Order::class.java)
        assertEquals(email + "_updated", order.email)

        // Test Patch
        //============================
        // Prepare the patch
        val orderMap: MutableMap<String, Any> = HashMap()
        orderMap.put("id", order.id!!)
        orderMap["email"] = order.email.toString() + "_patched"
        // Submit the patch
        order = RestAssured.given()
                .spec(defaultSpec())
                .body(orderMap)
                .put("/api/rest/orders/" + orderIdAdapter.readId(order))
                .then()
                .statusCode(200).extract().`as`(Order::class.java)
        assertEquals(email + "_updated_patched", order.email)
        // Test Read
        //============================
        // verify order was created and can be retrieved
        order = RestAssured.given()
                .spec(defaultSpec())["/api/rest/orders/" + orderIdAdapter.readId(order)]
                .then()
                .statusCode(200).extract().`as`(Order::class.java)
        assertEquals(email + "_updated_patched", order.email)
        // Add order items (lines)
        val quantity = 2
        for (p in products) {

            val orderLine: OrderLine = OrderLine(order = order, product = p, quantity = quantity)
            log.debug("Saving order line: $orderLine")
            RestAssured.given()
                    .spec(defaultSpec())
                    .body(orderLine)
                    .post("/api/rest/orderLines")
                    .then()
                    .statusCode(201).extract().`as`(OrderLine::class.java)
        }
        // Load a page of orders made today
        val localDate = LocalDate.now(ZoneId.systemDefault())
        var startOfDay = localDate.atStartOfDay()
        var endOfDay = localDate.atTime(LocalTime.MAX)
        // Test RSQL Search
        //============================
        var ordersOfTheDay = RestAssured.given()
                .spec(defaultSpec()) // >= day-start and <= day-end
                .param("filter",
                        "createdDate=ge=$startOfDay;createdDate=le=$endOfDay")["/api/rest/orders"]
                .then()
                .statusCode(200).extract().`as`(OrdersPage::class.java)
        // expecting 2 orders, one created on startup and one from this test
        Assertions.assertEquals(2, ordersOfTheDay.totalElements)
        // try the same dates for the following year
        startOfDay = startOfDay.plusYears(1)
        endOfDay = endOfDay.plusYears(1)
        ordersOfTheDay = RestAssured.given()
                .spec(defaultSpec()) // >= day-start and <= day-end
                .param("filter",
                        "createdDate=ge=$startOfDay;createdDate=le=$endOfDay")["/api/rest/orders"]
                .then()
                .statusCode(200).extract().`as`(OrdersPage::class.java)
        // expecting 0 orders as the date range is set to the future
        Assertions.assertEquals(0, ordersOfTheDay.totalElements)
    }

    @Test
    fun testScrudForEmbeddedId() { // Get LOTR books
        val products = RestAssured.given()
                .spec(defaultSpec())
                .queryParam("name", "LOTR %")["/api/rest/products"]
                .then()
                .statusCode(200).extract().`as`(ProductsPage::class.java)
        // Create ProductRelationship for each combination
        val relIdAdapter: IdentifierAdapter<ProductRelationship, *> =
                IdentifierAdaptersRegistry.getAdapterForClass(ProductRelationship::class.java)!!
        for (leftProduct in products) {
            for (rightProduct in products) {
                if (!leftProduct!!.equals(rightProduct)) { // Test Create
                    val id = ProductRelationshipIdentifier()
                    id.setLeft(leftProduct)
                    id.setRight(rightProduct)
                    val description = "Part of LOTR trilogy"
                    var relationship = ProductRelationship(id = id, description = description)

                    relationship = RestAssured.given()
                            .spec(defaultSpec())
                            .body(relationship)
                            .post("/api/rest/productRelationships")
                            .then()
                            .statusCode(201).extract().`as`(ProductRelationship::class.java)
                    // Test Update
                    relationship.description = "${relationship.description}_updated"
                    relationship = RestAssured.given()
                            .spec(defaultSpec())
                            .body(relationship)
                            .put("/api/rest/productRelationships" + '/' + relIdAdapter.readId(relationship))
                            .then()
                            .statusCode(200).extract().`as`(ProductRelationship::class.java)
                    // Test Patch
                    val patch: MutableMap<String, Any> = HashMap()
                    patch["description"] = "${relationship.description}_patched"
                    relationship = RestAssured.given()
                            .spec(defaultSpec())
                            .body(patch)
                            .put("/api/rest/productRelationships" + '/' + relIdAdapter.readId(relationship))
                            .then()
                            .statusCode(200).extract().`as`(ProductRelationship::class.java)
                    // Test Read
                    relationship = RestAssured.given()
                            .spec(defaultSpec())["/api/rest/productRelationships" + '/' + relIdAdapter.readId(relationship)]
                            .then()
                            .statusCode(200).extract().`as`(ProductRelationship::class.java)
                    assertEquals(description + "_updated_patched", relationship.description)
                }
            }
        }
    }

    @Test
    @Throws(Exception::class)
    fun testJsonSchema() { // --------------------------------
        // Gwt user schema
        // --------------------------------
        val schema = RestAssured.given().spec(defaultSpec())
                .log().all()["/api/rest/products/jsonschema"]
                .then()
                .log().all()
                .assertThat()
                .statusCode(200) // get model
                .extract().`as`(JsonNode::class.java)
        log.debug("testJsonSchema: \n{}", schema.toString())
    }

    // Help RestAssured's mapping
    class ProductsPage : TestableParamsAwarePage<Product>()

    class OrdersPage : TestableParamsAwarePage<Order>()
}