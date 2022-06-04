plugins {
    `java-library`
}
val springdocOpenapiVersion: String by System.getProperties()
val rsqlJpaSpringBootStarterVersion: String by System.getProperties()

dependencies {
    api(project(":scrudbeans-common"))
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springdoc:springdoc-openapi-kotlin:$springdocOpenapiVersion")
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("io.github.perplexhub:rsql-jpa-spring-boot-starter:$rsqlJpaSpringBootStarterVersion")
}
