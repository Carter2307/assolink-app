plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.map.secret)
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.assolink"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.assolink"
        minSdk = 24
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

}

dependencies {
    // CORE
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)

    // VIEWS
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.androidx.credentials)
    implementation(libs.androidx.credentials.play.services.auth)
    implementation(libs.googleid)

    // TESTING
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // NETWORK
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.okhttp)
    implementation(libs.glide)

    // VM & LIFECYCLE
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // DI
    implementation(libs.koin.core)
    implementation(libs.koin.android)


    // MAPS
    implementation(libs.play.services.maps)
    implementation(libs.android.maps.utils)


    // room
    implementation(libs.androidx.room.runtime)

    ksp(libs.androidx.room.compiler.v250)

    implementation(libs.androidx.room.ktx)

    // Firebase
    implementation (platform(libs.firebase.bom))
    implementation (libs.firebase.auth.ktx)
    implementation (libs.firebase.firestore.ktx)

    // Coroutines pour Firebase
    implementation (libs.kotlinx.coroutines.play.services)

}
