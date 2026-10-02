import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
    alias(libs.plugins.buildConfig)
    alias(libs.plugins.kotlinx.serialization)
}

val localProperties = Properties().apply {
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localPropertiesFile.inputStream().use { load(it) }
    }
}
val urlDev: String = localProperties.getProperty("URL_DEV") ?: "https://devapp.lapizzzeria.lol"
val urlPro: String = localProperties.getProperty("URL_PRO") ?: "https://app.lapizzzeria.lol"

buildConfig {
    packageName.set("com.pizzza.pizzzaapp.shared")
    val isIosDebug = System.getenv("CONFIGURATION") == "Debug"
    val isAndroidDebugOrDev = project.gradle.startParameter.taskNames.any {
        it.contains("Debug", ignoreCase = true) || it.contains("Dev", ignoreCase = true)
    }
    val isDebug = isAndroidDebugOrDev || isIosDebug || project.hasProperty("debug")
    
    buildConfigField("Boolean", "IS_DEBUG", isDebug.toString())
    buildConfigField("String", "BASE_URL_SERVICE", "\"$urlPro\"")
    buildConfigField("String", "BASE_URL_SERVICE_DEV", "\"$urlDev\"")
}

kotlin {
    androidTarget {
        @OptIn(ExperimentalKotlinGradlePluginApi::class)
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_21)
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "Shared"
            isStatic = true
        }
    }

    sourceSets {
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.room.runtime)
            implementation(libs.room.ktx)
        }

        commonMain.dependencies {
            api(libs.koin.core)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.websockets)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)
            implementation(libs.kotlinx.datetime)
            implementation(libs.room.runtime)
            implementation(libs.sqlite.bundled)
            implementation(libs.datastore.preferences)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.turbine)
            implementation(libs.koin.test)
        }

        androidUnitTest.dependencies {
            implementation(libs.mockk)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

configure<com.android.build.api.dsl.LibraryExtension> {
    namespace = "com.pizzza.pizzzaapp.shared"
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    buildFeatures {
        buildConfig = false
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
    add("kspIosX64", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}
