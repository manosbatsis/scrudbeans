package com.github.manosbatsis.scrudbeans.autoconfigure;

import javax.validation.Validator;

import com.github.manosbatsis.scrudbeans.jpa.binding.StringToEmbeddableManyToManyIdConverterFactory;
import com.github.manosbatsis.scrudbeans.jpa.fs.FilePersistenceConfigPostProcessor;
import com.github.manosbatsis.scrudbeans.jpa.model.AbstractEmbeddableManyToManyIdentifier;
import com.github.manosbatsis.scrudbeans.jpa.registry.JpaModelInfoRegistry;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
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

	@Bean
	@ConditionalOnMissingBean
	static JpaModelInfoRegistry jpaModelInfoRegistry() {
		return new JpaModelInfoRegistry();
	}


	/**
	 * Register a converter factory for identifiers extending {@link AbstractEmbeddableManyToManyIdentifier}
	 * @param registry
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
		registry.addConverterFactory(new StringToEmbeddableManyToManyIdConverterFactory());
	}

	@Bean
	@ConditionalOnMissingBean
	public Validator validator() {
		SpringConstraintValidatorFactory scvf = new SpringConstraintValidatorFactory(wac.getAutowireCapableBeanFactory());
		LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
		validator.setConstraintValidatorFactory(scvf);
		validator.setApplicationContext(wac);
		validator.afterPropertiesSet();
		return validator;
	}
	//@Bean
	//public HibernateExceptionTranslator hibernateExceptionTranslator() {
	//    return new HibernateExceptionTranslator();
	// }


	//@Bean
	//@ConditionalOnMissingBean
	//public javax.validation.Validator validator() {
//		return new LocalValidatorFactoryBean();
//	}

	//TODO
	@Bean
	@ConditionalOnMissingBean
	static FilePersistenceConfigPostProcessor filePersistenceConfigPostProcessor() {
		return new FilePersistenceConfigPostProcessor();
	}



}
