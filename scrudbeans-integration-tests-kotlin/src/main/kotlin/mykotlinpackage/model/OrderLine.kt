package mykotlinpackage.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.github.manosbatsis.scrudbeans.api.domain.KPersistable
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.validation.Unique
import io.swagger.v3.oas.annotations.media.Schema

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
@Schema(name = "Order Line", description = "A model representing an order line")
data class OrderLine(

        @field:Id
        @field:GeneratedValue(generator = "system-uuid")
        @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
        override var id: String? = null,

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
) : KPersistable<String> {
        override fun isNew(): Boolean = id == null
}