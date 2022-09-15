plugins {
    buildsrc.convention.base
    buildsrc.convention.`kotlin-jvm`
    buildsrc.convention.`publish-jvm`
    id("io.github.gradle-nexus.publish-plugin")
}

@Suppress("PropertyName")
val release_version: String by project
version = release_version

tasks.dokkaHtmlMultiModule.configure {
    includes.from("README.md")
    outputDirectory.set(buildDir.resolve("dokkaHtmlMultiModule"))
}

nexusPublishing {
    repositories {
        sonatype()
    }
}

tasks.wrapper {
    gradleVersion = "7.5"
    distributionType = Wrapper.DistributionType.ALL
}
