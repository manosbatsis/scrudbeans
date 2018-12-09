package com.github.manosbatsis.scrudbeans.sample.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.restdude.mdd.annotation.model.ModelResource;
import com.restdude.mdd.model.AbstractSystemUuidPersistableModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.javers.core.metamodel.annotation.DiffIgnore;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ModelResource(
		pathFragment = Order.API_PATH_FRAGMENT,
		apiName = "Orders",
		apiDescription = Order.API_MODEL_DESCRIPTION)

public class Order extends AbstractSystemUuidPersistableModel {

	public static final String API_PATH_FRAGMENT = "orders";

	public static final String API_MODEL_DESCRIPTION = "Persisted orders";

	@NotNull
	@Column(nullable = false)
	private String email;

	@CreatedDate
	@DiffIgnore
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(value = "Date created", readOnly = true)
	@Column(name = "date_created", nullable = false, updatable = false)
	private LocalDateTime createdDate;

	@LastModifiedDate
	@DiffIgnore
	@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(value = "Date last modified", readOnly = true)
	@Column(name = "date_last_modified", nullable = false)
	private LocalDateTime lastModifiedDate;

	@NotNull
	@Column(nullable = false)

	@Formula(" (select sum(lineTotal) from OrderLine orderLine where orderLine.order.id = id) ")
	private BigDecimal total;
}
