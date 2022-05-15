package myjavapackage.test;

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.api.mdd.registry.IdentifierAdaptersRegistry;
import com.github.manosbatsis.scrudbeans.logging.RequestResponseLoggingInterceptor;
import com.github.manosbatsis.scrudbeans.test.TestableParamsAwarePage;
import lombok.extern.slf4j.Slf4j;
import myjavapackage.ScrudBeansSampleApplication;
import myjavapackage.model.Order;
import myjavapackage.model.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ScrudBeansSampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RestServicesIT {
	TestRestTemplate restTemplate;

	@Autowired
	public void setRestTemplate(TestRestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.restTemplate.getRestTemplate().setRequestFactory(new BufferingClientHttpRequestFactory(new SimpleClientHttpRequestFactory()));
		this.restTemplate.getRestTemplate().getInterceptors().add(new RequestResponseLoggingInterceptor());
	}

	@Test
	void testScrud() {
		IdentifierAdapter orderIdAdapter = IdentifierAdaptersRegistry.getAdapterForClass(Order.class);
		// Test Search
		//============================
		// Get the lord of the rings trilogy as a page of results
		// using a prefixed wildcard search
		ResponseEntity<ProductsPage> productsPageResponseEntity = restTemplate.exchange(
				"/api/rest/products?filter=name=like=LOTR ", HttpMethod.GET,
				null,
				ProductsPage.class);
		assertThat(productsPageResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(productsPageResponseEntity.getBody()).isNotNull();
		ProductsPage products = productsPageResponseEntity.getBody();

		// expecting three books
		assertTrue(products.getContent().size() == 3);

		// Test Create
		//============================
		// We have no auth mechanism by default, so we'll
		// create and use an actual order as a shopping basket
		String email = "foo@bar.baz";
		Order order = new Order();
		order.setEmail(email);
		ResponseEntity<Order> orderResponseEntity = restTemplate.exchange(
				"/api/rest/orders", HttpMethod.POST,
				new HttpEntity(order),
				Order.class);
		assertThat(orderResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
		assertThat(orderResponseEntity.getBody()).isNotNull();
		order = orderResponseEntity.getBody();
		// Test Update
		//============================
		order.setEmail(order.getEmail() + "_updated");
		// 1) test update result...
		orderResponseEntity = restTemplate.exchange(
				"/api/rest/orders/"+order.getId(), HttpMethod.PUT,
				new HttpEntity(order),
				Order.class);
		assertThat(orderResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(orderResponseEntity.getBody()).isNotNull();
		order = orderResponseEntity.getBody();
		assertEquals(email + "_updated", order.getEmail());
		// 1) test reloaded resource...
		orderResponseEntity = restTemplate.exchange(
				"/api/rest/orders/"+order.getId(), HttpMethod.GET,
				null, Order.class);
		assertThat(orderResponseEntity.getStatusCode()).isEqualTo(HttpStatus.OK);
		assertThat(orderResponseEntity.getBody()).isNotNull();
		order = orderResponseEntity.getBody();
		assertEquals(email + "_updated", order.getEmail());
	}

	// Help RestAssured's mapping
	public static class ProductsPage extends TestableParamsAwarePage<Product> {
	}

	public static class OrdersPage extends TestableParamsAwarePage<Order> {
	}

}
