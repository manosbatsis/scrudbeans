plugins {
    buildsrc.convention.base
    buildsrc.convention.`kotlin-jvm`
    buildsrc.convention.`publish-jvm`
}

@Suppress("PropertyName")
val release_version: String by project
version = release_version

tasks.dokkaHtmlMultiModule.configure {
    includes.from("README.md")
    outputDirectory.set(buildDir.resolve("dokkaHtmlMultiModule"))
}
