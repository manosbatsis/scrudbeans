
rootProject.name = "scrudbeans"
pluginManagement {
    repositories {
        maven { url = uri("https://repo.spring.io/milestone") }
        gradlePluginPortal()
    }
}

include(
    "scrudbeans-annotation-processor-kotlin",
    "scrudbeans-api",
    "scrudbeans-common",
    "scrudbeans-integration-tests-kotlin",
    "scrudbeans-jpa",
    "scrudbeans-spring-boot-autoconfigure",
    "scrudbeans-spring-boot-starter",
    "scrudbeans-spring-boot-starter-test"
)
