plugins {
    kotlin("jvm") version "1.8.0"
    application
}

group = "com.ladsers.web"
version = "23.10.0"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(11)
}

application {
    mainClass.set("MainKt")
}