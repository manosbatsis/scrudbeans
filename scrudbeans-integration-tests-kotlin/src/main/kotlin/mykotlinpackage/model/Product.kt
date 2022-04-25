package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.model.AbstractAuditableEntity
import io.swagger.v3.oas.annotations.media.Schema
import java.math.BigDecimal
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "products")
@ScrudBean
@Schema(name = "Product", description = "A model representing a single product")
class Product(
        id: UUID? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:Schema(title = "The product name", required = true)
        var name: String? = null,

        @field:NotNull
        @field:javax.persistence.Column(nullable = false, length = 512)
        @field:Schema(title = "The product short description (max 512 chars)", required = true)
        var description: String? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:Schema(type = "float", description = "The product price", required = true, example = "3.05")
        var price: BigDecimal? = null
) : AbstractAuditableEntity(id) {
}