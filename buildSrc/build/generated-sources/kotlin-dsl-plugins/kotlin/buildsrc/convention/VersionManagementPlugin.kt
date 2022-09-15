package buildsrc.convention


/**
 * Precompiled [version-management.gradle.kts][buildsrc.convention.Version_management_gradle] script plugin.
 *
 * @see buildsrc.convention.Version_management_gradle
 */
class VersionManagementPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("buildsrc.convention.Version_management_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
