package mykotlinpackage.model;

import java.lang.System;

/**
 * Sample entity model to test validation of non-null or unique @field:Column constraints
 */
@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010\b\n\u0002\b\u0014\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0003\b\u0087\b\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\'\u0012\b\b\u0002\u0010\u0003\u001a\u00020\u0002\u0012\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u0016\u001a\u00020\u0002H\u00c6\u0003J\u000b\u0010\u0017\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\u0010\u0010\u0018\u001a\u0004\u0018\u00010\u0007H\u00c6\u0003\u00a2\u0006\u0002\u0010\u0012J0\u0010\u0019\u001a\u00020\u00002\b\b\u0002\u0010\u0003\u001a\u00020\u00022\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0007H\u00c6\u0001\u00a2\u0006\u0002\u0010\u001aJ\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u001eH\u00d6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00d6\u0001J\t\u0010 \u001a\u00020\u0005H\u00d6\u0001R \u0010\u0004\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\t\u0010\n\"\u0004\b\u000b\u0010\fR\u001e\u0010\u0003\u001a\u00020\u00028\u0016@\u0016X\u0097\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\r\u0010\u000e\"\u0004\b\u000f\u0010\u0010R\"\u0010\u0006\u001a\u0004\u0018\u00010\u00078\u0006@\u0006X\u0087\u000e\u00a2\u0006\u0010\n\u0002\u0010\u0015\u001a\u0004\b\u0011\u0010\u0012\"\u0004\b\u0013\u0010\u0014\u00a8\u0006!"}, d2 = {"Lmykotlinpackage/model/DiscountCode;", "Lcom/github/manosbatsis/scrudbeans/model/BaseEntity;", "", "id", "code", "", "percentage", "", "(JLjava/lang/String;Ljava/lang/Integer;)V", "getCode", "()Ljava/lang/String;", "setCode", "(Ljava/lang/String;)V", "getId", "()Ljava/lang/Long;", "setId", "(J)V", "getPercentage", "()Ljava/lang/Integer;", "setPercentage", "(Ljava/lang/Integer;)V", "Ljava/lang/Integer;", "component1", "component2", "component3", "copy", "(JLjava/lang/String;Ljava/lang/Integer;)Lmykotlinpackage/model/DiscountCode;", "equals", "", "other", "", "hashCode", "toString", "scrudbeans-integration-tests-kotlin"})
@io.swagger.v3.oas.annotations.media.Schema(name = "DiscountCode", description = "A model representing an discount code")
@com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean()
@javax.persistence.Table(name = "discount_code")
@javax.persistence.Entity()
public final class DiscountCode implements com.github.manosbatsis.scrudbeans.model.BaseEntity<java.lang.Long> {
    @javax.persistence.GeneratedValue(strategy = javax.persistence.GenerationType.IDENTITY)
    @javax.persistence.Id()
    private long id;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(title = "The discount code", required = true)
    @javax.persistence.Column(nullable = false, unique = true, updatable = false)
    @javax.validation.constraints.NotNull()
    private java.lang.String code;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(title = "The discount percentage", required = true)
    @javax.persistence.Column(nullable = false)
    @javax.validation.constraints.NotNull()
    private java.lang.Integer percentage;
    
    /**
     * Sample entity model to test validation of non-null or unique @field:Column constraints
     */
    @org.jetbrains.annotations.NotNull()
    public final mykotlinpackage.model.DiscountCode copy(long id, @org.jetbrains.annotations.Nullable()
    java.lang.String code, @org.jetbrains.annotations.Nullable()
    java.lang.Integer percentage) {
        return null;
    }
    
    /**
     * Sample entity model to test validation of non-null or unique @field:Column constraints
     */
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object other) {
        return false;
    }
    
    /**
     * Sample entity model to test validation of non-null or unique @field:Column constraints
     */
    @java.lang.Override()
    public int hashCode() {
        return 0;
    }
    
    /**
     * Sample entity model to test validation of non-null or unique @field:Column constraints
     */
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.String toString() {
        return null;
    }
    
    public DiscountCode() {
        super();
    }
    
    public DiscountCode(long id, @org.jetbrains.annotations.Nullable()
    java.lang.String code, @org.jetbrains.annotations.Nullable()
    java.lang.Integer percentage) {
        super();
    }
    
    public final long component1() {
        return 0L;
    }
    
    @org.jetbrains.annotations.NotNull()
    @java.lang.Override()
    public java.lang.Long getId() {
        return null;
    }
    
    @java.lang.Override()
    public void setId(long p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String component2() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getCode() {
        return null;
    }
    
    public final void setCode(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer component3() {
        return null;
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.Integer getPercentage() {
        return null;
    }
    
    public final void setPercentage(@org.jetbrains.annotations.Nullable()
    java.lang.Integer p0) {
    }
}