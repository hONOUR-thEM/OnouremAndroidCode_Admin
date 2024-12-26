// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.6.1" apply false
    id("org.jetbrains.kotlin.android") version "1.9.21" apply false
    // Make sure that you have the Google services Gradle plugin dependency
    id("com.google.gms.google-services") version "4.4.0" apply false

    // Add the dependency for the Crashlytics Gradle plugin
    id("com.google.firebase.crashlytics") version "2.9.9" apply false

    id("androidx.navigation.safeargs") version "2.7.5" apply false
//    id("com.google.devtools.ksp") version "1.9.0-1.0.13" apply false

}

//buildscript {
//    ext.kotlin_version = '1.8.10'
//    repositories {
//        google()
//        mavenCentral()
//        jcenter()
//    }
//    dependencies {
//        classpath 'com.android.tools.build:gradle:7.4.2'
//        classpath 'androidx.navigation:navigation-safe-args-gradle-plugin:2.5.0-alpha02'
//        classpath 'com.google.gms:google-services:4.3.10'
//        classpath 'com.google.firebase:firebase-crashlytics-gradle:2.6.0'
//        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
//        classpath "org.jetbrains.kotlin:kotlin-noarg:$kotlin_version"
//
//    }
//}
