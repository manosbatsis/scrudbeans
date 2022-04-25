package com.github.manosbatsis.scrudbeans.test;

import com.github.manosbatsis.scrudbeans.api.util.ParamsAwarePage;
import lombok.Data;
import org.apache.commons.lang3.NotImplementedException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * An basic {@link Page} implementation used for JSON deserialization during tests, e.g.:
 * <code>
 * // Somewhere in your test class:
 * public static class ProductsPage extends TestableParamsAwarePage<Product> { }
 *
 * // Later in a test method, using restassured:
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
@Data
public class TestableParamsAwarePage<T> implements ParamsAwarePage<T> {

	private int size;

	private int number;

	private int totalPages;

	private int numberOfElements;

	private long totalElements;

	private Map<String, String[]> parameters;

	private List<T> content;


	@Override
	public boolean hasContent() {
		return this.content != null && !this.content.isEmpty();
	}

	@Override
	public Sort getSort() {
		return Sort.by(Sort.DEFAULT_DIRECTION);
	}

	@Override
	public boolean isFirst() {
		return this.number == 0;
	}

	@Override
	public boolean hasNext() {
		return getNumber() + 1 < getTotalPages();
	}


	@Override
	public boolean hasPrevious() {
		return !isFirst();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#nextPageable()
	 */
	public Pageable nextPageable() {
		return hasNext() ? PageRequest.of(this.number, this.size, Sort.DEFAULT_DIRECTION).next() : Pageable.unpaged();
	}

	/*
	 * (non-Javadoc)
	 * @see org.springframework.data.domain.Slice#previousPageable()
	 */
	public Pageable previousPageable() {
		return hasPrevious() ? PageRequest.of(this.number, this.size, Sort.DEFAULT_DIRECTION).previousOrFirst() : Pageable.unpaged();
	}


	@Override
	public boolean isLast() {
		return !hasNext();
	}

	@Override
	public <U> Page<U> map(Function<? super T, ? extends U> converter) {
		throw new NotImplementedException("Non-implemented");
	}

	/**
	 * Returns an iterator over elements of type {@code T}.
	 *
	 * @return an Iterator.
	 */
	@Override
	public Iterator<T> iterator() {
		return this.getContent().iterator();
	}
}
