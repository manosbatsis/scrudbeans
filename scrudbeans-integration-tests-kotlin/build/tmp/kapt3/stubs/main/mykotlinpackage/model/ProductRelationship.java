package mykotlinpackage.model;

import java.lang.System;

/**
 * Sample composite ID entity
 */
@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u000b\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0019\u0012\u0006\u0010\u0003\u001a\u00020\u0002\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u000f\u001a\u00020\u0005H\u0016R \u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u0007\u0010\b\"\u0004\b\t\u0010\nR\u001e\u0010\u0003\u001a\u00020\u00028\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000e\u00a8\u0006\u0010"}, d2 = {"Lmykotlinpackage/model/ProductRelationship;", "Lcom/github/manosbatsis/scrudbeans/model/BaseEntity;", "Lmykotlinpackage/model/ProductRelationshipIdentifier;", "id", "description", "", "(Lmykotlinpackage/model/ProductRelationshipIdentifier;Ljava/lang/String;)V", "getDescription", "()Ljava/lang/String;", "setDescription", "(Ljava/lang/String;)V", "getId", "()Lmykotlinpackage/model/ProductRelationshipIdentifier;", "setId", "(Lmykotlinpackage/model/ProductRelationshipIdentifier;)V", "toString", "scrudbeans-integration-tests-kotlin"})
@io.swagger.v3.oas.annotations.media.Schema(name = "Product Relationships", description = "A model representing a relationship between products")
@com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean()
@javax.persistence.Table(name = "product_relationships")
@javax.persistence.Entity()
public final class ProductRelationship implements com.github.manosbatsis.scrudbeans.model.BaseEntity<mykotlinpackage.model.ProductRelationshipIdentifier> {
    @org.jetbrains.annotations.NotNull()
    @javax.persistence.EmbeddedId()
    @io.swagger.v3.oas.annotations.media.Schema(required = true)
    @javax.validation.constraints.NotNull()
    private mykotlinpackage.model.ProductRelationshipIdentifier id;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(title = "The relationship short description (max 512 chars)", required = true)
    @javax.persistence.Column(nullable = false, length = 512)
    @javax.validation.constraints.NotNull()
    private java.lang.String description;
    
    public ProductRelationship(@org.jetbrains.annotations.NotNull()
    mykotlinpackage.model.ProductRelationshipIdentifier id, @org.jetbrains.annotations.Nullable()
    java.lang.String description) {
        super();
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public mykotlinpackage.model.ProductRelationshipIdentifier getId() {
        return null;
    }
    
    @java.lang.Override()
    public void setId(@org.jetbrains.annotations.NotNull()
    mykotlinpackage.model.ProductRelationshipIdentifier p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getDescription() {
        return null;
    }
    
    public final void setDescription(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
}