package myjavapackage.test;

import static io.restassured.RestAssured.given;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.manosbatsis.scrudbeans.test.AbstractRestAssueredIT;
import com.github.manosbatsis.scrudbeans.test.TestableParamsAwarePage;
import lombok.extern.slf4j.Slf4j;
import myjavapackage.ScrudBeansSampleApplication;
import myjavapackage.model.Order;
import myjavapackage.model.OrderLine;
import myjavapackage.model.Product;
import myjavapackage.model.ProductRelationship;
import myjavapackage.model.ProductRelationshipIdentifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScrudBeansSampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestServicesIT extends AbstractRestAssueredIT {


	@Test
	public void testGetAll() {
		// Get all products, i.e. without paging
		Product[] products = given()
				.spec(defaultSpec())
				.queryParam("page", "no")
				.get("/api/rest/products")
				.then()
				.statusCode(200).extract().as(Product[].class);
		// expecting at least one order created on application startup
		assertNotNull(products);
		assertTrue(products.length > 0);
	}

	@Test
	public void testScrud() {

		// Test Search
		//============================
		// Get the lord of the rings trilogy as a page of results
		// using a prefixed wildcard search
		ProductsPage products = given()
				.spec(defaultSpec())
				.queryParam("name", "LOTR %")
				.get("/api/rest/products")
				.then()
				.statusCode(200).extract().as(ProductsPage.class);
		// expecting three books
		assertTrue(products.getContent().size() == 3);

		// Test Create
		//============================
		// We have no auth mechanism by default, so we'll
		// create and use an actual order as a shopping basket
		String email = "foo@bar.baz";
		Order order = Order.builder().email(email).build();
		order = given()
				.spec(defaultSpec())
				.body(order)
				.post("/api/rest/orders")
				.then()
				.statusCode(201).extract().as(Order.class);
		// Test Update
		//============================
		order.setEmail(order.getEmail() + "_updated");
		order = given()
				.spec(defaultSpec())
				.body(order)
				.put("/api/rest/orders/" + order.getId())
				.then()
				.statusCode(200).extract().as(Order.class);
		assertEquals(email + "_updated", order.getEmail());
		// Test Patch
		//============================
		// Prepare the patch
		Map<String, Object> orderMap = new HashMap<>();
		orderMap.put("id", order.getId());
		orderMap.put("email", order.getEmail() + "_patched");
		// Submit the patch
		order = given()
				.spec(defaultSpec())
				.body(orderMap)
				.put("/api/rest/orders/" + order.getId())
				.then()
				.statusCode(200).extract().as(Order.class);
		assertEquals(email + "_updated_patched", order.getEmail());

		// Test Read
		//============================
		// verify order was created and can be retrieved
		order = given()
				.spec(defaultSpec())
				.get("/api/rest/orders/" + order.getId())
				.then()
				.statusCode(200).extract().as(Order.class);
		assertEquals(email + "_updated_patched", order.getEmail());

		// Add order items (lines)
		for (Product p : products) {

			OrderLine orderLine = OrderLine.builder()
					.order(order)
					.product(p)
					.quantity(2).build();
			given()
					.spec(defaultSpec())
					.body(orderLine)
					.post("/api/rest/orderLines")
					.then()
					.statusCode(201).extract().as(OrderLine.class);
		}

		// Load a page of orders made today
		LocalDate localDate = LocalDate.now(ZoneId.systemDefault());
		LocalDateTime startOfDay = localDate.atStartOfDay();
		LocalDateTime endOfDay = localDate.atTime(LocalTime.MAX);

		// Test RSQL Search
		//============================
		OrdersPage ordersOfTheDay = given()
				.spec(defaultSpec())
				// >= day-start and <= day-end
				.param("filter",
						"createdDate=ge=" + startOfDay + ";createdDate=le=" + endOfDay)
				.get("/api/rest/orders")
				.then()
				.statusCode(200).extract().as(OrdersPage.class);

		// expecting 2 orders, one created on startup and one from this test
		assertEquals(2, ordersOfTheDay.getTotalElements());

		// try the same dates for the following year
		startOfDay = startOfDay.plusYears(1);
		endOfDay = endOfDay.plusYears(1);
		ordersOfTheDay = given()
				.spec(defaultSpec())
				// >= day-start and <= day-end
				.param("filter",
						"createdDate=ge=" + startOfDay + ";createdDate=le=" + endOfDay)
				.get("/api/rest/orders")
				.then()
				.statusCode(200).extract().as(OrdersPage.class);

		// expecting 0 orders as the date range is set to the future
		assertEquals(0, ordersOfTheDay.getTotalElements());

	}

	@Test
	public void testScrudForEmbeddedId() {

		// Get LOTR books
		ProductsPage products = given()
				.spec(defaultSpec())
				.queryParam("name", "LOTR %")
				.get("/api/rest/products")
				.then()
				.statusCode(200).extract().as(ProductsPage.class);
		// Create ProductRelationship for each combination
		for (Product leftProduct : products) {
			for (Product rightProduct : products) {
				if (!leftProduct.equals(rightProduct)) {
					// Test Create
					ProductRelationshipIdentifier id = new ProductRelationshipIdentifier();
					id.setLeft(leftProduct);
					id.setRight(rightProduct);
					String description = "Part of LOTR trilogy";
					ProductRelationship relationship = new ProductRelationship();
					relationship.setId(id);
					relationship.setDescription(description);
					relationship = given()
							.spec(defaultSpec())
							.body(relationship)
							.post("/api/rest/productRelationships")
							.then()
							.statusCode(201).extract().as(ProductRelationship.class);
					// Test Update
					relationship.setDescription(relationship.getDescription() + "_updated");
					relationship = given()
							.spec(defaultSpec())
							.body(relationship)
							.put("/api/rest/productRelationships" + '/' + relationship.getId())
							.then()
							.statusCode(200).extract().as(ProductRelationship.class);
					// Test Patch
					Map<String, Object> patch = new HashMap<>();
					patch.put("description", relationship.getDescription() + "_patched");
					relationship = given()
							.spec(defaultSpec())
							.body(patch)
							.put("/api/rest/productRelationships" + '/' + relationship.getId())
							.then()
							.statusCode(200).extract().as(ProductRelationship.class);
					// Test Read
					relationship = given()
							.spec(defaultSpec())
							.get("/api/rest/productRelationships" + '/' + relationship.getId())
							.then()
							.statusCode(200).extract().as(ProductRelationship.class);
					assertEquals(description + "_updated_patched", relationship.getDescription());
				}
			}
		}

	}

	@Test
	public void testJsonSchema() throws Exception {
		// --------------------------------
		// Gwt user schema
		// --------------------------------
		JsonNode schema = given().spec(defaultSpec())
				.log().all()
				.get("/api/rest/products/jsonschema")
				.then()
				.log().all()
				.assertThat()
				.statusCode(200)
				// get model
				.extract().as(JsonNode.class);
		log.debug("testJsonSchema: \n{}", schema.toString());
	}

	// Help RestAssured's mapping
	public static class ProductsPage extends TestableParamsAwarePage<Product> {
	}

	public static class OrdersPage extends TestableParamsAwarePage<Order> {
	}

}
