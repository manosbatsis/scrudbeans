package mykotlinpackage.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY
import com.github.manosbatsis.scrudbeans.api.domain.IdModel
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import mykotlinpackage.dto.OrderUpdateEmailDTO
import org.hibernate.annotations.Formula
import org.hibernate.annotations.GenericGenerator
import org.javers.core.metamodel.annotation.DiffIgnore
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME
import java.math.BigDecimal
import java.time.LocalDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.EntityListeners
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Table(name = "product_orders")
@EntityListeners(AuditingEntityListener::class)
@ScrudBean(dtoTypes = [OrderUpdateEmailDTO::class], dtoTypeNames = ["mykotlinpackage.dto.OrderUpdateCommentDTO"])
@ApiModel(value = "Order", description = "A model representing an order of product items")
data class Order (

    @field:Id @field:GeneratedValue(generator = "system-uuid") @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
    val id: String? = null,

    @field:Column(nullable = false)
    @field:ApiModelProperty(value = "The client's email", required = true)
    var email: @NotNull String? = null,
    @field:Column(length = 512)
    @field:ApiModelProperty(value = "Oder comment", required = false)
    val comment: String? = null,

    @CreatedDate
    @DiffIgnore
    @DateTimeFormat(iso = DATE_TIME)
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(value = "Date created", readOnly = true)
    @field:Column(name = "date_created", updatable = false)
    val createdDate: LocalDateTime? = null,

    @LastModifiedDate
    @DiffIgnore
    @DateTimeFormat(iso = DATE_TIME)
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(value = "Date last modified", readOnly = true)
    @field:Column(name = "date_last_modified")
    val lastModifiedDate: LocalDateTime? = null,

    @Formula(" (select sum(ol.quantity * ol.price) from order_lines ol where ol.order_id = id) ")
    @JsonProperty(access = READ_ONLY)
    @field:ApiModelProperty(dataType = "float", value = "Total order cost", readOnly = true, example = "45.99")
    val total: BigDecimal? = null

): IdModel<String> {
    override fun getScrudBeanId() = id!!
}