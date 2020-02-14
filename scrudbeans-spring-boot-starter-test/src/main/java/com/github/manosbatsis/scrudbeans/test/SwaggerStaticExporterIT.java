package com.github.manosbatsis.scrudbeans.test;


import io.github.swagger2markup.GroupBy;
import io.github.swagger2markup.Language;
import io.github.swagger2markup.Swagger2MarkupConfig;
import io.github.swagger2markup.Swagger2MarkupConverter;
import io.github.swagger2markup.builder.Swagger2MarkupConfigBuilder;
import io.github.swagger2markup.markup.builder.MarkupLanguage;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static io.restassured.RestAssured.given;

/**
 * Generates static swagger docs in ${getBuildDirName()}/{@value SwaggerStaticExporterIT#GENERATED_ASCIIDOCS_PATH}
 */
@Slf4j
public class SwaggerStaticExporterIT extends AbstractRestAssuredIT {

	private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerStaticExporterIT.class);

	protected static final String GENERATED_ASCIIDOCS_PATH = "swagger2asciidoc";

	protected static final String GENERATED_MARKDOWN_PATH = "swagger2md";

	protected static final String GENERATED_JSON_PATH = "swagger2json";

	/**
	 * Override to change the Swagger JSON endpoint.
	 */
	protected String getSwaggerPath() {
		return "/v2/api-docs";
	}

	/**
	 * Override to change the build dir, e.g. to "build" for Gradle.
	 */
	protected String getBuildDirName() {
		return "target";
	}

	@Test
	public void testCreateStaticDocs() throws Exception {
		try {
			String buildDir = this.getBuildDirName() + '/';
			// get swagger document
			String swaggerPath = getSwaggerPath();
			String json = given()
					.log().all()
					.spec(defaultSpec())
					.get(swaggerPath)
					.then()
					.log().all().statusCode(200).extract().asString();

			Path targetFolder = Paths.get(buildDir + SwaggerStaticExporterIT.GENERATED_JSON_PATH);
			LOGGER.debug("Creating swagger JSON file in {}", targetFolder);
			try {
				Files.createDirectories(targetFolder);
				Files.write(targetFolder.resolve("swagger.json"), json.getBytes());
			} catch (IOException e) {
				log.error("Failed writing file", e);
			}

			// create asciidoc
			targetFolder = Paths.get(buildDir + SwaggerStaticExporterIT.GENERATED_ASCIIDOCS_PATH);
			LOGGER.debug("Creating static docs at: {}", targetFolder);
			this.makeDocs(json, targetFolder, MarkupLanguage.ASCIIDOC);

			// create markdown
			targetFolder = Paths.get(buildDir + SwaggerStaticExporterIT.GENERATED_MARKDOWN_PATH);
			LOGGER.debug("Creating static docs at: {}", targetFolder);
			this.makeDocs(json, targetFolder, MarkupLanguage.MARKDOWN);

		}
		catch (Exception e) {
			LOGGER.error("Failed generating static docs", e);
			throw e;
		}
	}

	/**
	 * Create documentation from the given swagger JSON input
	 *
	 * @param json            the swagger JSON input
	 * @param outputDirectory the directory to create the docs into
	 * @param markupLanguage  the markup language to use
	 */
	protected void makeDocs(String json, Path outputDirectory, MarkupLanguage markupLanguage) {

		// config
		Swagger2MarkupConfig configMarkdown = new Swagger2MarkupConfigBuilder()
				.withMarkupLanguage(markupLanguage)
				.withOutputLanguage(Language.EN)
				.withPathsGroupedBy(GroupBy.TAGS)
				.build();

		// create docs
		Swagger2MarkupConverter.from(json)
				.withConfig(configMarkdown)
				.build()
				.toFile(outputDirectory.resolve("index" + markupLanguage.getFileNameExtensions().get(0)));
	}

}
