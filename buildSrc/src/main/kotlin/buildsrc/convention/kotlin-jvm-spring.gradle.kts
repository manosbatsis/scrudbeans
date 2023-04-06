package buildsrc.convention

import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.version
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("buildsrc.convention.kotlin-jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.noarg")

    id("io.spring.dependency-management")
}
