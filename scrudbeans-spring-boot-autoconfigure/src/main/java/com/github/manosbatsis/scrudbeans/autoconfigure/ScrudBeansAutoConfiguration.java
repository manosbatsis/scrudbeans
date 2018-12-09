package com.github.manosbatsis.scrudbeans.autoconfigure;

import com.restdude.mdd.registry.JpaModelInfoRegistry;
import lombok.extern.slf4j.Slf4j;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = {"com.github.manosbatsis.scrudbeans", "com.restdude"})
public class ScrudBeansAutoConfiguration {

	@Bean
	static JpaModelInfoRegistry jpaModelInfoRegistry() {
		return new JpaModelInfoRegistry();
	}

}
