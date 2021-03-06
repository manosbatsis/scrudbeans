package com.github.manosbatsis.scrudbeans.autoconfigure;

import com.github.manosbatsis.scrudbeans.binding.CustomEnumConverterFactory;
import com.github.manosbatsis.scrudbeans.binding.StringToEmbeddableCompositeIdConverterFactory;
import com.github.manosbatsis.scrudbeans.fs.FilePersistenceConfigPostProcessor;
import com.github.manosbatsis.scrudbeans.model.AbstractEmbeddableManyToManyIdentifier;
import com.github.manosbatsis.scrudbeans.registry.JpaModelInfoRegistry;
import com.github.manosbatsis.scrudbeans.validation.UniqueValidator;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
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

@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.github.manosbatsis.scrudbeans"})
public class ScrudBeansAutoConfiguration implements WebMvcConfigurer {

	private WebApplicationContext wac;

	@Autowired
	public void setWac(WebApplicationContext wac) {
		this.wac = wac;
	}

	/**
	 * Register a converter factory for
	 * a) identifiers extending {@link AbstractEmbeddableManyToManyIdentifier} and
	 * b) Enums
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEmbeddableCompositeIdConverterFactory());
		registry.addConverterFactory(new CustomEnumConverterFactory());
	}

	/** Improve exception handling */
	@Bean
	@ConditionalOnMissingBean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}

	/** Register a bean to collect model metadata */
	@Bean
	@ConditionalOnMissingBean
	public JpaModelInfoRegistry jpaModelInfoRegistry() {
		return new JpaModelInfoRegistry();
	}

	/** Add a validator is none is already created */
	@Bean
	@ConditionalOnMissingBean
	public javax.validation.Validator localValidatorFactoryBean() {
		return new LocalValidatorFactoryBean();
	}

	/** Register custom unique constraints validator */
	@Bean
	@ConditionalOnMissingBean
	public UniqueValidator uniqueValidator() {
		return new UniqueValidator();
	}

	//TODO
	@Bean
	@ConditionalOnMissingBean
	static FilePersistenceConfigPostProcessor filePersistenceConfigPostProcessor() {
		return new FilePersistenceConfigPostProcessor();
	}

}
