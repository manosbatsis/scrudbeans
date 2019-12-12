package myjavapackage.model;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.manosbatsis.scrudbeans.api.mdd.annotation.model.ScrudBean;
import com.github.manosbatsis.scrudbeans.model.AbstractSystemUuidPersistableModel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import myjavapackage.dto.OrderUpdateEmailDTO;
import org.javers.core.metamodel.annotation.DiffIgnore;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@Table(name = "product_orders")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@ScrudBean(dtoTypes = OrderUpdateEmailDTO.class, dtoTypeNames = "myjavapackage.dto.OrderUpdateCommentDTO")
@Schema(name = "Order", description = "A model representing an order of product items")
public class Order extends AbstractSystemUuidPersistableModel {

    @NotNull
    @Column(nullable = false)
    @Schema(description = "The client's email", required = true)
    private String email;

    @Column(length = 512)
    @Schema(description = "Oder comment", required = false)
    private String comment;

    @CreatedDate
    @DiffIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Date created", readOnly = true)
    @Column(name = "date_created", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    @DiffIgnore
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    @Schema(description = "Date last modified", readOnly = true)
    @Column(name = "date_last_modified", nullable = false)
    private LocalDateTime lastModifiedDate;

}
