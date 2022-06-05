import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion: String by System.getProperties()
	val ktlintVersion: String by System.getProperties()
	val springBootVersion: String by System.getProperties()
	val springDependencyManagementVersion: String by System.getProperties()

	id("org.springframework.boot") version springBootVersion apply false
	id("io.spring.dependency-management") version springDependencyManagementVersion apply false
	id("org.jlleitschuh.gradle.ktlint") version ktlintVersion apply false

	kotlin("jvm") version kotlinVersion apply false
	kotlin("kapt") version kotlinVersion apply false
	kotlin("plugin.spring") version kotlinVersion apply false
	kotlin("plugin.jpa") version kotlinVersion apply false
}

subprojects {

	apply {
		plugin("kotlin")
		plugin("org.jlleitschuh.gradle.ktlint")
		plugin("org.jetbrains.kotlin.jvm")
		plugin("org.springframework.boot")
		plugin("io.spring.dependency-management")
		plugin("org.jetbrains.kotlin.plugin.spring")
		plugin("org.jetbrains.kotlin.plugin.jpa")
	}

	group = "com.github.manosbatsis.scrudbeans"
	version = "0.0.1-SNAPSHOT"


	repositories {
		mavenCentral()
		maven { url = uri("https://repo.spring.io/milestone") }
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
	}
}