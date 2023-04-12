plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.jpa)
    buildsrc.convention.`publish-jvm`
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(project(":scrudbeans-common"))
    implementation(platform(libs.spring.boot.dependencies))
    api("org.springframework.boot:spring-boot-autoconfigure")
    api(libs.springdoc.openapi.kotlin)
    api(libs.rsql.jpa.spring.boot.starter)
}
