group = "finaldev.motion_sensors"
version = "1.0-SNAPSHOT"

buildscript {
    val agp_version = "9.1.1"
    val kotlin_version = "2.3.21"

    repositories {
        google()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:$agp_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.android.library")
}

android {
    namespace = "finaldev.motion_sensors"

    compileSdk = flutter.compileSdkVersion

    kotlin {
        jvmToolchain(21)
    }

    sourceSets {
        getByName("main") {
            java.directories.add("src/main/kotlin")
        }
    }

    defaultConfig {
        minSdk = 24
    }
}
