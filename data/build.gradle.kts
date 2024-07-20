plugins {
    alias(libs.plugins.xbot.android.library)
    alias(libs.plugins.xbot.android.hilt)
}
android {
    namespace = "com.xbot.data"

    defaultConfig {
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Project-level dependencies
    implementation(project(":domain"))

    // Retrofit
    implementation(libs.okhttp.core)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)
    implementation(libs.retrofit.adapters.result)
    implementation(libs.retrofit.adapters.serialization)

    // AndroidX dependencies
    implementation(libs.androidx.dataStore)

    // Testing dependencies
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.test.espresso.core)
    androidTestImplementation(libs.androidx.test.ext)
}