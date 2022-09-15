package mykotlinpackage.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0013\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0007\u0018\u00002\u00020\u0001BA\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u0012\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t\u0012\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\u000b\u00a2\u0006\u0002\u0010\fJ\u0013\u0010\u001e\u001a\u00020\u001f2\b\u0010 \u001a\u0004\u0018\u00010!H\u0096\u0002J\b\u0010\"\u001a\u00020\u0005H\u0016J\b\u0010#\u001a\u00020$H\u0016R \u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R \u0010\n\u001a\u0004\u0018\u00010\u000b8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014R \u0010\b\u001a\u0004\u0018\u00010\t8\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0015\u0010\u0016\"\u0004\b\u0017\u0010\u0018R\"\u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010\u001d\u001a\u0004\b\u0019\u0010\u001a\"\u0004\b\u001b\u0010\u001c\u00a8\u0006%"}, d2 = {"Lmykotlinpackage/model/OrderLine;", "Lcom/github/manosbatsis/scrudbeans/model/AbstractBaseEntity;", "id", "Ljava/util/UUID;", "quantity", "", "lineTotal", "Ljava/math/BigDecimal;", "product", "Lmykotlinpackage/model/Product;", "order", "Lmykotlinpackage/model/Order;", "(Ljava/util/UUID;Ljava/lang/Integer;Ljava/math/BigDecimal;Lmykotlinpackage/model/Product;Lmykotlinpackage/model/Order;)V", "getLineTotal", "()Ljava/math/BigDecimal;", "setLineTotal", "(Ljava/math/BigDecimal;)V", "getOrder", "()Lmykotlinpackage/model/Order;", "setOrder", "(Lmykotlinpackage/model/Order;)V", "getProduct", "()Lmykotlinpackage/model/Product;", "setProduct", "(Lmykotlinpackage/model/Product;)V", "getQuantity", "()Ljava/lang/Integer;", "setQuantity", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "equals", "", "other", "", "hashCode", "toString", "", "scrudbeans-integration-tests-kotlin"})
@io.swagger.v3.oas.annotations.media.Schema(name = "Order Line", description = "A model representing an order line")
@com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean()
@javax.persistence.Table(name = "order_lines")
@javax.persistence.Entity()
public final class OrderLine extends com.github.manosbatsis.scrudbeans.model.AbstractBaseEntity {
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(type = "int", description = "The desired quantity, default is 1", example = "2")
    @javax.persistence.Column(nullable = false)
    @javax.validation.constraints.NotNull()
    private java.lang.Integer quantity;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(type = "float", description = "The subtotal cost for this order line", readOnly = true, example = "3.05")
    @com.fasterxml.jackson.annotation.JsonProperty(access = com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY)
    @org.hibernate.annotations.Formula(value = " (select p.price * quantity from products p where p.id = product_id) ")
    private java.math.BigDecimal lineTotal;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(title = "The product for this order line", required = true)
    @javax.persistence.JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    @javax.persistence.ManyToOne()
    @javax.validation.constraints.NotNull()
    private mykotlinpackage.model.Product product;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(title = "The parent order", required = true)
    @javax.persistence.JoinColumn(referencedColumnName = "id", nullable = false, updatable = false)
    @javax.persistence.ManyToOne()
    @javax.validation.constraints.NotNull()
    private mykotlinpackage.model.Order order;
    
    public OrderLine() {
        super(null);
    }
    
    public OrderLine(@org.jetbrains.annotations.Nullable()
    java.util.UUID id, @org.jetbrains.annotations.Nullable()
    java.lang.Integer quantity, @org.jetbrains.annotations.Nullable()
    java.math.BigDecimal lineTotal, @org.jetbrains.annotations.Nullable()
    mykotlinpackage.model.Product product, @org.jetbrains.annotations.Nullable()
    mykotlinpackage.model.Order order) {
        super(null);
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getQuantity() {
        return null;
    }
    
    public final void setQuantity(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.math.BigDecimal getLineTotal() {
        return null;
    }
    
    public final void setLineTotal(@org.jetbrains.annotations.Nullable()
    java.math.BigDecimal p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final mykotlinpackage.model.Product getProduct() {
        return null;
    }
    
    public final void setProduct(@org.jetbrains.annotations.Nullable()
    mykotlinpackage.model.Product p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final mykotlinpackage.model.Order getOrder() {
        return null;
    }
    
    public final void setOrder(@org.jetbrains.annotations.Nullable()
    mykotlinpackage.model.Order p0) {
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
}