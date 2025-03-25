

// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    repositories {
        google()
    }
    dependencies {

        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.8.4")


    }
}
plugins {

    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id ("androidx.navigation.safeargs.kotlin") version("2.8.4") apply false
}