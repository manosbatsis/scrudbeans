package com.github.manosbatsis.scrudbeans.test;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * An basic {@link Page} implementation used for JSON deserialization during tests, e.g.:
 * <code>
 * // Somewhere in your test class:
 * public static class ProductsPage extends TestableParamsAwarePage<Product> { }
 *
 * // Later in a test method, e.g. using restassured:
 * ProductsPage products = given()
 * 		.spec(defaultSpec())
 * 		.queryParam("name", "LOTR %")
 * 		.get("/products")
 * 		.then()
 * 		.statusCode(200).extract().as(ProductsPage.class);
 * </code>
 *
 * @param <T> the model type
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestableParamsAwarePage<T> extends PageImpl<T> {

	private Map<String, String[]> parameters;

	@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
	public TestableParamsAwarePage(@JsonProperty("content") List<T> content,
							@JsonProperty("number") int number,
							@JsonProperty("size") int size,
							@JsonProperty("totalElements") Long totalElements,
							@JsonProperty("pageable") JsonNode pageable,
							@JsonProperty("last") boolean last,
							@JsonProperty("totalPages") int totalPages,
							@JsonProperty("sort") JsonNode sort,
							@JsonProperty("first") boolean first,
							@JsonProperty("numberOfElements") int numberOfElements,
							Map<String, String[]> parameters
	) {
		super(content, PageRequest.of(number, size), totalElements);
		this.parameters = parameters;
	}

	public TestableParamsAwarePage(List<T> content, Pageable pageable, long total) {
		super(content, pageable, total);
	}

	public TestableParamsAwarePage(List<T> content) {
		super(content);
	}

	public TestableParamsAwarePage() {
		super(new ArrayList<>());
	}

	public Map<String, String[]> getParameters() {
		return parameters;
	}

	public void setParameters(Map<String, String[]> parameters) {
		this.parameters = parameters;
	}
}