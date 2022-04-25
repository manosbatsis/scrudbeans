package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.model.AbstractAuditableEntity
import io.swagger.v3.oas.annotations.media.Schema
import mykotlinpackage.dto.OrderUpdateEmailDTO
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "product_orders")
@EntityListeners(AuditingEntityListener::class)
@ScrudBean(dtoTypes = [OrderUpdateEmailDTO::class], dtoTypeNames = ["mykotlinpackage.dto.OrderUpdateCommentDTO"])
@Schema(name = "Order", description = "A model representing an order of product items")
class Order(
        id: UUID? = null,
        @field:NotNull
        @field:Column(nullable = false)
        @field:Schema(title = "The client's email", required = true)
        var email: String? = null,
        @field:Column(length = 512)
        @field:Schema(title = "Oder comment", required = false)
        var comment: String? = null
) : AbstractAuditableEntity(id) {
}