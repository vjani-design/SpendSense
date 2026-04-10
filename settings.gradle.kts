pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }

    plugins {
        id("com.google.gms.google-services") version "4.4.1" // Firebase plugin
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()       // Required for Firebase & AndroidX
        mavenCentral() // Required for Kotlin libraries
    }
}

rootProject.name = "SpendSense"
include(":app")