package myjavapackage.model;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScrudBean
@Schema(name = "Product", description = "A model representing a single product")
public class Product {

    @Id
    @GeneratedValue(generator = "system-uuid")
    @GenericGenerator(name = "system-uuid", strategy = "uuid2")
    private String id;

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
