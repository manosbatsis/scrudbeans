import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.dokka.gradle.AbstractDokkaTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.plugin.SpringBootPlugin

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
    id("org.springframework.boot") apply false
    id("org.jetbrains.dokka")
    buildsrc.convention.`publish-jvm`
    id("io.github.gradle-nexus.publish-plugin")
    id("com.diffplug.spotless")

}

@Suppress("PropertyName")
val release_version: String by project
version = release_version

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
}
val dokkaEnabled: Provider<Boolean> = provider {
    tasks.withType<AbstractPublishToMaven>().any { it.hasTaskActions() }
}
tasks.dokkaHtmlMultiModule.configure {
    onlyIf{ dokkaEnabled.get() }
    includes.from("README.md")
    outputDirectory.set(buildDir.resolve("docs/apidoc"))
}


nexusPublishing {
    repositories {
        sonatype()
    }
}

tasks.wrapper {
    gradleVersion = "7.5"
    distributionType = Wrapper.DistributionType.ALL
}

subprojects {
    apply(plugin = "com.diffplug.spotless")

    spotless {
        format("misc") {
            target("**/*.gradle", "**/*.md", "**/.gitignore")

            trimTrailingWhitespace()
            indentWithTabs() // or spaces. Takes an integer argument if you don't like 4
            endWithNewline()
        }
        configure<com.diffplug.gradle.spotless.SpotlessExtension> {
            kotlin {
                // by default the target is every '.kt' and '.kts` file in the java sourcesets
                ktfmt() // has its own section below
                ktlint().userData(mapOf("disabled_rules" to "no-wildcard-imports"))
                    .setEditorConfigPath("${rootProject.projectDir}/.editorconfig")
                // diktat() // has its own section below
                // prettier(mapOf("prettier" to "2.0.5", "prettier-plugin-kotlin" to "2.1.0"))
                // .config(mapOf("parser" to "kotlin", "tabWidth" to 4))
                // make sure every file has the following copyright header.
                // licenseHeaderFile(rootProject.projectDir.resolve("etc/source-header/header.txt"))
            }
        }
    }

    tasks.withType<AbstractDokkaTask>().all {
        onlyIf{ dokkaEnabled.get() }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions.apply {
            jvmTarget = "17"
            apiVersion = "1.7"
            languageVersion = "1.7"
        }
    }

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
        testLogging.showStandardStreams = true
        testLogging.exceptionFormat = TestExceptionFormat.FULL
        systemProperties = mapOf(
            "junit.jupiter.execution.parallel.enabled" to true,
            "junit.jupiter.execution.parallel.mode.default" to "concurrent",
            "junit.jupiter.execution.parallel.mode.classes.default" to "concurrent"
        )
    }
}
