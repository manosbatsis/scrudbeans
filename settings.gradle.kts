
rootProject.name = "scrudbeans"

include(
    "scrudbeans-annotation-processor-kotlin",
    "scrudbeans-api",
    "scrudbeans-common",
    "scrudbeans-integration-tests-kotlin",
    "scrudbeans-jpa",
    "scrudbeans-spring-boot-autoconfigure",
    "scrudbeans-spring-boot-starter"
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

apply(from = "./buildSrc/repositories.settings.gradle.kts")

@Suppress("UnstableApiUsage") // Central declaration of repositories is an incubating feature
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS)
}
