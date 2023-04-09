import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm") version "1.8.20"//kotlinVersion
    `kotlin-dsl`
}

// set the versions of Gradle plugins that the subprojects will use here

val kotlinVersion by System.getProperties()
val ktlintVersion by System.getProperties()
val springBootVersion by System.getProperties()
val springDependencyManagementVersion by System.getProperties()
val detektPlugin by System.getProperties()
val spotlessPlugin by System.getProperties()



val gradleNexusPublishPlugin = "1.1.0"
val gradleTestLoggerPlugin = "3.1.0"
val gradleVersionsPlugin = "0.39.0"
val kotlinDokkaPlugin = "1.8.10"
val kotlinxKoverPlugin = "0.5.0"
val useLatestVersionsPlugin = "0.2.18"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
kotlin {
    //explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
    // Add Deps to compilation, so it will become available in main project
    sourceSets.getByName("main").kotlin.srcDir("buildSrc/src/main/kotlin")
}

dependencies {
    //implementation(platform("org.jetbrains.kotlin:kotlin-bom:${kotlinVersion}"))
    implementation("org.jetbrains.kotlin:kotlin-serialization")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-compiler-embeddable")
    implementation("org.jetbrains.kotlin:kotlin-allopen")
    implementation("org.jetbrains.kotlin:kotlin-noarg")
    implementation("org.jetbrains.kotlin.plugin.jpa:org.jetbrains.kotlin.plugin.jpa.gradle.plugin:${kotlinVersion}")
    implementation("org.jetbrains.kotlin.plugin.spring:org.jetbrains.kotlin.plugin.spring.gradle.plugin:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    implementation("org.jlleitschuh.gradle.ktlint:org.jlleitschuh.gradle.ktlint.gradle.plugin:${ktlintVersion}")



    implementation(platform("org.springframework.boot:spring-boot-dependencies:${springBootVersion}"))
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${springBootVersion}")
    implementation("io.spring.gradle:dependency-management-plugin:${springDependencyManagementVersion}")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:${spotlessPlugin}")

    implementation("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:$detektPlugin")
    implementation("io.github.gradle-nexus:publish-plugin:$gradleNexusPublishPlugin")
    implementation("com.adarshr:gradle-test-logger-plugin:$gradleTestLoggerPlugin")
    implementation("com.github.ben-manes:gradle-versions-plugin:$gradleVersionsPlugin")
    implementation("org.jetbrains.dokka:dokka-gradle-plugin:$kotlinDokkaPlugin")
    implementation("org.jetbrains.kotlinx:kover:$kotlinxKoverPlugin")
    implementation("se.patrikerdes:gradle-use-latest-versions-plugin:$useLatestVersionsPlugin")
}
