package mykotlinpackage.service;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u00002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\b&\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u0002*\b\b\u0001\u0010\u0003*\u00020\u0002*\u0014\b\u0002\u0010\u0004*\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00030\u00052\u0014\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u0003\u0012\u0004\u0012\u0002H\u00040\u00062\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00030\u0007B)\u0012\u0006\u0010\b\u001a\u00028\u0002\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0012\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00028\u0000\u0012\u0004\u0012\u00028\u00010\f\u00a2\u0006\u0002\u0010\rJ\b\u0010\u000e\u001a\u00020\u000fH\u0016\u00a8\u0006\u0010"}, d2 = {"Lmykotlinpackage/service/CustomBaseServiceImpl;", "T", "", "S", "B", "Lcom/github/manosbatsis/scrudbeans/repository/JpaEntityProjectorRepository;", "Lcom/github/manosbatsis/scrudbeans/service/AbstractJpaEntityProjectorService;", "Lmykotlinpackage/service/CustomBaseService;", "repository", "entityManager", "Ljavax/persistence/EntityManager;", "identifierAdapter", "Lcom/github/manosbatsis/scrudbeans/api/mdd/model/IdentifierAdapter;", "(Lcom/github/manosbatsis/scrudbeans/repository/JpaEntityProjectorRepository;Ljavax/persistence/EntityManager;Lcom/github/manosbatsis/scrudbeans/api/mdd/model/IdentifierAdapter;)V", "getFoo", "", "scrudbeans-integration-tests-kotlin"})
public abstract class CustomBaseServiceImpl<T extends java.lang.Object, S extends java.lang.Object, B extends com.github.manosbatsis.scrudbeans.repository.JpaEntityProjectorRepository<T, S>> extends com.github.manosbatsis.scrudbeans.service.AbstractJpaEntityProjectorService<T, S, B> implements mykotlinpackage.service.CustomBaseService<T, S> {
    
    public CustomBaseServiceImpl(@org.jetbrains.annotations.NotNull()
    B repository, @org.jetbrains.annotations.NotNull()
    javax.persistence.EntityManager entityManager, @org.jetbrains.annotations.NotNull()
    com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter<T, S> identifierAdapter) {
        super(null, null, null);
    }
    
    @java.lang.Override()
    public boolean getFoo() {
        return false;
    }
}