plugins {
    buildsrc.convention.`kotlin-jvm-spring`
    buildsrc.convention.`publish-jvm`
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

dependencies {
    implementation(project(":scrudbeans-jpa"))
    implementation("com.github.manosbatsis.kotlin-utils:kotlin-utils-kapt:${Versions.kotlinUtilsVersion}")
    implementation("com.squareup:kotlinpoet:${Versions.kotlinPoetVersion}")
    implementation("org.atteo:evo-inflector:${Versions.evoInflectorVersion}")
    // implementation("org.springframework.boot:spring-boot-autoconfigure")
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}
