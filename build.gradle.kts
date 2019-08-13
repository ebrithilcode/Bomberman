import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.3.41"
    application
}

application {
    mainClassName = "com.ebrithilcode.bomberman.BomberManKt"
}

group = "com.ebrithilcode.bomberman"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        jcenter()
    }

}



tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
}