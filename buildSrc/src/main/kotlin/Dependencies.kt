object Versions {

    // Androidx
    const val kotlin = "1.5.0"
    const val buildToolsVersion = "4.2"
    const val appCompat = "1.4.0-alpha01"
    const val constraintLayout = "2.1.0-beta02"
    const val ktx = " 1.6.0-beta01"
    const val material = "1.4.0-alpha02"

    // Test Libraries
    const val junit4 = "4.13.1"
    const val testRunner = " 1.4.0-beta01"
    const val espresso = "3.4.0-beta01"
    const val annotation = "1.3.0-alpha01"

    // Gradle Plugins
    const val ktlint = "10.0.0"
    const val detekt = "1.17.1"
    const val spotless = "5.12.5"
    const val dokka = "1.4.32"
    const val gradleVersionsPlugin = "0.38.0"
    const val jacoco = "0.8.4"

    // Stream
    const val stream = "4.9.2"

    // Fragment
    const val fragment = "1.4.0-alpha01"
}

object BuildPlugins {
    const val androidLibrary = "com.android.library"
    const val ktlintPlugin = "org.jlleitschuh.gradle.ktlint"
    const val detektPlugin = "io.gitlab.arturbosch.detekt"
    const val spotlessPlugin = "com.diffplug.spotless"
    const val dokkaPlugin = "org.jetbrains.dokka"
    const val androidApplication = "com.android.application"
    const val kotlinAndroid = "org.jetbrains.kotlin.android"
    const val kotlinParcelizePlugin = "org.jetbrains.kotlin.plugin.parcelize"
    const val gradleVersionsPlugin = "com.github.ben-manes.versions"
    const val jacocoAndroid = "com.hiya.jacoco-android"
    const val kapt = "kotlin-kapt"
}

object Libraries {
    // Core Libs
    const val kotlinStandardLibrary = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
    const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompat}"
    const val constraintLayout =
        "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
    const val ktxCore = "androidx.core:core-ktx:${Versions.ktx}"
    const val materialComponents = "com.google.android.material:material:${Versions.material}"

    // Stream
    const val stream = "io.getstream:stream-chat-android-ui-components:${Versions.stream}"

    // Fragment
    const val fragment = "androidx.fragment:fragment-ktx:${Versions.fragment}"
}

object TestLibraries {
    const val junit4 = "junit:junit:${Versions.junit4}"
    const val testRunner = "androidx.test:runner:${Versions.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${Versions.espresso}"
    const val annotation = "androidx.annotation:annotation:${Versions.annotation}"
}


object AndroidSdk {
    const val minSdkVersion = 21
    const val compileSdkVersion = 30
    const val targetSdkVersion = compileSdkVersion
    const val versionCode = 1
    const val versionName = "1.0"
}