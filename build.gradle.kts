import org.gradle.api.JavaVersion.VERSION_1_8

group = "com.github.mbto.cutimage"
version = "2.0"

plugins {
    java
    application
}

repositories {
    jcenter()
}

tasks {
    compileJava { options.encoding = "UTF-8" }
    compileTestJava { options.encoding = "UTF-8" }
}

dependencies {
    compile("com.beust:jcommander:1.78")

    val lombokVer = "1.18.12"
    compileOnly("org.projectlombok:lombok:$lombokVer");
    annotationProcessor("org.projectlombok:lombok:$lombokVer");
    testCompile("org.projectlombok:lombok:$lombokVer");
    testAnnotationProcessor("org.projectlombok:lombok:$lombokVer");

    testCompile("junit:junit:4.13.2")
}

application {
    mainClassName = "com.github.mbto.cutimage.Application"

    configure<JavaPluginConvention> {
        sourceCompatibility = VERSION_1_8
    }
}
