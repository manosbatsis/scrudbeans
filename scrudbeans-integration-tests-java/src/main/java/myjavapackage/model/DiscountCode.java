package myjavapackage.model;

import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Sample entity model to test validation of non-null or unique @Column constraints
 */
@Entity
@Table(name = "discount_code")
@ScrudBean
@Schema(name = "DiscountCode", description = "A model representing an discount code")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiscountCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(nullable = false, unique = true)
    @Schema(description = "The discount code", required = true)
    private String code;

    @NotNull
    @Column(nullable = false)
    @Schema(description = "The discount percentage", required = true)
    private Integer percentage;
}
