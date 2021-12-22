import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    scala
    application
}

group = "me.johnl"
version = "1.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(15))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

dependencies {
    implementation(files( "${projectDir}/../compiler/build/libs/compiler.jar"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "15"
}

application {
    mainClass.set("MainKt")
}
