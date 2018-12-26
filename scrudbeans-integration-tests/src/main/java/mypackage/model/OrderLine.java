package mypackage.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;

@Entity
@Table(name = "order_lines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScrudBean(
		//pathFragment = OrderLine.API_PATH_FRAGMENT,
		//apiName = "Order lines",
		//apiDescription = OrderLine.API_MODEL_DESCRIPTION
)
@ApiModel(value = "Order Line", description = "A model representing an order line")
public class OrderLine extends AbstractSystemUuidPersistableModel {

	public static final String API_PATH_FRAGMENT = "orderLines";

	public static final String API_MODEL_DESCRIPTION = "Search, create or modify order lines";

	@NotNull
	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(value = "The product name", readOnly = true)
	private String name;

	@NotNull
	@Column(nullable = false, length = 512)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(value = "The product short description (max 512 chars)", readOnly = true)
	private String description;

	@NotNull
	@Column(nullable = false)
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(value = "The product price", readOnly = true)
	private BigDecimal price;

	@NotNull
	@Column(nullable = false)
	@ApiModelProperty(dataType = "int", value = "The desired quantity, default is 1", example = "2")
	private Integer quantity = 1;

	@Formula("price * quantity")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@ApiModelProperty(dataType = "float", value = "The subtotal cost for this order line", readOnly = true, example = "3.05")
	private BigDecimal lineTotal;

	@NotNull
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
	@ApiModelProperty(value = "The product for this order line", required = true)
	private Product product;

	@NotNull
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
	@ApiModelProperty(value = "The parent order", required = true)
	private Order order;

}
