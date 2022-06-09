import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion: String by System.getProperties()
	val ktlintVersion: String by System.getProperties()
	val springBootVersion: String by System.getProperties()
	val springDependencyManagementVersion: String by System.getProperties()

	id("org.springframework.boot") version springBootVersion apply false
	id("io.spring.dependency-management") version springDependencyManagementVersion apply false
	id("org.jlleitschuh.gradle.ktlint") version ktlintVersion apply false

	kotlin("jvm") version kotlinVersion
	kotlin("kapt") version kotlinVersion apply false
	kotlin("plugin.spring") version kotlinVersion apply false
	kotlin("plugin.jpa") version kotlinVersion apply false
	kotlin("plugin.noarg") version kotlinVersion apply false
	id("org.jetbrains.dokka") version kotlinVersion
}

// TODO: publish, see https://stackoverflow.com/questions/71181790/how-do-you-publish-a-kotlin-artifact-to-maven-central


allprojects{
	repositories {
		mavenLocal()
		mavenCentral()
		maven { url = uri("https://repo.spring.io/milestone") }
	}
}

tasks.dokkaHtmlMultiModule.configure {
	includes.from("README.md")
	outputDirectory.set(buildDir.resolve("dokkaHtmlMultiModule"))
}

subprojects {

	apply {
		plugin("org.jetbrains.kotlin.jvm")
		plugin("org.jlleitschuh.gradle.ktlint")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.plugin.spring")
		plugin("org.jetbrains.kotlin.plugin.jpa")
	}

	dependencies {
		val implementation by configurations
		implementation("org.jetbrains.kotlin:kotlin-reflect")
		implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	}

	configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
		verbose.set(true)
		disabledRules.set(
			setOf(
				"import-ordering",
				"no-wildcard-imports",
				"final-newline",
				"insert_final_newline",
				"max_line_length"
			)
		)
	}

	configure<JavaPluginExtension> {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}

	tasks.withType<KotlinCompile> {
		dependsOn("ktlintCheck")
		kotlinOptions {
			freeCompilerArgs = listOf("-Xjsr305=strict")
			jvmTarget = JavaVersion.VERSION_11.toString()
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
		testLogging {
			showStandardStreams = true
		}
	}
}