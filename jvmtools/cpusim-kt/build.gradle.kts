plugins {
    scala

    // Apply the application plugin to add support for building a CLI application in Java.
    application

    alias(libs.plugins.kotlin.jvm)
}

group = "me.johnl"
version = "1.0"

repositories {
    // Use Maven Central for resolving dependencies.
    mavenCentral()
}

dependencies {
    // pull in the compiler !!
    implementation(files("${projectDir}/../compiler/build/libs/compiler.jar"))

    // Use Scala 2.13 in our library project
    implementation(libs.scala.library)

    // This dependency is used by the application.
    //implementation(libs.guava)

    // Use Scalatest for testing our library
    testImplementation(libs.scalatest.v2.v13)

    // Need scala-xml at test runtime
    //testRuntimeOnly(libs.scala.xml.v2.v13)
//    testImplementation("org.jetbrains.kotlin:kotlin-test")
//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")

//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.6.0")
//    testImplementation("org.junit.jupiter:junit-jupiter-params:5.6.0")
//    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.6.0")

    testImplementation(kotlin("test"))
//    testImplementation(kotlin("test-junit5"))
    implementation("org.junit.platform:junit-platform-commons:1.11.3")

//    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5:2.1.0")
//
////    testImplementation(libs.junit.jupiter)
//
//    testImplementation("org.junit.jupiter:junit-jupiter-api:5.11.3")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.11.3")
////    testRuntimeOnly("org.junit.platform:junit-platform-console:1.2.0")
//
////    testImplementation(platform("org.junit:junit-bom:5.8.1"))
////    testImplementation("org.junit.jupiter:junit-jupiter:1.8.1")
    testImplementation("org.junit.platform:junit-platform-launcher:1.11.3")
////    testImplementation("org.junit.platform:junit-platform-commons:1.8.1")

}

// Apply a specific Java toolchain to ease working on different environments.
java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}
//
//testing {
//    suites {
//        // Configure the built-in test suite
//        val test by getting(JvmTestSuite::class) {
//            // Use JUnit Jupiter test framework
//            useJUnitJupiter("1.8.2")
//        }
//    }
//}

tasks.test {
    // Use the built-in JUnit support of Gradle.
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}

application {
    // Define the main class for the application.
    mainClass.set("MainKt")
}
