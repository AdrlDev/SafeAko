import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.sprtcoding.safeako"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.sprtcoding.safeako"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Load API_KEY from local.properties and assign it to buildConfigField
        val properties = Properties()
        val localProperties = rootProject.file("local.properties")
        if (localProperties.exists()) {
            properties.load(localProperties.inputStream())
        }
        val apiKey: String = properties.getProperty("API_KEY", "")
        buildConfigField("String", "API_KEY", "\"$apiKey\"")

    }

    buildFeatures {
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.ui.desktop)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Import the BoM for the Firebase platform
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.storage)

    // retrofit
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    implementation(libs.picasso)
    implementation(libs.circleimageview)
    //implementation(libs.imagepicker)
    // UCrop dependency
    //implementation(libs.ucrop)

    //easy permission library
    implementation(libs.easypermissions)
    implementation(libs.github.imagepicker)
    //implementation(libs.imagepicker.support)

    implementation(libs.mpandroidchart)
}