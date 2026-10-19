plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "space.balehroon.daftarsher"
    compileSdk = 35

    defaultConfig {
        applicationId = "space.balehroon.daftarsher"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"
    }
}
