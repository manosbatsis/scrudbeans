package myjavapackage.model;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import org.springframework.data.domain.Persistable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

/**
 * Sample composite ID entity
 */
@Entity
@Table(name = "product_relationships")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScrudBean
@Schema(name = "Product Relationships", description = "A model representing a relationship between products")
public class ProductRelationship implements Persistable<ProductRelationshipIdentifier> {

    @NotNull
    @Schema(required = true)
    @EmbeddedId
    private ProductRelationshipIdentifier id;

    @NotNull
    @Column(nullable = false, length = 512)
    @Schema(description = "The relationship short description (max 512 chars)", required = true)
    private String description;

    @Formula(" true ")
    private boolean persisted;

    @Override
    public boolean isNew() {
        return persisted;
    }

}
