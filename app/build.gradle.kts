plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.android")
}

val okhttp_version = "4.11.0"
val kotlin_version = "1.9.0"

dependencies {
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-analytics-ktx")

    implementation("androidx.multidex:multidex:2.0.1")

    implementation("com.google.android.gms:play-services-maps:19.0.0")
    implementation("com.google.android.gms:play-services-location:21.3.0")
    implementation("com.google.android.gms:play-services-analytics:18.1.1")

    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.preference:preference:1.2.1")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.android.material:material:1.12.0")

    implementation("com.google.firebase:firebase-crashlytics-ktx")

    implementation("com.github.gabrielemariotti.changeloglib:changelog:2.0.0")

    implementation("com.o3dr.android:dronekit-android:3.0.2")

    implementation("me.grantland:autofittextview:0.2.1")
    implementation(name = "shimmer-android-release", ext = "aar")
    implementation(name = "libuvccamera-release", ext = "aar")
    implementation(name = "sliding-up-panel-3.3.0", ext = "aar")

    implementation(files("libs/droneapi-java-0.3-SNAPSHOT.jar"))
    implementation(files("libs/protobuf-java-2.5.0.jar"))
    implementation(files("libs/jeromq-0.3.4.jar"))
    implementation(files("libs/sius-0.3.1-SNAPSHOT.jar"))
    implementation(files("libs/BaiduLBS_Android.jar"))
    implementation(files("libs/dronekit-android-3.0.2.jar"))

    implementation("com.squareup.okhttp3:okhttp:$okhttp_version")
    implementation("com.squareup.okhttp3:okhttp-urlconnection:$okhttp_version")

    implementation("com.jakewharton.timber:timber:3.1.0")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlin_version")
    implementation("com.crashlytics.sdk.android:crashlytics:2.5.5@aar") {
        isTransitive = true
    }

    implementation("com.github.lecho:hellocharts-library:1.5.5@aar")

    debugImplementation("com.squareup.leakcanary:leakcanary-android:1.4-beta2")
    releaseImplementation("com.squareup.leakcanary:leakcanary-android-no-op:1.4-beta2")
    testImplementation("com.squareup.leakcanary:leakcanary-android-no-op:1.4-beta2")
}

val versionPrefix = "Tower-v"

val versionMajor = 4
val versionMinor = 0
val versionPatch = 0
val versionBuild = 10

val logLevelVerbose = 2
val logLevelDebug = 3
val logLevelInfo = 4
val logLevelWarn = 5
val logLevelError = 6
val logLevelAssert = 7

android {
    namespace = "org.droidplanner.android"
    compileSdk = 34

    buildFeatures {
        buildConfig = true
    }
}

defaultConfig {
    applicationId = "com.example.tower2"  //"org.droidplanner.android"
    minSdk = 24
    targetSdk = 34
    versionCode = computeVersionCode(versionMajor, versionMinor, versionPatch, versionBuild)
    versionName = generateVersionName(versionPrefix, versionMajor, versionMinor, versionPatch)

    buildConfigField("boolean", "WRITE_LOG_FILE", "true")
    buildConfigField("int", "LOG_FILE_LEVEL", "$logLevelDebug")
    buildConfigField("boolean", "ENABLE_CRASHLYTICS", "false")

    manifestPlaceholders = [
        fabricApiKey: getFabricApiKey()
    ]

    multiDexEnabled = true
}

compileOptions {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

sourceSets {
    main {
        manifest.srcFile = "AndroidManifest.xml"
        java.srcDirs = listOf("src")
        resources.srcDirs = listOf("src")
        aidl.srcDirs = listOf("src")
        renderscript.srcDirs = listOf("src")
        res.srcDirs = listOf("res")
        assets.srcDirs = listOf("assets")

        jniLibs.srcDir = "jni/libs"
    }

    debug.setRoot("build-types/debug")
    release.setRoot("build-types/release")
}

signingConfigs {
    create("release") {
        storeFile = getAppKeystoreFile()
        storePassword = getAppKeystorePassword()
        keyAlias = getAppKey()
        keyPassword = getAppKeyPassword()
    }
}

flavorDimensions("version")

android {
    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            output.outputFileName = "tower-${variant.name}-${variant.versionCode}.apk"
        }
    }
}

androidComponents {
    onVariants { variant ->
        val flavors = variant.productFlavors
        if (!flavors.isEmpty()) {
            val flavorName = flavors[0].name
            val buildTypeName = variant.buildType.name

            val ignoreVariant = when (buildTypeName) {
                "release" -> flavorName !in listOf("prod", "beta")
                "debug" -> flavorName != "dev"
                else -> true
            }

            variant.enabled = !ignoreVariant
        }
    }
}

productFlavors {
    create("dev") {
        applicationIdSuffix = ".debug"
        resValue("string", "app_title", "Tower Dev")
        resValue("string", "baidu_api_key", "")
        versionName = generateVersionName(versionPrefix, versionMajor, versionMinor, versionPatch, "debug.$versionBuild")
    }

    create("beta") {
        applicationIdSuffix = ".beta"
        resValue("string", "app_title", "Tower Beta")
        resValue("string", "baidu_api_key", "4x7A3lUHEmzFxosafDa4bLP7yinmEnPh")
        versionName = generateVersionName(versionPrefix, versionMajor, versionMinor, versionPatch, "beta.$versionBuild")
    }

    create("prod") {
        resValue("string", "app_title", "Tower")
        resValue("string", "baidu_api_key", "41mPy2URFjOL61GzyWk8U5oLhhwSYGWe")
        buildConfigField("boolean", "WRITE_LOG_FILE", "false")
    }
}

buildTypes {
    getByName("debug") {
        buildConfigField("boolean", "ENABLE_CRASHLYTICS", "false")
    }

    getByName("release") {
        signingConfig = signingConfigs.getByName("release")
        buildConfigField("boolean", "ENABLE_CRASHLYTICS", "${hasFabricApiKey()}")
    }
}

lint {
    abortOnError = false
}

fun getGitVersion(): String {
    val cmd = "git describe --tag"
    return try {
        cmd.execute().text.trim()
    } catch (e: IOException) {
        "please update version name manually"
    }
}

fun getAppKeystoreFile(): File? {
    val filePath = System.getProperty("ORG_DROIDPLANNER_ANDROID_KEYSTORE")
    return filePath?.let { file(it) }
}

fun getAppKeystorePassword(): String {
    return System.getProperty("ORG_DROIDPLANNER_ANDROID_KEYSTORE_PWD