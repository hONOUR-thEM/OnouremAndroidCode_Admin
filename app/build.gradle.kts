plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("androidx.navigation.safeargs")
    id("kotlin-android")
    id("kotlin-kapt")
    id("kotlin-parcelize")
//    id("com.google.devtools.ksp")
//    id("kotlin-noarg")
}

//
//noArg {
//    annotation("com.onourem.android.activity.ui.utils.NoArg")
//    invokeInitializers = true
//}

android {
    namespace = "com.onourem.android.activity"
    compileSdk = 34

//    signingConfigs {
//        config {
//            keyAlias keystoreProperties['keyAlias']
//            keyPassword keystoreProperties['keyPassword']
//            storeFile file(keystoreProperties['storeFile'])
//            storePassword keystoreProperties['storePassword']
//        }
//    }

    signingConfigs {
        getByName("debug") {
            storeFile = file("../onouremkey.keystore")
            storePassword = "onouremkey"
            keyAlias = "onouremkey"
            keyPassword = "onouremkey"
        }
        create("release") {
            storeFile = file("../onouremkey.keystore")
            storePassword = "onouremkey"
            keyAlias = "onouremkey"
            keyPassword = "onouremkey"
        }
    }

    defaultConfig {
        applicationId = "com.onourem.android.activity"
        minSdk = 23
        targetSdk = 34
        versionCode = 270
        versionName = "2.5.70-Admin"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true
        buildFeatures.dataBinding
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {

        getByName("release") {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("release")
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }
        }

        getByName("debug") {
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
            ndk {
                debugSymbolLevel = "SYMBOL_TABLE"
            }

//            firebaseCrashlytics.mappingFileUploadEnabled = true
//            firebaseCrashlytics.nativeSymbolUploadEnabled = true
        }

    }

//    flavorDimensions.add("version")
    flavorDimensions += "version"
    productFlavors {
//        create("Prod") {
//            dimension = "version"
//            buildConfigField("boolean", "IS_ADMIN", "false")
//        }
//
//        create("Dev") {
//            dimension = "version"
//            applicationIdSuffix = ".dev"
////            applicationIdSuffix ".dev"
//            buildConfigField("boolean", "IS_ADMIN", "false")
//            versionName = "${android.defaultConfig.versionName}.users"
//        }

        create("AdminDev") {
            applicationIdSuffix = ".admin.dev"
            buildConfigField("boolean", "IS_ADMIN", "true")
            versionName = "${android.defaultConfig.versionName}.admin"
            dimension = "version"
        }

        create("AdminProd") {
            dimension = "version"
            applicationIdSuffix = ".admin"
            versionName = "${android.defaultConfig.versionName}.admin"
            buildConfigField("boolean", "IS_ADMIN", "true")
        }
    }

    buildFeatures {
        //noinspection DataBindingWithoutKapt
        dataBinding = true
        viewBinding = true
        buildConfig = true
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources.excludes.add("META-INF/*.kotlin_module")
    }

    lint {
        abortOnError = true
        checkReleaseBuilds = false
    }
}

