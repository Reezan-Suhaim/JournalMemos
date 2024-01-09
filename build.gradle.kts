buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:8.2.0")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.8.21")
        classpath("com.google.gms:google-services:4.4.0")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.2.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.22" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
    id("com.android.library") version "8.2.1" apply false
}