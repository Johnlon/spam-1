import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
//    scala
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
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))
}

dependencies {
    implementation(files( "${projectDir}/../compiler/build/libs/compiler.jar"))
}

val jar by tasks.getting(Jar::class) {
    exclude("module-info.class", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "META-INF/*.MF")

    manifest {
        attributes["Main-Class"] = "MainKt"
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
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
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}