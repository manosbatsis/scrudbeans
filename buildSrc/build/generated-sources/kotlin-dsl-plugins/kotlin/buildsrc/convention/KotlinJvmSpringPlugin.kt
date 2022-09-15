package buildsrc.convention


/**
 * Precompiled [kotlin-jvm-spring.gradle.kts][buildsrc.convention.Kotlin_jvm_spring_gradle] script plugin.
 *
 * @see buildsrc.convention.Kotlin_jvm_spring_gradle
 */
class KotlinJvmSpringPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("buildsrc.convention.Kotlin_jvm_spring_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
