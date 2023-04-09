package buildsrc.convention

import org.gradle.kotlin.dsl.kotlin

plugins {
    id("buildsrc.convention.kotlin-jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.noarg")
    id("io.spring.dependency-management")
}
