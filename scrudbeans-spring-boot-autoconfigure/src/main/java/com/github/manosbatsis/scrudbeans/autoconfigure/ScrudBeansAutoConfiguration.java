package com.github.manosbatsis.scrudbeans.autoconfigure;

import javax.validation.Validator;

import com.github.manosbatsis.scrudbeans.jpa.domain.fs.FilePersistenceConfigPostProcessor;
import com.github.manosbatsis.scrudbeans.jpa.mdd.registry.JpaModelInfoRegistry;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.validation.beanvalidation.SpringConstraintValidatorFactory;
import org.springframework.web.context.WebApplicationContext;

@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.github.manosbatsis.scrudbeans"})
public class ScrudBeansAutoConfiguration {

	@Autowired
	private WebApplicationContext wac;


	@Bean
	@ConditionalOnMissingBean
	//@DependsOn({"entityManagerFactory","localValidatorFactoryBean"})
	static JpaModelInfoRegistry jpaModelInfoRegistry() {
		return new JpaModelInfoRegistry();
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

	@Bean
	@ConditionalOnMissingBean
	static FilePersistenceConfigPostProcessor filePersistenceConfigPostProcessor() {
		return new FilePersistenceConfigPostProcessor();
	}



}
