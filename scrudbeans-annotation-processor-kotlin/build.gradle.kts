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
    api(project(":scrudbeans-jpa"))
    implementation(platform(libs.spring.boot.dependencies))
    api(libs.kotlin.utils.kapt)
    implementation(libs.kotlinpoet)
    implementation(libs.evo.inflector)
}
