package buildsrc.convention

import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.jlleitschuh.gradle.ktlint.KtlintExtension

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")

    id("buildsrc.convention.base")
    id("org.jlleitschuh.gradle.ktlint")
    // TODO id("buildsrc.convention.detekt")
}

dependencies {
    testImplementation(Deps.jUnit)
    testImplementation(Deps.strikt)
    testImplementation(Deps.Mockk.mockk)
    testImplementation(Deps.Mockk.dslJvm)
}

configure<KtlintExtension> {
    verbose.set(true)
    disabledRules.set(
        setOf(
            "import-ordering",
            "no-wildcard-imports",
            "max_line_length"
        )
    )
}

kotlin {
    //explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions.apply {
        jvmTarget = "17"
        freeCompilerArgs += listOf("-Xjsr305=strict")
        apiVersion = "1.6"
        languageVersion = "1.6"
    }
}

tasks.withType<Test>().configureEach {
    useJUnitPlatform()
    systemProperties = mapOf(
        "junit.jupiter.execution.parallel.enabled" to true,
        "junit.jupiter.execution.parallel.mode.default" to "concurrent",
        "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
    )
}

tasks.named<Jar>("javadocJar") {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(tasks.dokkaJavadoc)
}
