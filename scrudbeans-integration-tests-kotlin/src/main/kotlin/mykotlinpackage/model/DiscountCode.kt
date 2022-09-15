package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.model.BaseEntity
import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.*
import javax.persistence.GenerationType.IDENTITY
import javax.validation.constraints.NotNull

/**
 * Sample entity model to test validation of non-null or unique @field:Column constraints
 */
@Entity
@Table(name = "discount_code")
@ScrudBean
@Schema(name = "DiscountCode", description = "A model representing an discount code")
data class DiscountCode(
    @field:Id
    @field:GeneratedValue(strategy = IDENTITY)
    override var id: Long = 0,

    @field:NotNull
    @field:Column(nullable = false, unique = true, updatable = false)
    @field:Schema(title = "The discount code", required = true)
    var code: String? = null,

    @field:NotNull
    @field:Column(nullable = false)
    @field:Schema(title = "The discount percentage", required = true)
    var percentage: Int? = null
) : BaseEntity<Long>
