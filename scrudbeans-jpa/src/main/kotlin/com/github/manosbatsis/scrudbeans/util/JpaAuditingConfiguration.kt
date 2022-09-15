package com.github.manosbatsis.scrudbeans.util

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.auditing.DateTimeProvider
import org.springframework.data.jpa.repository.config.EnableJpaAuditing
import java.time.OffsetDateTime
import java.time.ZoneOffset.UTC
import java.util.*

@Configuration
@EnableJpaAuditing(
    // TODO auditorAwareRef = "auditorProvider",
    dateTimeProviderRef = "auditingDateTimeProvider"
)
class JpaAuditingConfiguration {
    @Bean(name = ["auditingDateTimeProvider"])
    fun dateTimeProvider(): DateTimeProvider {
        return DateTimeProvider { Optional.of(OffsetDateTime.now(UTC)) }
    }
}
