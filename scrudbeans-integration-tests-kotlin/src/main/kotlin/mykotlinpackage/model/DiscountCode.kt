package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.domain.Persistable
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.validation.Unique
import io.swagger.v3.oas.annotations.media.Schema

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
@Unique
@Table(name = "discount_code")
@ScrudBean
@Schema(name = "DiscountCode", description = "A model representing an discount code")
data class DiscountCode(
        @field:Id
        @field:GeneratedValue(strategy = IDENTITY)
        var id: Long? = null,

        @field:NotNull
        @field:Column(nullable = false, unique = true)
        @field:Schema(title = "The discount code", required = true)
        var code: String? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:Schema(title = "The discount percentage", required = true)
        var percentage: Int? = null
) : Persistable<Long> {
    override fun getScrudBeanId(): Long = id!!
    override fun isNew(): Boolean = id == null
}