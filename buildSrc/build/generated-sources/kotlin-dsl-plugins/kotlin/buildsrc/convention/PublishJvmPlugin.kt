package buildsrc.convention


/**
 * Precompiled [publish-jvm.gradle.kts][buildsrc.convention.Publish_jvm_gradle] script plugin.
 *
 * @see buildsrc.convention.Publish_jvm_gradle
 */
class PublishJvmPlugin : org.gradle.api.Plugin<org.gradle.api.Project> {
    override fun apply(target: org.gradle.api.Project) {
        try {
            Class
                .forName("buildsrc.convention.Publish_jvm_gradle")
                .getDeclaredConstructor(org.gradle.api.Project::class.java, org.gradle.api.Project::class.java)
                .newInstance(target, target)
        } catch (e: java.lang.reflect.InvocationTargetException) {
            throw e.targetException
        }
    }
}
