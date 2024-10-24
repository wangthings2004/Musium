// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{
    dependencies {
        classpath("com.google.dagger:hilt-android-gradle-plugin:2.43.2")
        classpath("androidx.navigation.safeargs:androidx.navigation.safeargs.gradle.plugin:2.8.3")
    }
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.jetbrains.kotlin.android) apply false
    id ("com.google.dagger.hilt.android") version ("2.52") apply false
}