package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.model.AbstractEmbeddableManyToManyIdentifier
import io.swagger.v3.oas.annotations.media.Schema
import javax.persistence.Embeddable
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

/**
 * Sample composite identifier for manyToMany relationship entities.
 * @see AbstractEmbeddableManyToManyIdentifier
 */
@Embeddable
@Schema(name = "ProductRelationshipIdentifier", description = "A composite identifier used an ID in ProductRelationship entities")
class ProductRelationshipIdentifier(
    @field:Schema(title = "The left part type", required = true)
    @field:JoinColumn(name = "left_id", nullable = false, updatable = false)
    @field:ManyToOne(optional = false)
    val left: Product,

    @field:Schema(title = "The right part type", required = true)
    @field:JoinColumn(name = "right_id", nullable = false, updatable = false)
    @field:ManyToOne(optional = false)
    val right: Product
)