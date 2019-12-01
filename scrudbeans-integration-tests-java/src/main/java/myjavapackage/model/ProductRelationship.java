package myjavapackage.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.github.manosbatsis.scrudbeans.api.domain.SettableIdModel;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Sample composite ID entity
 */
@Entity
@Table(name = "product_relationships")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScrudBean
@ApiModel(value = "Product Relationships", description = "A model representing a relationship between products")
public class ProductRelationship implements SettableIdModel<ProductRelationshipIdentifier> {

	@NotNull
	@ApiModelProperty(required = true)
	@EmbeddedId
	private ProductRelationshipIdentifier id;

	@NotNull
	@Column(nullable = false, length = 512)
	@ApiModelProperty(value = "The relationship short description (max 512 chars)", required = true)
	private String description;

}
