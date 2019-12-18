package com.github.manosbatsis.scrudbeans.autoconfigure;

import com.github.manosbatsis.scrudbeans.error.BasicErrorController;
import com.github.manosbatsis.scrudbeans.error.ScrudbeansErrorAttributes;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;


@Configuration
@ComponentScan(basePackages = "com.github.manosbatsis.scrudbeans.error")
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@ConditionalOnProperty(name = "spring.main.allow-bean-definition-overriding", havingValue = "true")
@AutoConfigureBefore({WebMvcAutoConfiguration.class, ErrorMvcAutoConfiguration.class})
public class ScrudbeansErrorAutoConfiguration implements PriorityOrdered {

    @Bean
    @Primary
    public ScrudbeansErrorAttributes errorAttributes() {
        return new ScrudbeansErrorAttributes();
    }

    @Bean
    public BasicErrorController basicErrorController() {
        return new BasicErrorController();
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
