plugins {
    buildsrc.convention.`kotlin-jvm-spring`
}

noArg {
    annotation("com.github.manosbatsis.scrudbeans.api.annotation.NoArg")
}

springBoot {
    buildInfo()
}

dependencies {
    implementation(project(":scrudbeans-spring-boot-starter"))
    implementation("io.github.wimdeblauwe:error-handling-spring-boot-starter:${Versions.errorHandlingSpringBootStarterVersion}")
    kapt(project(":scrudbeans-annotation-processor-kotlin"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testImplementation("com.squareup.okhttp3:okhttp:4.10.0")
}
