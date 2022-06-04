plugins {
    `java-library`
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

dependencies {
    api(project(":scrudbeans-spring-boot-autoconfigure"))
    // implementation("org.springframework.boot:spring-boot-autoconfigure")
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}