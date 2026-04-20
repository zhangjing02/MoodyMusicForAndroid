plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.example.moodymusicforandroid"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.moodymusicforandroid"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        manifestPlaceholders["JPUSH_PKGNAME"] = "com.example.moodymusicforandroid"
        manifestPlaceholders["JPUSH_APPKEY"] = "cab5e87b9dd9b0acd6df56c3"
        manifestPlaceholders["JPUSH_CHANNEL"] = "developer-default"
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
        dataBinding = true
        viewBinding = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // CommonBase module (provides base classes, network, API services, data models, and image loading)
    implementation(project(":commonbase"))

    // AndroidX Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)

    // Material & UI
    implementation(libs.androidx.material)
    implementation(libs.androidx.constraintlayout)

    // UI Widget Helper - 圆角、边框、渐变、状态切换
    implementation(libs.rwidget.helper)

    // JPush
    implementation(libs.jpush)

    // Testing
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}