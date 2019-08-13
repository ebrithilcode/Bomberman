import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    application
}

application {
    mainClassName = "com.ebrithilcode.bomberman.client.ClientKt"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(group = "org.processing", name= "core", version= "2.2.1")

}