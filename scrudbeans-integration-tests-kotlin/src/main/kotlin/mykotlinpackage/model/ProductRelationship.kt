package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.api.domain.Persistable
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean
import com.github.manosbatsis.scrudbeans.validation.Unique
import io.swagger.v3.oas.annotations.media.Schema

import javax.persistence.Column
import javax.persistence.EmbeddedId
import javax.persistence.Entity
import javax.persistence.Table
import javax.validation.constraints.NotNull

/**
 * Sample composite ID entity
 */
@Entity
@Unique
@Table(name = "product_relationships")
@ScrudBean
@Schema(name = "Product Relationships", description = "A model representing a relationship between products")
data class ProductRelationship(

        @field:NotNull
        @field:Schema(required = true)
        @field:EmbeddedId
        var id: ProductRelationshipIdentifier? = null,

        @field:NotNull
        @field:Column(nullable = false, length = 512)
        @field:Schema(title = "The relationship short description (max 512 chars)", required = true)
        var description: String? = null
) : Persistable<ProductRelationshipIdentifier?> {
    override fun getScrudBeanId() = id!!
    override fun isNew(): Boolean = id == null
}