package mypackage.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudResource;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.javers.core.metamodel.annotation.DiffIgnore;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "product_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ScrudResource(
		pathFragment = Order.API_PATH_FRAGMENT,
		apiName = "Orders",
		apiDescription = Order.API_MODEL_DESCRIPTION)
@ApiModel(value = "Order", description = "A model representing an order of product items")
public class Order extends AbstractSystemUuidPersistableModel {

	public static final String API_PATH_FRAGMENT = "orders";

	public static final String API_MODEL_DESCRIPTION = "Search, create or modify orders";

	@NotNull
	@Column(nullable = false)
	@ApiModelProperty(value = "The client's email", required = true)
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

	@Formula(" (select sum(ol.quantity * ol.price) from order_lines ol where ol.order_id = id) ")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(dataType = "float", value = "Total order cost", readOnly = true, example = "45.99")
	private BigDecimal total;

}
