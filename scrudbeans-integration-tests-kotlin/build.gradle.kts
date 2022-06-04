plugins {
    kotlin("kapt")
}
val rsqlJpaSpringBootStarterVersion: String by System.getProperties()

dependencies {
    implementation(project(":scrudbeans-spring-boot-starter"))
    kapt(project(":scrudbeans-annotation-processor-kotlin"))
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("io.github.perplexhub:rsql-jpa-spring-boot-starter:$rsqlJpaSpringBootStarterVersion")
    testImplementation(project(":scrudbeans-spring-boot-starter-test"))
}