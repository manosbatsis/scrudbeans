plugins {
    `java-library`
    kotlin("jvm")
    kotlin("plugin.spring")
    buildsrc.convention.`publish-jvm`
}

java {
    withJavadocJar()
    withSourcesJar()
}

dependencies {
    api(project(":scrudbeans-jpa"))
    implementation(platform(libs.spring.boot.dependencies))
}
