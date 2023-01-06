import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.0"
    id("org.jlleitschuh.gradle.ktlint") version "11.0.0"
    application
}

group = "dev.minz"
version = "1.0"

repositories {
    mavenCentral()
}

val nettyVersion by extra("4.1.86.Final")

dependencies {
    implementation("io.netty:netty-all:$nettyVersion")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = JavaVersion.VERSION_19.toString()
    }
}

application {
    mainClass.set("MainKt")
}
