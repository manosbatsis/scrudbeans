package com.github.manosbatsis.scrudbeans.sample;

import com.restdude.mdd.repository.ModelRepositoryFactoryBean;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication(exclude = {ErrorMvcAutoConfiguration.class})
@EnableTransactionManagement
@EntityScan({ScrudBeansSampleApplication.PACKAGE_NAME})
@EnableJpaRepositories(
		basePackages = {ScrudBeansSampleApplication.PACKAGE_NAME},
		repositoryFactoryBeanClass = ModelRepositoryFactoryBean.class
)
@EnableJpaAuditing
@EnableScheduling
public class ScrudBeansSampleApplication {

	public static final String PACKAGE_NAME = "com.github.manosbatsis.scrudbeans.sample";

	public static void main(String[] args) {
		SpringApplication.run(ScrudBeansSampleApplication.class, args);
	}

}
