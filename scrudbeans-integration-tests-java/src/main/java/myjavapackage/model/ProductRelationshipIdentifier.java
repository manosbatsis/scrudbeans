package myjavapackage.model;


import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.Serializable;

/**
 * Embeddable many2many identifier for Product relationships
 */
@Embeddable
@Schema(name = "ProductRelationshipIdentifier",
        description = "A composite identifier used an ID in ProductRelationship entities")
public class ProductRelationshipIdentifier implements Serializable {
	@Schema(title = "The left part type", required = true)
	@JoinColumn(name = "left_id", nullable = false, updatable = false)
	@ManyToOne(optional = false)
	Product left;

	@Schema(title = "The right part type", required = true)
	@JoinColumn(name = "right_id", nullable = false, updatable = false)
	@ManyToOne(optional = false)
	Product right;
}
