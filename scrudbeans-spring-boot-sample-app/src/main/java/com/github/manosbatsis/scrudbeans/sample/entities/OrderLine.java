package com.github.manosbatsis.scrudbeans.sample.entities;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.restdude.mdd.annotation.model.ModelResource;
import com.restdude.mdd.model.AbstractSystemUuidPersistableModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "order_lines")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ModelResource(
		pathFragment = OrderLine.API_PATH_FRAGMENT,
		apiName = "Order lines",
		apiDescription = OrderLine.API_MODEL_DESCRIPTION)
public class OrderLine extends AbstractSystemUuidPersistableModel {

	public static final String API_PATH_FRAGMENT = "orderLines";

	public static final String API_MODEL_DESCRIPTION = "Persisted order lines";

	@NotNull
	@Column(nullable = false)
	private String name;

	@NotNull
	@Column(nullable = false)
	private String description;

	@NotNull
	@Column(nullable = false)
	private BigDecimal price;

	@NotNull
	@Column(nullable = false)
	private Integer quantity;

	@Formula("price * quantity")
	private BigDecimal lineTotal;

	@NotNull
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
	private Order order;

}
