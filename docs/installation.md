---
title: Installation
---

This document will help you get a quick start with ScrudBeans and Spring Boot. 

## Using the Project Template 

The [scrudbeans-template](https://github.com/manosbatsis/scrudbeans-template) replicates 
this quick tutorial and can help you get started with ScrudBeans right away:

```bash
git clone https://github.com/manosbatsis/scrudbeans-template.git
``` 

## Using a Custom Project 

Alternatively, you can use an existing Spring Boot project or create a new one with 
[Spring Initializr](https://start.spring.io/). 

## ScrudBeans Starter

If you are not using scrudbeans-template, the ScrudBeans autoconfiguration starter for 
Spring Boot is all your project needs in terms of dependencies. 

### Using Maven

To add the dependency with Maven:

```xml
<dependency>
	<groupId>com.github.manosbatsis.scrudbeans</groupId>
	<artifactId>scrudbeans-spring-boot-starter</artifactId>
	<version>0.3</version>
</dependency>
```

### Using Gradle

To add the dependency with Gradle:

```groovy
dependencies {
	implementation 'com.github.manosbatsis.scrudbeans:scrudbeans-spring-boot-starter:0.3'
}
```

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
spring.jpa.properties.javax.persistence.validation.mode=none

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