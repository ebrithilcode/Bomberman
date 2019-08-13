import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "client.Client"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "org.processing", name= "core", version= "2.2.1")

}