package buildsrc.config

import org.gradle.api.Action
import org.gradle.api.Project
import org.gradle.api.artifacts.repositories.PasswordCredentials
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.publish.maven.MavenPom
import org.gradle.api.publish.maven.MavenPublication

fun MavenPublication.createManosBatsisPom(
    projectName: String,
    projectDescription: String,
    configure: MavenPom.() -> Unit = {}
): Unit = pom {

    name.set(projectName)
    description.set(projectDescription)
    url.set("https://manosbatsis.github.io/${projectName}/")
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
        connection.set("scm:git:git://github.com/manosbatsis/${projectName}.git")
        developerConnection.set("scm:git:ssh://github.com:manosbatsis/${projectName}.git")
        url.set("https://github.com/manosbatsis/${projectName}/tree/master")
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
    repositoryVendor: String
): Provider<Action<PasswordCredentials>> = zip(
    gradleProperty("${repositoryVendor}Username"),
    gradleProperty("${repositoryVendor}Password"),
) { user, pass ->
    Action<PasswordCredentials> {
        username = user
        password = pass
    }
}
