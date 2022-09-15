package buildsrc.config

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.kotlin.dsl.findByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.jvm.KotlinJvmTarget

fun MavenPublication.createManosBatsisPom(
    configure: MavenPom.() -> Unit = {}
): Unit = pom {
    name.set("scrudbeans")
    description.set("Hands-free, RESTful, extensible SCRUD for your Kotlin JPA entities in Spring Boot ")
    url.set("https://manosbatsis.github.io/scrudbeans/")
    licenses {
        license {
            name.set("GNU Lesser General Public License")
            url.set("https://opensource.org/licenses/LGPL-3.0")
        }
    }
    developers {
        developer {
            id.set("manosbatsis")
            name.set("Manos Batsis")
        }
    }
    scm {
        connection.set("scm:git:git://github.com/manosbatsis/scrudbeans.git")
        developerConnection.set("scm:git:ssh://github.com:manosbatsis/scrudbeans.git")
        url.set("https://github.com/manosbatsis/scrudbeans/tree/master")
    }
    configure()
}

/**
 * Fetches credentials from `gradle.properties`, environment variables, or command line args.
 *
 * See https://docs.gradle.org/current/userguide/declaring_repositories.html#sec:handling_credentials
 */
// https://github.com/gradle/gradle/issues/20925
fun ProviderFactory.credentialsAction(
    repositoryName: String
): Provider<Action<PasswordCredentials>> = zip(
    gradleProperty("${repositoryName}Username"),
    gradleProperty("${repositoryName}Password"),
) { user, pass ->
    Action<PasswordCredentials> {
        username = user
        password = pass
    }
}

/**
 * Check if a Kotlin Mutliplatform project also has Java enabled.
 *
 * Logic from [KotlinJvmTarget.withJava]
 */
fun Project.isKotlinMultiplatformJavaEnabled(): Boolean {
    val multiplatformExtension: KotlinMultiplatformExtension? =
        extensions.findByType(KotlinMultiplatformExtension::class)

    return multiplatformExtension?.targets
        ?.any { target -> target is KotlinJvmTarget && target.withJavaEnabled }
        ?: false
}
