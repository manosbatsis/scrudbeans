package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.model.AbstractAuditableEntity
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EntityListeners
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import mykotlinpackage.service.CustomBaseService
import mykotlinpackage.service.CustomBaseServiceImpl
import org.apache.commons.lang3.builder.EqualsBuilder
import org.apache.commons.lang3.builder.HashCodeBuilder
import org.apache.commons.lang3.builder.ToStringBuilder
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*

@Entity
@Table(name = "product_orders")
@EntityListeners(AuditingEntityListener::class)
@ScrudBean(
    serviceSuperInterface = CustomBaseService::class,
    serviceImplSuperClass = CustomBaseServiceImpl::class,
)
@Schema(name = "Order", description = "A model representing an order of product items")
class Order(
    id: UUID? = null,
    @field:NotNull
    @field:Column(nullable = false)
    @field:Schema(title = "The client's email", required = true)
    var email: String,

    @field:Column(length = 512)
    @field:Schema(title = "Order comment", required = false)
    var comment: String? = null,
) : AbstractAuditableEntity(id) {
    override fun equals(obj: Any?): Boolean {
        if (obj == null) return false
        if (obj === this) return true
        if (!Order::class.isInstance(obj)) return false
        obj as Order
        return EqualsBuilder()
            .appendSuper(super.equals(obj))
            .append(email, obj.email)
            .append(comment, obj.comment)
            .isEquals
    }

    override fun hashCode(): Int {
        return HashCodeBuilder(19, 39)
            .appendSuper(super.hashCode())
            .append(email)
            .append(comment)
            .toHashCode()
    }

    override fun toString(): String {
        return ToStringBuilder(this)
            .appendSuper(super.toString())
            .append("email", email)
            .append("comment", comment)
            .build()
    }
}
