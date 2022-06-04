package mykotlinpackage.model

import io.swagger.v3.oas.annotations.media.Schema
import java.io.Serializable
import jakarta.persistence.Embeddable
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

/** Embeddable many2many identifier for [Product] relationships */
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
) : Serializable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductRelationshipIdentifier

        if (left != other.left) return false
        if (right != other.right) return false

        return true
    }

    override fun hashCode(): Int {
        var result = left.hashCode()
        result = 31 * result + right.hashCode()
        return result
    }
}