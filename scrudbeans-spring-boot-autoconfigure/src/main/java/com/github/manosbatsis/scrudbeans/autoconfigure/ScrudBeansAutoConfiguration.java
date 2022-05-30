package com.github.manosbatsis.scrudbeans.autoconfigure;
/*
import com.github.manosbatsis.scrudbeans.binding.CustomEnumConverterFactory;
import com.github.manosbatsis.scrudbeans.binding.StringToEmbeddableCompositeIdConverterFactory;
import com.github.manosbatsis.scrudbeans.fs.FilePersistenceConfigPostProcessor;
import com.github.manosbatsis.scrudbeans.model.AbstractEmbeddableManyToManyIdentifier;
import com.github.manosbatsis.scrudbeans.registry.JpaModelInfoRegistry;
import com.github.manosbatsis.scrudbeans.validation.UniqueValidator;

 */

import com.github.manosbatsis.scrudbeans.api.mdd.model.IdentifierAdapter;
import com.github.manosbatsis.scrudbeans.binding.OffsetDateTimeDeserializer;
import com.github.manosbatsis.scrudbeans.binding.OffsetDateTimeSerializer;
import com.github.manosbatsis.scrudbeans.binding.StringToEmbeddableCompositeIdConverterFactory;
import com.github.manosbatsis.scrudbeans.binding.StringToPersistedEntityGenericConverter;
import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistryInitializer;
import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.github.manosbatsis.scrudbeans"})
public class ScrudBeansAutoConfiguration implements WebMvcConfigurer {

	/**
	 * Initialized by the `app.format.datetime.with-colon-in-time-zone`
	 * application property, default is `false`. Examples for zoned date-time:
	 *
	 * - When true: `1970-01-01T02:30:00.000+0000`
	 * - When true: `1970-01-01T02:30:00.000+00:00`
	 */
	@Value("${app.format.datetime.skip-colon-in-time-zone:false}")
	public String skipColonInTimeZone;
	private WebApplicationContext wac;

	@Autowired
	public void setWac(WebApplicationContext wac) {
		this.wac = wac;
	}

	/**
	 * Register a converter factory for
	 * a) identifiers extending {@link com.github.manosbatsis.scrudbeans.api.mdd.model.EmbeddableCompositeIdentifier} and
	 * b) Enums
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEmbeddableCompositeIdConverterFactory());
	}
	@Bean
	@ConditionalOnProperty(name = "scrudbeans.jackson.format-offset-date-time", havingValue = "true", matchIfMissing = true)
	public OffsetDateTimeSerializer offsetDateTimeSerializer() {
		return new OffsetDateTimeSerializer();
	}
	@Bean
	@ConditionalOnProperty(name = "scrudbeans.jackson.format-offset-date-time", havingValue = "true", matchIfMissing = true)
	public OffsetDateTimeDeserializer offsetDateTimeDeserializer() {
		return new OffsetDateTimeDeserializer();
	}

	/** Improve exception handling */
	@Bean
	@ConditionalOnMissingBean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}

	@Bean
	public StringToPersistedEntityGenericConverter stringToPersistedEntityGenericConverter(
			@Autowired List<? extends JpaPersistableModelService<?, ?>> entityServices
	){
		return new StringToPersistedEntityGenericConverter(entityServices);
	}
	@Bean
	@ConditionalOnMissingBean
	public IdentifierAdapterRegistryInitializer identifierAdapterRegistryInitializer(
			List<? extends IdentifierAdapter<?, ?>> identifierAdapters
	) {
		return new IdentifierAdapterRegistryInitializer(identifierAdapters);
	}

	/** Register a bean to collect model metadata
	@Bean
	@ConditionalOnMissingBean
	public JpaModelInfoRegistry jpaModelInfoRegistry() {
		return new JpaModelInfoRegistry();
	}
	 */
	/** Add a validator is none is already created */
	@Bean
	@ConditionalOnMissingBean
	public javax.validation.Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	/** Register custom unique constraints validator
	@Bean
	@ConditionalOnMissingBean
	public UniqueValidator uniqueValidator() {
		return new UniqueValidator();
	}
	 */
	/*
	//TODO
	@Bean
	@ConditionalOnMissingBean
	static FilePersistenceConfigPostProcessor filePersistenceConfigPostProcessor() {
		return new FilePersistenceConfigPostProcessor();
	}
	 */
}
