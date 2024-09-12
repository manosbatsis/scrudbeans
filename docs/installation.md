---
title: Installation
---

This document will help you get a quick start with ScrudBeans and Spring Boot.

## ScrudBeans Starter

The `scrudbeans-spring-boot-starter` and `scrudbeans-annotation-processor-kotlin`
are all your project needs in terms of dependencies.

### Using Maven

__Starter__: To add the dependencies with Maven, begin with the Spring Boot starters:

```xml
<!-- Main starter -->
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-spring-boot-starter</artifactId>
	<version>${scrudbeans_version}</version>
</dependency>
```

__Kotlin Processor__: If you use Kotlin, add this annotation processor:


```xml
<!-- Provided/code generation Dependencies for Java -->
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-annotation-processor-kotlin</artifactId>
	<version>${scrudbeans_version}</version>
	<scope>provided</scope>
</dependency>
```

> You may have to setup apt or kapt for Kotlin,
> see [scrudbeans-template-kotlin](https://github.com/manosbatsis/scrudbeans-template-kotlin)
> for an example.

### Using Gradle


Add the starter and annotation processor as an implementation and kapt dependency respectively:

```groovy
dependencies {
	//...
	implementation("com.github.manosbatsis.scrudbeans:scrudbeans-spring-boot-starter:$scrudbeans_version")
	kapt("com.github.manosbatsis.scrudbeans:scrudbeans-annotation-processor-kotlin:$scrudbeans_version")
}
```

> You may have to setup the Gradle kapt plugin if you have not already,
> see [scrudbeans-template-kotlin](https://github.com/manosbatsis/scrudbeans-template-kotlin)
> for an example.

## Application Properties

Regardless of whether the template or a custom project is used, you'd want to have the following
in your `src/main/resources/application.properties`:

```properties
# The base packages to scan at runtime
scrudbeans.packages=mypackage

# Properties used by our Swagger UI: version, contact info, license etc.
# Update with your own info
scrudbeans.appVersion=0.1-SNAPSHOT # Your project version
scrudbeans.contact.name=Your contact name
scrudbeans.contact.url=https://your.contact.website
scrudbeans.contact.email=contact_email@there
scrudbeans.license.name=Your Licence Name
scrudbeans.license.url=https://your.license.url

# Set the project name - also used by our swagger UI
spring.application.name=Project Name

# Work around useless errors
spring.jackson.serialization.FAIL_ON_EMPTY_BEANS=false
# We do our own validation with ScrudBeans
spring.jpa.properties.jakarta.persistence.validation.mode=none

# Use an H2 database for dev/testing
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console
jdbc:h2:mem:testdb

# Logging settings
server.error.include-stacktrace=always
#debug=true
#trace=true
#logging.config= # Location of the logging configuration file. For instance, `classpath:logback.xml` for Logback.
logging.exception-conversion-word=%wEx
logging.file=target/logs/scrudbeans.log
logging.level.root=warn
logging.level.com.github.manosbatsis=warn
logging.level.mypackage=debug
# Work around swagger bug
logging.level.io.swagger.models.parameters.AbstractSerializableParameter=ERROR
```

You are now ready to use ScrudBeans within your Spring Boot app. Next: Model Mapping
