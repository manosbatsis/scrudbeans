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
package com.github.manosbatsis.scrudbeans.test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.config.ObjectMapperConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;

import org.springframework.boot.web.server.LocalServerPort;

//import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
//import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
//import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

/**
 * Base class for rest-assured based controller integration testing
 */
@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class AbstractRestAssuredIT {

	public static final String MIME_APPLICATION_JSON_UTF8 = "application/json; charset=UTF-8";

	public static final String MIME_APPLICATION_VND_API_JSON_UTF8 = "application/vnd.api+json; charset=UTF-8";

	private static final RequestSpecification SPEC_JSON = new RequestSpecBuilder()
			.setContentType(MIME_APPLICATION_JSON_UTF8)
			.setAccept(MIME_APPLICATION_JSON_UTF8)
			.build();
	private static final RequestSpecification SPEC_JSON_API = new RequestSpecBuilder()
			.setContentType(MIME_APPLICATION_VND_API_JSON_UTF8)
			.setAccept(MIME_APPLICATION_VND_API_JSON_UTF8)
			.build();

	@LocalServerPort
	public int port;

	protected RequestSpecification defaultSpec() {
		// Update to use our port
		RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder().addRequestSpecification(SPEC_JSON).setPort(port);
		// add logging filters if debug is enabled
		if (log.isDebugEnabled()) {
			requestSpecBuilder
					.addFilter(new RequestLoggingFilter())
					.addFilter(new ResponseLoggingFilter());
		}
		return requestSpecBuilder.build();
	}

	protected RequestSpecification jsonApiSpec() {
		// Update to use our port
		RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder().addRequestSpecification(SPEC_JSON_API).setPort(port);
		// add logging filters if debug is enabled
		if (log.isDebugEnabled()) {
			requestSpecBuilder
					.addFilter(new RequestLoggingFilter())
					.addFilter(new ResponseLoggingFilter());
		}
		return requestSpecBuilder.build();
	}

	@BeforeAll
	public void setUp() {
		RestAssured.port = port;
		RestAssured.urlEncodingEnabled = true;
		// configure our object mapper
		RestAssured.config = RestAssuredConfig.config().objectMapperConfig(
				// config object mapper
				new ObjectMapperConfig().jackson2ObjectMapperFactory((type, s) -> {
					ObjectMapper objectMapper = new ObjectMapper()
							//.registerModule(new ParameterNamesModule())
							//.registerModule(new Jdk8Module())
							//.registerModule(new JavaTimeModule())
							;

					// Disable features
					objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
					objectMapper.configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false);
					objectMapper.configure(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE, false);
					objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
					objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
					// enable features
					objectMapper.configure(MapperFeature.DEFAULT_VIEW_INCLUSION, true);

					return objectMapper;
				}));
	}

}
