package mykotlinpackage.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.manosbatsis.scrudbeans.api.domain.AbstractBaseEntityWithUuidId
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.validation.Unique
import io.swagger.v3.oas.annotations.media.Schema
import mykotlinpackage.dto.OrderUpdateEmailDTO
import org.javers.core.metamodel.annotation.DiffIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.format.annotation.DateTimeFormat
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Unique
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
        var comment: String? = null,

        @field:CreatedDate
        @field:DiffIgnore
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @field:Schema(title = "Date created", readOnly = true)
        @field:Column(name = "date_created", nullable = false, updatable = false)
        var createdDate: LocalDateTime? = null,

        @field:LastModifiedDate
        @field:DiffIgnore
        @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        @field:JsonProperty(access = JsonProperty.Access.READ_ONLY)
        @field:Schema(title = "Date last modified", readOnly = true)
        @field:Column(name = "date_last_modified", nullable = false)
        var lastModifiedDate: LocalDateTime? = null


) : AbstractBaseEntityWithUuidId(id) {
}