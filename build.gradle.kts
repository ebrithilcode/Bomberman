plugins {
    kotlin("jvm") version "1.3.41"
}
subprojects {
    group = "com.ebrithilcode.bomberman"
    version = "1.0"
    repositories {
        mavenCentral()
    }
    dependencies {
        "implementation"(kotlin("stdlib-jdk8"))
        "implementation"(group= "javax.json", name= "javax.json-api", version= "1.1.4")
        "implementation"(group= "org.glassfish", name= "javax.json", version= "1.0.4")
        "implementation"(group= "org.jetbrains.kotlinx", name= "kotlinx-coroutines-core", version= "1.3.0-RC2")
        "implementation"(group = "org.processing", name= "core", version= "3.3.6")
    }
}

project(":gridBuilder") {
    dependencies {
        "implementation"(project(":server"))
    }
}


