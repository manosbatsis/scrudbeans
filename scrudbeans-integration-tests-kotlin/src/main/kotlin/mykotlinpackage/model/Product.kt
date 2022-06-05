package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.model.AbstractAuditableEntity
import io.swagger.v3.oas.annotations.media.Schema
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
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
    @field:Column(nullable = false, length = 512)
    @field:Schema(title = "The product short description (max 512 chars)", required = true)
    var description: String? = null,

    @field:NotNull
    @field:Column(nullable = false)
    @field:Schema(type = "float", description = "The product price", required = true, example = "3.05")
    var price: BigDecimal? = null
) : AbstractAuditableEntity(id) {

    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj === this) return true
        if (!Product::class.isInstance(obj)) return false
        obj as Product
        return EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(name, obj.name)
            .append(description, obj.description)
            .append(price, obj.price)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(15, 35)
            .appendSuper(super.hashCode())
            .append(name)
            .append(description)
            .append(price)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("name", name)
            .append("description", description)
            .append("price", price)
            .build()
    }
}