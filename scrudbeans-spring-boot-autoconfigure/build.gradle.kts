plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.dokka)
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
