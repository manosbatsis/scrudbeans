package myjavapackage.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "order_lines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScrudBean
@Schema(name = "Order Line", description = "A model representing an order line")
public class OrderLine {

	@Id
	@GeneratedValue(generator = "system-uuid")
	@GenericGenerator(name = "system-uuid", strategy = "uuid2")
	private String id;

	@NotNull
	@Column(nullable = false)
	@Schema(type = "int", description = "The desired quantity, default is 1", example = "2")
	private Integer quantity = 1;

	@Formula(" (select p.price * quantity from products p where p.id = product_id) ")
	@JsonProperty(access = JsonProperty.Access.READ_ONLY)
	@Schema(type = "float", description = "The subtotal cost for this order line", readOnly = true, example = "3.05")
	private BigDecimal lineTotal;

	@NotNull
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
	@Schema(description = "The product for this order line", required = true)
	private Product product;

	@NotNull
	@ManyToOne
	@JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
	@Schema(description = "The parent order", required = true)
	private Order order;

}
