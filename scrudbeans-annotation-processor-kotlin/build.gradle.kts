
tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

val kotlinUtilsVersion: String by System.getProperties()
val kotlinPoetVersion: String by System.getProperties()
val evoInflectorVersion: String by System.getProperties()

dependencies {
    implementation(project(":scrudbeans-jpa"))
    implementation("com.github.manosbatsis.kotlin-utils:kotlin-utils-kapt:$kotlinUtilsVersion")
    implementation("com.squareup:kotlinpoet:$kotlinPoetVersion")
    implementation("org.atteo:evo-inflector:$evoInflectorVersion")
    // implementation("org.springframework.boot:spring-boot-autoconfigure")
    // implementation("org.springframework.boot:spring-boot-starter-web")
    // implementation("org.springframework.boot:spring-boot-starter-validation")
    // implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
}