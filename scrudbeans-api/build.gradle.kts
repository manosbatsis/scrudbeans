plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.dokka)
    // id("org.springframework.boot")
    buildsrc.convention.`publish-jvm`
}

java {
    withJavadocJar()
    withSourcesJar()
}

// tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
//     enabled = false
// }
//
// tasks.named<Jar>("jar") {
//     enabled = true
// }

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    api(libs.kotlin.utils.api)
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
}
