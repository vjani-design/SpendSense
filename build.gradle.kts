buildscript {
    dependencies {
        classpath("com.android.tools.build:gradle:8.3.0")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
        // Google Services plugin is declared in settings.gradle.kts
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}