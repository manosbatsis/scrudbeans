package com.github.manosbatsis.scrudbeans.sample.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.restdude.mdd.annotation.model.ModelResource;
import com.restdude.mdd.model.AbstractSystemUuidPersistableModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ModelResource(
		pathFragment = Product.API_PATH_FRAGMENT,
		apiName = "Products",
		apiDescription = Product.API_MODEL_DESCRIPTION)
public class Product extends AbstractSystemUuidPersistableModel {

	public static final String API_PATH_FRAGMENT = "products";

	public static final String API_MODEL_DESCRIPTION = "Persisted products";

	@NotNull
	@Column(nullable = false)
	private String name;

	@NotNull
	@Column(nullable = false)
	private String description;

	@NotNull
	@Column(nullable = false)
	private BigDecimal price;

}
