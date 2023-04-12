import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.java
import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm") version "1.8.10"
    `kotlin-dsl`
}

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

