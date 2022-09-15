package mykotlinpackage.service;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0018\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\bf\u0018\u0000*\b\b\u0000\u0010\u0001*\u00020\u0002*\b\b\u0001\u0010\u0003*\u00020\u00022\u000e\u0012\u0004\u0012\u0002H\u0001\u0012\u0004\u0012\u0002H\u00030\u0004J\b\u0010\u0005\u001a\u00020\u0006H&\u00a8\u0006\u0007"}, d2 = {"Lmykotlinpackage/service/CustomBaseService;", "T", "", "S", "Lcom/github/manosbatsis/scrudbeans/service/JpaEntityProjectorService;", "getFoo", "", "scrudbeans-integration-tests-kotlin"})
public abstract interface CustomBaseService<T extends java.lang.Object, S extends java.lang.Object> extends com.github.manosbatsis.scrudbeans.service.JpaEntityProjectorService<T, S> {
    
    public abstract boolean getFoo();
}