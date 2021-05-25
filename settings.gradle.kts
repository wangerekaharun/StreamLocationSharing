pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
        maven("https://dl.bintray.com/kotlin/kotlin-eap")
    }

    plugins {
        id("com.android.application") version "4.2.1"
        id("org.jetbrains.kotlin.android") version "1.5.0"
        id("com.android.library") version "4.2.0"
        id("com.google.firebase.crashlytics") version "2.1.0"
        id("com.google.gms.google-services") version "4.3.3"
    }

    resolutionStrategy {
        eachPlugin {
            when (requested.id.id) {
                "com.android.application", "com.android.library" -> useModule("com.android.tools.build:gradle:4.2.1")
                "com.google.firebase.crashlytics" -> useModule("com.google.firebase:firebase-crashlytics-gradle:2.1.0")
                "com.google.gms.google-services" -> useModule("com.google.gms:google-services:4.3.3")
            }
        }
    }
}
include("app")
rootProject.name = "StreamLocationSharing"