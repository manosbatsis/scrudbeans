package com.github.manosbatsis.scrudbeans.autoconfigure;
/*
import com.github.manosbatsis.scrudbeans.binding.CustomEnumConverterFactory;
import com.github.manosbatsis.scrudbeans.binding.StringToEmbeddableCompositeIdConverterFactory;
import com.github.manosbatsis.scrudbeans.fs.FilePersistenceConfigPostProcessor;
import com.github.manosbatsis.scrudbeans.model.AbstractEmbeddableManyToManyIdentifier;
import com.github.manosbatsis.scrudbeans.registry.JpaModelInfoRegistry;
import com.github.manosbatsis.scrudbeans.validation.UniqueValidator;

 */

import com.github.manosbatsis.scrudbeans.binding.*;
import com.github.manosbatsis.scrudbeans.service.IdentifierAdapterRegistry;
import com.github.manosbatsis.scrudbeans.service.JpaPersistableModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.orm.hibernate5.HibernateExceptionTranslator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Map;

@Slf4j
@Configuration
//	@ComponentScan(basePackages = {"com.github.manosbatsis.scrudbeans"})
public class ScrudBeansAutoConfiguration implements WebMvcConfigurer {

	Map<String, ? extends JpaPersistableModelService<?, ?>> entityServices;

	@Autowired
	public void setEntityServices(Map<String, ? extends JpaPersistableModelService<?, ?>> entityServices) {
		this.entityServices = entityServices;
	}


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
	public StringToScrudBeanGenericConverter stringToPersistedEntityGenericConverter(){
		return new StringToScrudBeanGenericConverter(identifierAdapterRegistry());
	}

	@Bean
	public ScrudBeanToStringGenericConverter scrudBeanToStringGenericConverter(){
		return new ScrudBeanToStringGenericConverter(identifierAdapterRegistry());
	}

	@Bean
	public StringToScrudBeanIdGenericConverter stringToScrudBeanIdGenericConverter(){
		return new StringToScrudBeanIdGenericConverter(identifierAdapterRegistry());
	}

	@Bean
	public ScrudBeanIdToStringGenericConverter scrudBeanIdToStringGenericConverter(){
		return new ScrudBeanIdToStringGenericConverter(identifierAdapterRegistry());
	}

	@Bean
	public IdentifierAdapterRegistry identifierAdapterRegistry(){
		return new IdentifierAdapterRegistry(entityServices);
	}

	/** Add a validator is none is already created */
	@Bean
	@ConditionalOnMissingBean
	public javax.validation.Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}


	/*
	//TODO
	@Bean
	@ConditionalOnMissingBean
	static FilePersistenceConfigPostProcessor filePersistenceConfigPostProcessor() {
		return new FilePersistenceConfigPostProcessor();
	}
	 */
}
