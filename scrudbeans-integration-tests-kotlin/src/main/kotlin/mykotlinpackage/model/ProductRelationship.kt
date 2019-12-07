package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.domain.IdModel
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull

/**
 * Sample composite ID entity
 */
@Entity
@Table(name = "product_relationships")
@ScrudBean
@ApiModel(value = "Product Relationships", description = "A model representing a relationship between products")
data class ProductRelationship(
    @field:ApiModelProperty(required = true)
    @field:EmbeddedId
    var id: @NotNull ProductRelationshipIdentifier? = null,
    @field:Column(nullable = false, length = 512)
    @field:ApiModelProperty(value = "The relationship short description (max 512 chars)", required = true)
    var description: @NotNull String? = null
) : IdModel<ProductRelationshipIdentifier?> {
    override fun getScrudBeanId() = id!!
}