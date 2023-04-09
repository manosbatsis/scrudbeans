plugins {
    `java-library`
    kotlin("jvm")
    buildsrc.convention.`publish-jvm`
}

java {
    withJavadocJar()
    withSourcesJar()
}
dependencies {
    api(project(":scrudbeans-spring-boot-autoconfigure"))
    implementation(platform(libs.spring.boot.dependencies))
}
