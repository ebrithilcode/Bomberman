plugins {
    kotlin("jvm") version "1.3.41"
    application
}

allprojects {
    group = "com.ebrithilcode.bomberman"
    version = "1.0"
    repositories {
        //mavenCentral()
        jcenter()
    }
}

application {
    //TODO fix
    mainClassName = ""
}

subprojects {
    apply(plugin="kotlin")
    dependencies {
        implementation(group= "org.jetbrains.kotlin", name= "kotlin-stdlib-jdk8", version= "1.3.41")
        implementation(group= "org.jetbrains.kotlinx", name= "kotlinx-coroutines-core", version= "1.3.0-RC2")
    }
}

project(":gridBuilder") {
    apply(plugin="application")
    dependencies {
        implementation(group= "org.processing", name= "core", version= "3.3.6")
        implementation(project(":server"))
    }
    application {
        mainClassName = "com.ebrithilcode.bomberman.gridBuilder.GridBuilderKt"
    }
}

project(":client") {
    apply(plugin="application")
    dependencies {
        implementation(group= "org.processing", name= "core", version= "3.3.6")
        implementation(group= "com.beust", name= "klaxon", version= "5.0.11")
        implementation(project(":common"))
    }
    application {
        mainClassName = "com.ebrithilcode.bomberman.client.AppKt"
    }
}

project(":server") {
    apply(plugin="application")
    dependencies {
        implementation(group= "org.processing", name= "core", version= "3.3.6")
        implementation(group= "com.beust", name= "klaxon", version= "5.0.11")
        implementation(project(":common"))
    }
    application {
        mainClassName = "com.ebrithilcode.bomberman.server.AppKt"
    }
}

project(":common") {
    dependencies {
        implementation(group= "org.processing", name= "core", version= "3.3.6")
        implementation(group= "com.beust", name= "klaxon", version= "5.0.11")
    }
}

