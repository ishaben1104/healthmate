buildscript {
    dependencies {
        classpath("com.google.gms:google-services:4.4.0")
        classpath("com.android.tools.build:gradle:8.1.0")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.9")

        classpath ("com.android.tools.build:gradle:4.2.2") // Use the latest version
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.21") // Use the latest version

    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false

}