import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin")
}

val properties=Properties().apply{
    val localPropertiesFile=rootProject.file("local.properties")
    if(localPropertiesFile.exists()){
        load(localPropertiesFile.inputStream())
    }
}

android {
    namespace = "com.example.thesswatair"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.thesswatair"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "IQAIR_API_KEY", "\"${properties.getProperty("IQAIR_API_KEY")?.trim() ?: ""}\"")
        buildConfigField("String", "OPEN_WEATHER_API_KEY", "\"${properties.getProperty("OPEN_WEATHER_API_KEY")?.trim() ?: ""}\"")
        buildConfigField("String", "WAQI_API_TOKEN", "\"${properties.getProperty("WAQI_API_TOKEN")?.trim() ?: ""}\"")
        buildConfigField("String", "GOOGLE_MAPS_API_KEY", "\"${properties.getProperty("GOOGLE_MAPS_API_KEY")?.trim() ?: ""}\"")
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.work:work-runtime-ktx:2.11.1")

    implementation("com.google.android.gms:play-services-location:21.3.0")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.2")

    //depedencies για Retrofit API όπου μετατρέπει το HTTP/HTTPS API σας σε μια διεπαφή Kotlin
    implementation ("com.google.code.gson:gson:2.9.1")
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")

    //depedencies για το Google Maps
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation("androidx.navigation:navigation-compose:2.9.7")
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}