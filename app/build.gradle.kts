plugins {
    alias(libs.plugins.android.application)
}
android {
    namespace = "com.marinov.youtube"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.marinov.youtube"
        minSdk = 16
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.3.1")
    testImplementation(libs.junit)
    implementation("org.mozilla.geckoview:geckoview:118.0.20230918143747")
}
