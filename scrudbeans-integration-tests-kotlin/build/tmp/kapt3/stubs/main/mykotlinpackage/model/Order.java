package mykotlinpackage.model;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\t\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B%\u0012\n\b\u0002\u0010\u0002\u001a\u0004\u0018\u00010\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\n\b\u0002\u0010\u0006\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\u0002\u0010\u0007J\u0013\u0010\u000e\u001a\u00020\u000f2\b\u0010\u0010\u001a\u0004\u0018\u00010\u0011H\u0096\u0002J\b\u0010\u0012\u001a\u00020\u0013H\u0016J\b\u0010\u0014\u001a\u00020\u0005H\u0016R \u0010\u0006\u001a\u0004\u0018\u00010\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\b\u0010\t\"\u0004\b\n\u0010\u000bR\u001e\u0010\u0004\u001a\u00020\u00058\u0006@\u0006X\u0087\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\f\u0010\t\"\u0004\b\r\u0010\u000b\u00a8\u0006\u0015"}, d2 = {"Lmykotlinpackage/model/Order;", "Lcom/github/manosbatsis/scrudbeans/model/AbstractAuditableEntity;", "id", "Ljava/util/UUID;", "email", "", "comment", "(Ljava/util/UUID;Ljava/lang/String;Ljava/lang/String;)V", "getComment", "()Ljava/lang/String;", "setComment", "(Ljava/lang/String;)V", "getEmail", "setEmail", "equals", "", "obj", "", "hashCode", "", "toString", "scrudbeans-integration-tests-kotlin"})
@io.swagger.v3.oas.annotations.media.Schema(name = "Order", description = "A model representing an order of product items")
@com.github.manosbatsis.scrudbeans.api.annotation.model.ScrudBean(serviceSuperInterface = mykotlinpackage.service.CustomBaseService.class, serviceImplSuperClass = mykotlinpackage.service.CustomBaseServiceImpl.class)
@javax.persistence.EntityListeners(value = {org.springframework.data.jpa.domain.support.AuditingEntityListener.class})
@javax.persistence.Table(name = "product_orders")
@javax.persistence.Entity()
public final class Order extends com.github.manosbatsis.scrudbeans.model.AbstractAuditableEntity {
    @org.jetbrains.annotations.NotNull()
    @io.swagger.v3.oas.annotations.media.Schema(title = "The client\'s email", required = true)
    @javax.persistence.Column(nullable = false)
    @javax.validation.constraints.NotNull()
    private java.lang.String email;
    @org.jetbrains.annotations.Nullable()
    @io.swagger.v3.oas.annotations.media.Schema(title = "Order comment", required = false)
    @javax.persistence.Column(length = 512)
    private java.lang.String comment;
    
    public Order(@org.jetbrains.annotations.Nullable()
    java.util.UUID id, @org.jetbrains.annotations.NotNull()
    java.lang.String email, @org.jetbrains.annotations.Nullable()
    java.lang.String comment) {
        super(null, null, null);
    }
    
    @org.jetbrains.annotations.NotNull()
    public final java.lang.String getEmail() {
        return null;
    }
    
    public final void setEmail(@org.jetbrains.annotations.NotNull()
    java.lang.String p0) {
    }
    
    @org.jetbrains.annotations.Nullable()
    public final java.lang.String getComment() {
        return null;
    }
    
    public final void setComment(@org.jetbrains.annotations.Nullable()
    java.lang.String p0) {
    }
    
    @java.lang.Override()
    public boolean equals(@org.jetbrains.annotations.Nullable()
    java.lang.Object obj) {
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