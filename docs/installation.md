---
title: Installation
---

This document will help you get a quick start with ScrudBeans and Spring Boot.

## Using the Project Templates

The [scrudbeans-template-java](https://github.com/manosbatsis/scrudbeans-template-java) and
[scrudbeans-template-kotlin](https://github.com/manosbatsis/scrudbeans-template-kotlin) projects
replicates this quick tutorial and can help you get started with ScrudBeans right away.
> Both Maven and Gradle build scripts are included in the above samples.

For Kotlin:

```bash
git clone https://github.com/manosbatsis/scrudbeans-template-kotlin.git
```

For Java:

```bash
git clone https://github.com/manosbatsis/scrudbeans-template-java.git
```

## Using a Custom Project

Alternatively, you can use an existing Spring Boot project or create a new one with
[Spring Initializr](https://start.spring.io/).

## ScrudBeans Starter

If you are not using scrudbeans-template, the ScrudBeans autoconfiguration starter for
Spring Boot is all your project needs in terms of dependencies.

### Using Maven

__Starter__: To add the dependencies with Maven, begin with the Spring Boot starters:

```xml
<!-- Main starter -->
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-spring-boot-starter</artifactId>
	<version>${scrudbeans.version}</version>
</dependency>
<!-- Test Starter -->
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-spring-boot-starter-test</artifactId>
	<version>${scrudbeans.version}</version>
	<scope>test</scope>
</dependency>
```

__Java Processor__: If you use Java, add this annotation processor:

```xml
<!-- Provided/code generation Dependencies for Java -->
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-annotation-processor-java</artifactId>
	<version>${project.version}</version>
	<scope>provided</scope>
</dependency>
```

__Kotlin Processor__: If you use Kotlin, add this annotation processor:


```xml
<!-- Provided/code generation Dependencies for Java -->
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-annotation-processor-kotlin</artifactId>
	<version>${project.version}</version>
	<scope>provided</scope>
</dependency>
```

> You may have to setup apt or kapt for Java or Kotlin respectively,
> see [scrudbeans-template-java](https://github.com/manosbatsis/scrudbeans-template-java)
> and [scrudbeans-template-kotlin](https://github.com/manosbatsis/scrudbeans-template-kotlin)
> for examples.

### Using Gradle

If you use Java:

```groovy
dependencies {
	//...
	implementation 'com.github.manosbatsis.scrudbeans:scrudbeans-spring-boot-starter:$scrudbeans_version'
	implementation("com.github.manosbatsis.scrudbeans:scrudbeans-annotation-processor-java:$scrudbeans_version")
}
```

If you use Kotlin:

```groovy
dependencies {
	//...
	implementation 'com.github.manosbatsis.scrudbeans:scrudbeans-spring-boot-starter:$scrudbeans_version'
	kapt("com.github.manosbatsis.scrudbeans:scrudbeans-annotation-processor-kotlin:$scrudbeans_version")
}
```

> You may have to setup apt or kapt for Java or Kotlin respectively,
> see [scrudbeans-template-java](https://github.com/manosbatsis/scrudbeans-template-java)
> and [scrudbeans-template-kotlin](https://github.com/manosbatsis/scrudbeans-template-kotlin)
> for examples.

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
