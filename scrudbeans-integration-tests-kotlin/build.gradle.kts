plugins {
    kotlin("kapt")
    kotlin("plugin.noarg")
}

noArg {
    annotation("com.github.manosbatsis.scrudbeans.api.annotation.NoArg")
}

springBoot {
    buildInfo()
}

val errorHandlingSpringBootStarterVersion: String by System.getProperties()
dependencies {
    implementation(project(":scrudbeans-spring-boot-starter"))
    implementation("io.github.wimdeblauwe:error-handling-spring-boot-starter:$errorHandlingSpringBootStarterVersion")
    kapt(project(":scrudbeans-annotation-processor-kotlin"))
    testImplementation(project(":scrudbeans-spring-boot-starter-test"))
    testImplementation("com.h2database:h2")
}