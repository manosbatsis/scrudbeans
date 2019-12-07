package mykotlinpackage.model

import com.github.manosbatsis.scrudbeans.jpa.model.AbstractEmbeddableManyToManyIdentifier
import io.swagger.annotations.ApiModel
import java.io.Serializable
import javax.persistence.Embeddable

/**
 * Sample composite identifier for manyToMany relationship entities.
 * @see AbstractEmbeddableManyToManyIdentifier
 */
@Embeddable
@ApiModel(value = "ProductRelationshipIdentifier", description = "A composite identifier used an ID in ProductRelationship entities")
class ProductRelationshipIdentifier : AbstractEmbeddableManyToManyIdentifier<Product, String, Product, String>() {
    override fun buildLeft(left: Serializable) = Product(id = left.toString())

    override fun buildRight(right: Serializable) = Product(id = right.toString())
}