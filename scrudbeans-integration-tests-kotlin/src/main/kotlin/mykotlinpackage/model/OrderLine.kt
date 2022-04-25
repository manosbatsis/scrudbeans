package mykotlinpackage.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.model.AbstractBaseEntity
import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.annotations.Formula
import java.math.BigDecimal
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
@Table(name = "order_lines")
@ScrudBean
@Schema(name = "Order Line", description = "A model representing an order line")
class OrderLine(

        id: UUID? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:Schema(type = "int", description = "The desired quantity, default is 1", example = "2")
        var quantity: Int? = 1,

        // @field:Formula("price * quantity")

        @field:Formula(" (select p.price * quantity from products p where p.id = product_id) ")
        @field:JsonProperty(access = READ_ONLY)
        @field:Schema(type = "float", description = "The subtotal cost for this order line", readOnly = true, example = "3.05")
        var lineTotal: BigDecimal? = null,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
        @field:Schema(title = "The product for this order line", required = true)
        var product: Product? = null,

        @field:NotNull
        @field:ManyToOne
        @field:JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
        @field:Schema(title = "The parent order", required = true)
        var order: Order? = null
) : AbstractBaseEntity(id) {
}