dependencies {

    val appCompatVersion = "1.6.1"
    val constraintLayoutVersion = "2.1.4"
    val glideVersion = "4.16.0"
    val lifeCycleVersion = "2.2.0"
    val materialVersion = "1.4.0"
    val junitVersion = "4.13.2"
    val daggerVersion = "2.48.1"
    val navigationUIVersion = "2.7.4"
    val navigationFragmentVersion = "2.7.4"
    val retrofitVersion = "2.9.0"
    val okhttp3Version = "4.9.0"

    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar", "*.aar"))))
    implementation("androidx.appcompat:appcompat:$appCompatVersion")
    implementation("androidx.constraintlayout:constraintlayout:$constraintLayoutVersion")
    implementation("com.google.android.material:material:$materialVersion")
    implementation("androidx.navigation:navigation-fragment-ktx:$navigationFragmentVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navigationUIVersion")
    implementation("androidx.lifecycle:lifecycle-extensions:$lifeCycleVersion")
    implementation("androidx.multidex:multidex:2.0.1")

//    coreLibraryDesugaring "com.android.tools:desugar_jdk_libs:1.1.5"
//    implementation "com.github.kizitonwose:CalendarView:1.1.0"
//    implementation "com.wdullaer:materialdatetimepicker:4.2.3"
//    implementation "com.github.dhaval2404:imagepicker:1.7.4"
//    implementation "com.github.yalantis:ucrop:2.2.6"

    //Dagger Dependency
//    implementation "com.google.dagger:dagger:$daggerVersion"
//    annotationProcessor "com.google.dagger:dagger-compiler:$daggerVersion"
//    implementation "com.google.dagger:dagger-android-support:$daggerVersion"
    // if you use the support libraries
//    annotationProcessor "com.google.dagger:dagger-android-processor:$daggerVersion"

//    kapt "com.google.dagger:dagger-compiler:$daggerVersion"
//    kapt "com.google.dagger:dagger-android-processor:$daggerVersion"


    implementation("com.google.dagger:dagger:$daggerVersion")
    kapt("com.google.dagger:dagger-compiler:$daggerVersion")
    implementation("com.google.dagger:dagger-android-support:$daggerVersion")
    // if you use the support libraries
    kapt("com.google.dagger:dagger-android-processor:$daggerVersion")


    // https://mvnrepository.com/artifact/com.google.code.gson/gson
//    implementation group "com.google.code.gson", name: "gson", version: "2.8.6"
    implementation("com.google.code.gson:gson:2.10.1")

    // https://mvnrepository.com/artifact/com.github.bumptech.glide/glide
//    implementation group: "com.github.bumptech.glide", name: "glide", version: "$glideVersion"
    implementation("com.github.bumptech.glide:glide:$glideVersion")
    implementation("com.github.bumptech.glide:okhttp3-integration:$glideVersion")


//    kapt("android.arch.lifecycle:compiler:2.6.2")
    kapt("com.github.bumptech.glide:compiler:4.14.2")

    implementation("com.squareup.retrofit2:retrofit:$retrofitVersion")
    implementation("com.squareup.retrofit2:converter-gson:$retrofitVersion")
    implementation("com.squareup.okhttp3:logging-interceptor:$okhttp3Version")

    testImplementation("junit:junit:$junitVersion")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("com.facebook.android:facebook-login:16.2.0")

    //chart lib
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.makeramen:roundedimageview:2.3.0")


    implementation("com.google.firebase:firebase-invites:17.0.0")
    //implementation "com.google.firebase:firebase-core:17.4.2"
    implementation("com.google.firebase:firebase-messaging:23.3.1")
    // Add the Firebase Crashlytics SDK
    implementation("com.google.firebase:firebase-crashlytics:18.5.1")

    // Recommended: Add the Google Analytics SDK
    implementation("com.google.firebase:firebase-analytics:21.4.0")

    // Add the Firebase Crashlytics NDK dependency
    implementation("com.google.firebase:firebase-crashlytics-ndk:18.5.1")

    //video
//    implementation("com.github.HamidrezaAmz:MagicalExoPlayer:2.0.7")

    val roomVersion = "2.6.0" // check latest version from docs

    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")

//    implementation "androidx.room:room-runtime:2.3.0"
//    annotationProcessor "androidx.room:room-compiler:2.3.0"

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation("com.google.android.play:core:1.10.3")
    implementation("com.github.chrisbanes:PhotoView:2.3.0")
    implementation("com.mindorks.android:prdownloader:0.6.0")
    implementation("com.arthenica:mobile-ffmpeg-full:4.4")
    //implementation ("com.github.SimformSolutionsPvtLtd:SSffmpegVideoOperation:1.0.6"
    implementation("com.daimajia.easing:library:2.4@aar")
    implementation("com.daimajia.androidanimations:library:2.4@aar")
    implementation("com.jakewharton.rxbinding4:rxbinding:4.0.0")
    implementation("com.github.squti:Android-Wave-Recorder:1.7.0")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta02")

//    implementation("com.google.android.exoplayer:extension-ima:2.19.1")
//    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

//    implementation ("com.github.yalantis:ucrop:2.2.6"
//    implementation ("com.theartofdev.edmodo:android-image-cropper:2.8.0"
    implementation("com.vanniktech:android-image-cropper:4.5.0")

//    implementation("com.google.android.exoplayer:extension-mediasession:2.19.1")
    implementation("androidx.work:work-runtime-ktx:2.8.1")

    implementation("com.sagar:coroutinespermission:2.0.3")
    implementation("org.mozilla:rhino:1.7.14")
//    implementation files("libs/YouTubeAndroidPlayerApi.jar")
//    implementation ("com.kizitonwose.calendar:view:1.1.0"
    implementation("com.googlecode.libphonenumber:libphonenumber:8.13.24")
    implementation("com.techyourchance:threadposter:1.0.1")
    implementation("com.github.vestrel00:contacts-android:0.2.4")
//    implementation("io.coil-kt:coil:2.2.2")

    implementation("com.android.volley:volley:1.2.1")
    implementation("org.jsoup:jsoup:1.14.3")

    implementation("com.pierfrancescosoffritti.androidyoutubeplayer:core:12.1.0")
    implementation("com.github.zhpanvip:viewpagerindicator:1.2.3")
//    implementation("com.github.zhpanvip:bannerviewpager:3.5.12")

    val mediaVersion = "1.2.0"

    implementation("androidx.media3:media3-exoplayer:$mediaVersion")
    implementation("androidx.media3:media3-ui:$mediaVersion")
    implementation("androidx.media3:media3-exoplayer-dash:$mediaVersion")

    //admin
    coreLibraryDesugaring("com.android.tools:desugar_jdk_libs:2.0.4")
    implementation("com.github.kizitonwose:CalendarView:1.0.4")
    implementation("com.wdullaer:materialdatetimepicker:4.2.3")
    implementation("com.github.dhaval2404:imagepicker:1.7.4")
    implementation("com.github.yalantis:ucrop:2.2.6")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.0")

}