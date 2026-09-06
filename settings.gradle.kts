rootProject.name = "PizzzaApp"

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

include(":composeApp")
include(":shared")

// Core Modules
include(":core:ui")
include(":core:navigation")

// Feature Modules
include(":feature:auth")
include(":feature:home")
include(":feature:cart")
include(":feature:orders")
include(":feature:monitoring")
