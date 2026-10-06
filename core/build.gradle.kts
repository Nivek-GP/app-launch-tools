plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "dev.kevin.dapcore"
    compileSdk = 37

    defaultConfig {
        minSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    api(libs.androidx.core.ktx)
    api(libs.androidx.appcompat)
    api(libs.material)
    api(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.recyclerview)
}
