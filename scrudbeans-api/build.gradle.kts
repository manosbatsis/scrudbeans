plugins {
    `java-library`
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}
tasks.named<Jar>("jar") {
    enabled = true
}

val kotlinUtilsVersion: String by System.getProperties()
val commonsLang3Version: String by System.getProperties()
val commonsCollections4Version: String by System.getProperties()
val commonsFileUploadVersion: String by System.getProperties()

dependencies {
    api("com.github.manosbatsis.kotlin-utils:kotlin-utils-api:$kotlinUtilsVersion")
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
}