plugins {
    id(BuildPlugins.androidApplication)
    id(BuildPlugins.kotlinAndroid)
    id(BuildPlugins.kotlinParcelizePlugin)
    id(BuildPlugins.ktlintPlugin)
    id(BuildPlugins.kapt)
   // id(BuildPlugins.secretsPlugin) version  Versions.secretsPlugin
}

android {

    compileSdkVersion(AndroidSdk.compileSdkVersion)
    defaultConfig {
        applicationId = "io.stream.locationsharing"
        minSdkVersion(AndroidSdk.minSdkVersion)
        targetSdkVersion(AndroidSdk.targetSdkVersion)
        versionCode = AndroidSdk.versionCode
        versionName = AndroidSdk.versionName
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    testOptions {
        animationsDisabled = true
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    val TOKEN: String? =
        com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir)
            .getProperty("TOKEN")

    val googleMapsKey: String? =
        com.android.build.gradle.internal.cxx.configure.gradleLocalProperties(rootDir)
            .getProperty("googleMapsKey")

    buildTypes {
        this.forEach {
            it.buildConfigField("String", "TOKEN", TOKEN.toString())
        }

        this.forEach {
            it.resValue("string", "googleMapsKey", googleMapsKey.toString())
        }

        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    dependencies {
        implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
        implementation(Libraries.kotlinStandardLibrary)
        implementation(Libraries.appCompat)
        implementation(Libraries.ktxCore)
        implementation(Libraries.constraintLayout)
        implementation(Libraries.materialComponents)

        // Stream
        implementation(Libraries.stream)

        // Fragment
        implementation(Libraries.fragment)

        // Maps
        implementation(Libraries.maps)
        implementation(Libraries.locationServices)

        // Coroutines
        implementation(Libraries.coroutines)
        implementation(Libraries.coroutinesAndroid)

        // Lifecycle
        implementation(Libraries.lifecycle)

        androidTestImplementation(TestLibraries.testRunner)
        androidTestImplementation(TestLibraries.espresso)
        androidTestImplementation(TestLibraries.annotation)
        testImplementation(TestLibraries.junit4)
    }
}