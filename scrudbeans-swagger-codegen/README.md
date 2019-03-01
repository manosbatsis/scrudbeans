# scrudbeans-swagger-codegen

Removes springfox-generated `operationId` (or `nickname`) suffixes, 
before delegating to `TypeScriptAngularClientCodegen`, i.e. "typescript-angular".

## Command-line Example

	java -cp scrudbeans-swagger-codegen.jar:swagger-codegen-cli.jar \
	com.github.manosbatsis.scrudbeans.swaggercodegen.ScrudbeansTypescriptAngularGenerator

> Windows users  will need to use ; instead of : to separate the classpath jars


## Maven Example

```xml
<plugin>
	<groupId>io.swagger</groupId>
	<artifactId>swagger-codegen-maven-plugin</artifactId>
	<version>2.4.0</version>
	<executions>
		<execution>
			<goals>
				<goal>generate</goal>
			</goals>
			<phase>post-integration-test</phase>
			<configuration>
				<inputSpec>${project.basedir}/target/swagger2json/swagger.json</inputSpec>
				<output>${project.basedir}/target/swagger2angular</output>
				<!--language>typescript-angular</language-->
				<language>com.github.manosbatsis.scrudbeans.swaggercodegen.ScrudbeansTypescriptAngularGenerator</language>
				<providedInRoot>true</providedInRoot>
				<configOptions>
					<npmName>@swisscom-ui/api-client</npmName>
					<npmVersion>0.0.1</npmVersion>
					<!--snapshot>true</snapshot-->
					<ngVersion>7.2.4</ngVersion>
				</configOptions>
			</configuration>
		</execution>
	</executions>
	<dependencies>
		<dependency>
			<groupId>com.github.manosbatsis.scrudbeans</groupId>
			<artifactId>scrudbeans-swagger-codegen</artifactId>
			<version>${scrudbeans.version}</version>
		</dependency>
	</dependencies>
</plugin>
```
