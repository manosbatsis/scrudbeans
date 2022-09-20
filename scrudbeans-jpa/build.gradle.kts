plugins {
    buildsrc.convention.`kotlin-jvm-spring`
    buildsrc.convention.`publish-jvm`
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}

dependencies {
    api(project(":scrudbeans-common"))
    api("org.springframework.boot:spring-boot-autoconfigure")
    api("org.springdoc:springdoc-openapi-kotlin:${Versions.springdocOpenapiVersion}")
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("io.github.perplexhub:rsql-jpa-spring-boot-starter:${Versions.rsqlJpaSpringBootStarterVersion}")
}
