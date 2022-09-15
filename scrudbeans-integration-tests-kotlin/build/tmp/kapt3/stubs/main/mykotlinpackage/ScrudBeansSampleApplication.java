package mykotlinpackage;

import java.lang.System;

@kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000(\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0017\u0018\u0000 \u000e2\u00020\u0001:\u0001\u000eB\u0005\u00a2\u0006\u0002\u0010\u0002J%\u0010\u0003\u001a\u00020\u00042\u0006\u0010\u0005\u001a\u00020\u00062\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\u0006H\u0017\u00a2\u0006\u0002\u0010\nJ\u000e\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\r0\fH\u0017\u00a8\u0006\u000f"}, d2 = {"Lmykotlinpackage/ScrudBeansSampleApplication;", "", "()V", "demo", "Lorg/springframework/boot/CommandLineRunner;", "orderService", "error/NonExistentClass", "orderLineService", "Lmykotlinpackage/service/OrderLineService;", "productService", "(Lerror/NonExistentClass;Lmykotlinpackage/service/OrderLineService;Lerror/NonExistentClass;)Lorg/springframework/boot/CommandLineRunner;", "localDateFormatter", "Lorg/springframework/format/Formatter;", "Ljava/time/LocalDateTime;", "Companion", "scrudbeans-integration-tests-kotlin"})
@org.springframework.data.jpa.repository.config.EnableJpaRepositories(basePackages = {"mykotlinpackage"})
@org.springframework.boot.autoconfigure.domain.EntityScan(value = {"mykotlinpackage"})
@org.springframework.data.jpa.repository.config.EnableJpaAuditing()
@org.springframework.transaction.annotation.EnableTransactionManagement()
@org.springframework.boot.autoconfigure.SpringBootApplication(exclude = {org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class, org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration.class})
public class ScrudBeansSampleApplication {
    @org.jetbrains.annotations.NotNull()
    public static final mykotlinpackage.ScrudBeansSampleApplication.Companion Companion = null;
    @org.jetbrains.annotations.NotNull()
    public static final java.lang.String PACKAGE_NAME = "mykotlinpackage";
    
    public ScrudBeansSampleApplication() {
        super();
    }
    
    /**
     * Register a formatter for parsing LocalDateTime
     * @return
     */
    @org.jetbrains.annotations.NotNull()
    @org.springframework.context.annotation.Bean()
    public org.springframework.format.Formatter<java.time.LocalDateTime> localDateFormatter() {
        return null;
    }
    
    /**
     * Create sample products for demo purposes
     */
    @org.jetbrains.annotations.NotNull()
    @org.springframework.context.annotation.Bean()
    public org.springframework.boot.CommandLineRunner demo(@org.jetbrains.annotations.NotNull()
    error.NonExistentClass orderService, @org.jetbrains.annotations.NotNull()
    mykotlinpackage.service.OrderLineService orderLineService, @org.jetbrains.annotations.NotNull()
    error.NonExistentClass productService) {
        return null;
    }
    
    @kotlin.Metadata(mv = {1, 6, 0}, k = 1, d1 = {"\u0000\u0012\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\b\u0086\u0003\u0018\u00002\u00020\u0001B\u0007\b\u0002\u00a2\u0006\u0002\u0010\u0002R\u000e\u0010\u0003\u001a\u00020\u0004X\u0086T\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0005"}, d2 = {"Lmykotlinpackage/ScrudBeansSampleApplication$Companion;", "", "()V", "PACKAGE_NAME", "", "scrudbeans-integration-tests-kotlin"})
    public static final class Companion {
        
        private Companion() {
            super();
        }
    }
}