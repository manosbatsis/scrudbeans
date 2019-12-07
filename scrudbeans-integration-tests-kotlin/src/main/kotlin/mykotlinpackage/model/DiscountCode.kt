package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.domain.IdModel
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import java.io.Serializable
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType.IDENTITY
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull


/**
 * Sample entity model to test validation of non-null or unique @field:Column constraints
 */
@Entity
@Table(name = "discount_code")
@ScrudBean
@ApiModel(value = "DiscountCode", description = "A model representing an discount code")
data class DiscountCode(
        @field:Id
        @field:GeneratedValue(strategy = IDENTITY)
        var id: Long? = null,
        @field:Column(nullable = false, unique = true)
        @field:ApiModelProperty(value = "The discount code", required = true)
        var code: String? = null,
        @field:Column(nullable = false)
        @field:ApiModelProperty(value = "The discount percentage", required = false)
        val percentage: Int = 0
) : IdModel<Long> {
        override fun getScrudBeanId(): Long = id!!
}