/**
 *
 * ScrudBeans: Model driven development for Spring Boot
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.manosbatsis.scrudbeans.autoconfigure;


import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;

/**
 * Autoconfigures Springfox and Swagger UI based on the following application properties:
 *
 * <pre>
 *
 * </pre>
 *
 */
@EnableSwagger2
public class SwaggerAutoConfiguration {

	@Value("${spring.application.name}")
	private String applicationName;

	@Value("${scrudbeans.appVersion}")
	private String applicationVersion;

	@Value("${scrudbeans.contact.name}")
	private String contactName;

	@Value("${scrudbeans.contact.url}")
	private String contactUrl;

	@Value("${scrudbeans.contact.email}")
	private String contactEmail;


	@Value("${scrudbeans.license.name}")
	private String licenseName;

	@Value("${scrudbeans.license.url}")
	private String licenseUrl;

	@Bean
	public Docket customImplementation() {
		return new Docket(DocumentationType.SWAGGER_2)
				.select()
				.apis(RequestHandlerSelectors.any())
				.build()
				.apiInfo(apiInfo())
				.pathMapping("/");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
				.title(applicationName)
				.description("Automatically-generated documentation based on [Swagger](http://swagger.io/) and created by [Springfox](http://springfox.github.io/springfox/).")
				.version(applicationVersion)
				.contact(new Contact(contactName, contactUrl, contactEmail))
				.license(licenseName)
				.licenseUrl(licenseUrl)
				.termsOfServiceUrl("urn:tos")
				.build();
	}

}
