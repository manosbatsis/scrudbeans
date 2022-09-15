package buildsrc.convention

import buildsrc.config.createManosBatsisPom
import buildsrc.config.credentialsAction

plugins {
    `maven-publish`
    signing
}

description = "Configuration for publishing Jvm libraries to Sonatype Maven Central"

val signingKeyId: String? by project
val signingKey: String? by project
val signingPassword: String? by project

val signingEnabled: Provider<Boolean> = provider {
    signingKeyId != null && signingKey != null && signingPassword != null
}

tasks.withType<AbstractPublishToMaven>().configureEach {
    // Gradle warns about some signing tasks using publishing task outputs without explicit dependencies
    dependsOn(tasks.withType<Sign>())
}

publishing {
    repositories {
        // publish to local dir, for testing
        maven(rootProject.layout.buildDirectory.dir("maven-internal")) {
            name = "LocalProjectDir"
        }
    }
    publications.create<MavenPublication>("mavenJava") {
        from(components["java"])

        createManosBatsisPom {
            name.set("${project.name}")
        }
    }
}

signing {
    if (signingEnabled.get()) {
        sign(publishing.publications["mavenJava"])

        useInMemoryPgpKeys(signingKeyId, signingKey, signingPassword)
    }
}
