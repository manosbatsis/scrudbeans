package myjavapackage.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.model.AbstractSystemUuidPersistableModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScrudBean
@Schema(name = "Product", description = "A model representing a single product")
public class Product extends AbstractSystemUuidPersistableModel {

    @NotNull
    @Column(nullable = false)
    @Schema(description = "The product name", required = true)
    private String name;

    @NotNull
    @Column(nullable = false, length = 512)
    @Schema(description = "The product short description (max 512 chars)", required = true)
    private String description;

    @NotNull
    @Column(nullable = false)
    @Schema(type = "float", description = "The product price", required = true, example = "3.05")
    private BigDecimal price;

}
