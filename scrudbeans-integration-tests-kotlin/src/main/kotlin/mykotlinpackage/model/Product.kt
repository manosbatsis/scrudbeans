package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.domain.Persistable
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.jpa.validation.Unique
import io.swagger.annotations.ApiModel
import io.swagger.annotations.ApiModelProperty
import org.hibernate.annotations.GenericGenerator
import java.math.BigDecimal
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id
import javax.persistence.Table
import javax.validation.constraints.NotNull

@Entity
@Unique
@Table(name = "products")
@ScrudBean
@ApiModel(value = "Product", description = "A model representing a single product")
data class Product(
        @field:Id
        @field:GeneratedValue(generator = "system-uuid")
        @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
        var id: String? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:ApiModelProperty(value = "The product name", required = true)
        var name: String? = null,

        @field:NotNull
        @field:javax.persistence.Column(nullable = false, length = 512)
        @field:ApiModelProperty(value = "The product short description (max 512 chars)", required = true)
        var description: String? = null,

        @field:NotNull
        @field:Column(nullable = false)
        @field:ApiModelProperty(dataType = "float", value = "The product price", required = true, example = "3.05")
        var price: BigDecimal? = null
) : Persistable<String> {
        override fun getScrudBeanId() = id!!
        override fun isNew(): Boolean = id == null
}