package mykotlinpackage.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.github.manosbatsis.scrudbeans.api.domain.Persistable
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.jpa.validation.Unique
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
@Unique
@Table(name = "order_lines")
@ScrudBean
@ApiModel(value = "Order Line", description = "A model representing an order line")
data class OrderLine(

        @field:Id
        @field:GeneratedValue(generator = "system-uuid")
        @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
        var id: String? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:ApiModelProperty(dataType = "int", value = "The desired quantity, default is 1", example = "2")
        var quantity: Int? = 1,

        // @field:Formula("price * quantity")

        @field:Formula(" (select p.price * quantity from products p where p.id = product_id) ")
        @field:JsonProperty(access = READ_ONLY)
        @field:ApiModelProperty(dataType = "float", value = "The subtotal cost for this order line", readOnly = true, example = "3.05")
        var lineTotal: BigDecimal? = null,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
        @field:ApiModelProperty(value = "The product for this order line", required = true)
        var product: Product? = null,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
        @field:ApiModelProperty(value = "The parent order", required = true)
        var order: Order? = null
) : Persistable<String> {
        override fun getScrudBeanId() = id!!
        override fun isNew(): Boolean = id == null
}