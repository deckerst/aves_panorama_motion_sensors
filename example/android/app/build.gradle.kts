plugins {
    id("com.android.application")
    // The Flutter Gradle Plugin must be applied after the Android and Kotlin Gradle plugins.
    id("dev.flutter.flutter-gradle-plugin")
}

android {
    namespace = "finaldev.motion_sensors_example"
    compileSdk = flutter.compileSdkVersion
    ndkVersion = flutter.ndkVersion

    kotlin {
        // Gradle looks up toolchain JDKs (for this app and each of its modules)
        // among locally installed JDKs (including in `~/.gradle/jdks/` and `~/jdks`)
        // and download them from configured repositories if necessary.
        jvmToolchain(21)
    }

    defaultConfig {
        applicationId = "finaldev.motion_sensors_example"
        // You can update the following values to match your application needs.
        // For more information, see: https://flutter.dev/to/review-gradle-config.
        minSdk = flutter.minSdkVersion
        targetSdk = flutter.targetSdkVersion
        versionCode = flutter.versionCode
        versionName = flutter.versionName
    }

    buildTypes {
        release {
            // Signing with the debug keys for now, so `flutter run --release` works.
            signingConfig = signingConfigs.getByName("debug")
        }
    }
}

flutter {
    source = "../.."
}
