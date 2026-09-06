plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinx.serialization)
}

kotlin {
    androidTarget {
        @OptIn(org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    sourceSets {
        androidMain.dependencies {
            implementation(project(":core:ui"))
            implementation(project(":core:navigation"))
            implementation(project(":shared"))
            
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.ui)
            implementation("androidx.compose.material:material-icons-extended:1.7.8")
            implementation(libs.androidx.lifecycle.viewmodelCompose)
            implementation(libs.androidx.lifecycle.runtimeCompose)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.tay.compose.library)
            
            if ("feature/orders" == "feature/monitoring" || "feature/orders" == "feature/cart") {
                implementation(libs.google.maps.compose)
                implementation(libs.play.services.maps)
            }
            if ("feature/orders" == "feature/cart") {
                implementation(libs.play.services.location)
            }
        }
    }
}

android {
    namespace = "com.pizzza.pizzzaapp.feature.orders"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    buildFeatures {
        compose = true
    }
}
