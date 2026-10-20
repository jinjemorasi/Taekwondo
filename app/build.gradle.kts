plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "dev.taekwondo.academy"
    compileSdk = 37

    defaultConfig {
        applicationId = "dev.taekwondo.academy"
        minSdk = 24
        targetSdk = 37
        versionCode = 4
        versionName = "1.2.1"
    }

    signingConfigs {
        create("fixed") {
            val ksPath = System.getenv("TAEKWONDO_KEYSTORE_FILE")
            if (ksPath != null) {
                storeFile = file(ksPath)
                storePassword = System.getenv("TAEKWONDO_STORE_PASS")
                keyAlias = System.getenv("TAEKWONDO_KEY_ALIAS")
                keyPassword = System.getenv("TAEKWONDO_KEY_PASS")
            }
        }
    }

    buildTypes {
        debug {
            if (System.getenv("TAEKWONDO_KEYSTORE_FILE") != null) {
                signingConfig = signingConfigs.getByName("fixed")
            }
        }
        release {
            if (System.getenv("TAEKWONDO_KEYSTORE_FILE") != null) {
                signingConfig = signingConfigs.getByName("fixed")
            }
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    buildFeatures {
        compose = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlin {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.core)
    debugImplementation(libs.androidx.compose.ui.tooling)
}
