package com.github.manosbatsis.scrudbeans.autoconfigure

import com.github.manosbatsis.scrudbeans.binding.*
import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistry
import com.github.manosbatsis.scrudbeans.service.JpaEntityService
import javax.validation.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.orm.hibernate5.HibernateExceptionTranslator
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
@ConditionalOnBean(JpaEntityService::class)
class ScrudBeansAutoConfiguration : WebMvcConfigurer {

    @Autowired
    lateinit var entityServices: Map<String, JpaEntityService<*, *>>

    /**
     * Initialized by the `app.format.datetime.with-colon-in-time-zone`
     * application property, default is `false`. Examples for zoned date-time:
     *
     * - When true: `1970-01-01T02:30:00.000+0000`
     * - When true: `1970-01-01T02:30:00.000+00:00`
     */
    @Value("\${app.format.datetime.skip-colon-in-time-zone:false}")
    lateinit var skipColonInTimeZone: String
    @Autowired
    lateinit var wac: WebApplicationContext

    @Bean
    @ConditionalOnProperty(
        name = ["scrudbeans.jackson.format-offset-date-time"],
        havingValue = "true",
        matchIfMissing = true
    )
    fun offsetDateTimeSerializer(): OffsetDateTimeSerializer {
        return OffsetDateTimeSerializer()
    }

    @Bean
    @ConditionalOnProperty(
        name = ["scrudbeans.jackson.format-offset-date-time"],
        havingValue = "true",
        matchIfMissing = true
    )
    fun offsetDateTimeDeserializer(): OffsetDateTimeDeserializer {
        return OffsetDateTimeDeserializer()
    }

    /** Improve exception handling  */
    @Bean
    @ConditionalOnMissingBean
    fun hibernateExceptionTranslator(): HibernateExceptionTranslator {
        return HibernateExceptionTranslator()
    }

    @Bean
    fun stringToPersistedEntityGenericConverter(): StringToScrudBeanGenericConverter {
        return StringToScrudBeanGenericConverter(identifierAdapterRegistry())
    }

    @Bean
    fun scrudBeanToStringGenericConverter(): ScrudBeanToStringGenericConverter {
        return ScrudBeanToStringGenericConverter(identifierAdapterRegistry())
    }

    @Bean
    fun stringToScrudBeanIdGenericConverter(): StringToScrudBeanIdGenericConverter {
        return StringToScrudBeanIdGenericConverter(identifierAdapterRegistry())
    }

    @Bean
    fun scrudBeanIdToStringGenericConverter(): ScrudBeanIdToStringGenericConverter {
        return ScrudBeanIdToStringGenericConverter(identifierAdapterRegistry())
    }

    @Bean
    fun identifierAdapterRegistry(): IdentifierAdapterRegistry {
        return IdentifierAdapterRegistry(entityServices)
    }

    /** Add a validator is none is already created  */
    @Bean
    @ConditionalOnMissingBean
    fun localValidatorFactoryBean(): Validator {
        return LocalValidatorFactoryBean()
    }

    /* TODO
	@Bean
	@ConditionalOnMissingBean
	static FilePersistenceConfigPostProcessor filePersistenceConfigPostProcessor() {
		return new FilePersistenceConfigPostProcessor();
	}
	 */
}
