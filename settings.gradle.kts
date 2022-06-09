
rootProject.name = "scrudbeans"
pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://repo.spring.io/milestone") }
    }
}

include(
    "scrudbeans-annotation-processor-kotlin",
    "scrudbeans-api",
    "scrudbeans-common",
    "scrudbeans-integration-tests-kotlin",
    "scrudbeans-jpa",
    "scrudbeans-spring-boot-autoconfigure",
    "scrudbeans-spring-boot-starter"
)
