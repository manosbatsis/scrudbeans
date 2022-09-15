package buildsrc.convention;                            

           
                                  
                                   
                                    
                                                      

plugins {
    id("buildsrc.convention.kotlin-jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    kotlin("plugin.noarg")

    id("org.springframework.boot")
    id("io.spring.dependency-management")
}