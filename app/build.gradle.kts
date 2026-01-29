plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.graffzone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.graffzone"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(platform("androidx.compose:compose-bom:2026.01.00"))
    androidTestImplementation(platform("androidx.compose:compose-bom:2026.01.00"))

    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-compose:1.10.0")

    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    debugImplementation("androidx.compose.ui:ui-tooling")

    implementation("androidx.navigation:navigation-compose:2.8.6")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.7")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.1")

    // Room (local DB)
    implementation("androidx.room:room-runtime:2.8.4")
    implementation("androidx.room:room-ktx:2.8.4")
    ksp("androidx.room:room-compiler:2.8.4")

    // Images
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Video
    implementation("androidx.media3:media3-exoplayer:1.9.1")
    implementation("androidx.media3:media3-ui:1.9.1")

    // Map (OSM)
    implementation("org.osmdroid:osmdroid-android:6.1.20")
}
