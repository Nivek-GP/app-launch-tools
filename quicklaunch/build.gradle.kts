plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "dev.kevin.quicklaunch"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.kevin.quicklaunch"
        minSdk = 33
        // Android 13 es el SO del HiBy M300.
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"))
            // APK de uso personal (sideload): se firma con la clave debug.
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }

    androidResources {
        localeFilters += listOf("en", "es")
    }

    lint {
        disable += listOf("ExpiredTargetSdkVersion", "OldTargetApi")
    }
}

dependencies {
    implementation(project(":core"))
}
