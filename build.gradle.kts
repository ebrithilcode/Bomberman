plugins {
    kotlin("jvm") version "1.3.41"
    id("kotlinx-serialization") version "1.3.41"
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

/*tasks {
    register("run 'em all", GradleBuild::class) {
        tasks = listOf("client:run", "server:run")
    }
}*/

tasks.register<Task>("run 'em all") {
    dependsOn("client:run", "server:run")
}

application {
    //TODO fix
    mainClassName = ""
}

subprojects {
    apply(plugin="kotlin")
    apply(plugin = "kotlinx-serialization")

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

    tasks.test {
        useJUnitPlatform()
    }


    dependencies {
        implementation(group= "org.processing", name= "core", version= "3.3.6")
        implementation(group= "com.beust", name= "klaxon", version= "5.0.11")
        implementation(project(":common"))

        compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")


        testCompile("org.assertj:assertj-core:3.11.1")

        testImplementation("org.junit.jupiter:junit-jupiter-api:5.3.1")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.3.1")

        //testImplementation("org.junit.jupiter:junit-jupiter:5.5.1")
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

        compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")
    }
    application {
        mainClassName = "com.ebrithilcode.bomberman.server.AppKt"
    }
}

project(":common") {
    dependencies {
        implementation(group= "org.processing", name= "core", version= "3.3.6")
        implementation(group= "com.beust", name= "klaxon", version= "5.0.11")

        compile("org.jetbrains.kotlinx:kotlinx-serialization-runtime:0.11.1")
    }
}

