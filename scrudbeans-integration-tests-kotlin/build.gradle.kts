plugins {
    `java-library`
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.kotlin.noarg)
    alias(libs.plugins.kotlin.jpa)
    alias(libs.plugins.spring.boot)
}

noArg {
    annotation("com.github.manosbatsis.scrudbeans.api.annotation.NoArg")
}

springBoot {
    buildInfo()
}

dependencies {
    api(project(":scrudbeans-spring-boot-starter"))
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.error.handling.spring.boot.starter)
    implementation("org.springframework.boot:spring-boot-properties-migrator")
    kapt(project(":scrudbeans-annotation-processor-kotlin"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testImplementation("com.squareup.okhttp3:okhttp:4.10.0")
}
