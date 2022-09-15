package mykotlinpackage.model;

import java.lang.System;

/**
 * Embeddable many2many identifier for [Product] relationships
 */
@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0005J\u0013\u0010\t\u001a\u00020\n2\b\u0010\u000b\u001a\u0004\u0018\u00010\fH\u0096\u0002J\b\u0010\r\u001a\u00020\u000eH\u0016R\u0016\u0010\u0002\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0006\u0010\u0007R\u0016\u0010\u0004\u001a\u00020\u00038\u0006X\u0087\u0004\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\u0007\u00a8\u0006\u000f"}, d2 = {"Lmykotlinpackage/model/ProductRelationshipIdentifier;", "Ljava/io/Serializable;", "left", "Lmykotlinpackage/model/Product;", "right", "(Lmykotlinpackage/model/Product;Lmykotlinpackage/model/Product;)V", "getLeft", "()Lmykotlinpackage/model/Product;", "getRight", "equals", "", "other", "", "hashCode", "", "scrudbeans-integration-tests-kotlin"})
@io.swagger.v3.oas.annotations.media.Schema(name = "ProductRelationshipIdentifier", description = "A composite identifier used an ID in ProductRelationship entities")
@javax.persistence.Embeddable()
public final class ProductRelationshipIdentifier implements java.io.Serializable {
    @org.jetbrains.annotations.NotNull()
    @javax.persistence.ManyToOne(optional = false)
    @javax.persistence.JoinColumn(name = "left_id", nullable = false, updatable = false)
    @io.swagger.v3.oas.annotations.media.Schema(title = "The left part type", required = true)
    private final mykotlinpackage.model.Product left = null;
    @org.jetbrains.annotations.NotNull()
    @javax.persistence.ManyToOne(optional = false)
    @javax.persistence.JoinColumn(name = "right_id", nullable = false, updatable = false)
    @io.swagger.v3.oas.annotations.media.Schema(title = "The right part type", required = true)
    private final mykotlinpackage.model.Product right = null;
    
    public ProductRelationshipIdentifier(@org.jetbrains.annotations.NotNull()
    mykotlinpackage.model.Product left, @org.jetbrains.annotations.NotNull()
    mykotlinpackage.model.Product right) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    public final mykotlinpackage.model.Product getLeft() {
        return null;
    }
    
    @org.jetbrains.annotations.NotNull()
    public final mykotlinpackage.model.Product getRight() {
        return null;
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
}