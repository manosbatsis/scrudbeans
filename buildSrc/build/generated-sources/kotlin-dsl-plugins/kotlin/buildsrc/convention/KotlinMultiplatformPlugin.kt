package buildsrc.convention


/**
 * Precompiled [kotlin-multiplatform.gradle.kts][buildsrc.convention.Kotlin_multiplatform_gradle] script plugin.
 *
 * @see buildsrc.convention.Kotlin_multiplatform_gradle
 */
class KotlinMultiplatformPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("buildsrc.convention.Kotlin_multiplatform_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
