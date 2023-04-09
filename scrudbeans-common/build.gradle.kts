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
    api(project(":scrudbeans-api"))
    implementation(platform(libs.spring.boot.dependencies))
}
