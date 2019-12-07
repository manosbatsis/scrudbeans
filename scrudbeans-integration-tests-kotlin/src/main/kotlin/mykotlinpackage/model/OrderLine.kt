package mykotlinpackage.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.github.manosbatsis.scrudbeans.api.domain.IdModel
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "order_lines")
@ScrudBean
@ApiModel(value = "Order Line", description = "A model representing an order line")
data class OrderLine (
    @field:Id
    @field:GeneratedValue(generator = "system-uuid") @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
    val id: String? = null,
    @field:Column(nullable = false)
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(value = "The product name", readOnly = true)
    public val name: @NotNull String? = null,
    @field:Column(nullable = false, length = 512)
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(value = "The product short description (max 512 chars)", readOnly = true)
    public val description: @NotNull String? = null,
    @field:Column(nullable = false)
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(value = "The product price", readOnly = true)
    public val price: @NotNull BigDecimal? = null,
    @field:Column(nullable = false)
    @field:ApiModelProperty(dataType = "int", value = "The desired quantity, default is 1", example = "2")
    public val quantity: @NotNull Int? = 1,
    @Formula("price * quantity")
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(dataType = "float", value = "The subtotal cost for this order line", readOnly = true, example = "3.05")
    public val lineTotal: BigDecimal? = null,
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    @field:ApiModelProperty(value = "The product for this order line", required = true)
    public val product: @NotNull Product? = null,
    @ManyToOne
    @JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    @field:ApiModelProperty(value = "The parent order", required = true)
    public val order: @NotNull Order? = null
): IdModel<String> {
    override fun getScrudBeanId() = id!!
}