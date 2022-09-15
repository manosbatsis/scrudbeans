package buildsrc.convention;                            

           
                                                      
                                                      
                                                    

plugins {
    `java-library`
    kotlin("jvm")
    kotlin("kapt")
    id("org.jetbrains.dokka")

    id("buildsrc.convention.base")
    id("org.jlleitschuh.gradle.ktlint")
                                            
}