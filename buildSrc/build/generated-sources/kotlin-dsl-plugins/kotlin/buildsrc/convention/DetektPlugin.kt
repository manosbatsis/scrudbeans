package buildsrc.convention


/**
 * Precompiled [detekt.gradle.kts][buildsrc.convention.Detekt_gradle] script plugin.
 *
 * @see buildsrc.convention.Detekt_gradle
 */
class DetektPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("buildsrc.convention.Detekt_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
