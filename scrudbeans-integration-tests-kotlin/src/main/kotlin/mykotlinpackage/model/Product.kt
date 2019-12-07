package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.domain.IdModel
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractSystemUuidPersistableModel
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
@Table(name = "products")
@ScrudBean
@ApiModel(value = "Product", description = "A model representing a single product")
data class Product(
    @field:Id
    @field:GeneratedValue(generator = "system-uuid") @field:GenericGenerator(name = "system-uuid", strategy = "uuid2")
    val id: String? = null,
    @field:Column(nullable = false)
    @field:ApiModelProperty(value = "The product name", required = true)
    public val name: @NotNull String? = null,
    @field:Column(nullable = false, length = 512)
    @field:ApiModelProperty(value = "The product short description (max 512 chars)", required = true)
    public val description: @NotNull String? = null,
    @field:Column(nullable = false)
    @field:ApiModelProperty(dataType = "float", value = "The product price", required = true, example = "3.05")
    public val price: @NotNull BigDecimal? = null
): IdModel<String> {
    override fun getScrudBeanId() = id!!
}