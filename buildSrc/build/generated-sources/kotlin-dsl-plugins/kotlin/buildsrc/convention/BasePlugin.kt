package buildsrc.convention


/**
 * Precompiled [base.gradle.kts][buildsrc.convention.Base_gradle] script plugin.
 *
 * @see buildsrc.convention.Base_gradle
 */
class BasePlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("buildsrc.convention.Base_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
