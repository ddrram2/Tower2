// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    val kotlin_version = "1.8.20" // Frissített Kotlin verzió
    val play_services_version = "9.4.0"
    // A nem használt változókat eltávolítottam

    repositories {
        google() // Google Maven repository hozzáadva
        mavenCentral() // jcenter() eltávolítva
        maven { url = uri("https://maven.fabric.io/public") }
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.11.1")
        classpath("com.google.gms:google-services:$play_services_version")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")

        //Dexcount gradle plugin
        classpath("com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.4.4")
    }
}

fun getMavenUsername(): String {
    return System.getProperty("COM_O3DR_MAVEN_USERNAME", "")
}

fun getMavenApiKey(): String {
    return System.getProperty("COM_O3DR_MAVEN_APIKEY", "")
}

fun getMavenRepoUrl(): String {
    return System.getProperty("COM_O3DR_MAVEN_REPO_URL", "https://dl.bintray.com/3d-robotics/maven")
}

fun computeVersionCode(versionMajor: Int, versionMinor: Int, versionPatch: Int, versionBuild: Int = 0): Int {
    return versionMajor * 100000 + versionMinor * 1000 + versionPatch * 100 + versionBuild
}

fun generateVersionName(
    versionPrefix: String,
    versionMajor: Int,
    versionMinor: Int,
    versionPatch: Int,
    versionSuffix: String = ""
): String {
    var versionName = "${versionPrefix}${versionMajor}.${versionMinor}.${versionPatch}"

    if (versionSuffix.isNotEmpty() && versionSuffix != "release") {
        versionName += "-${versionSuffix}"
    }

    return versionName
}


allprojects {
    repositories {
        google() // Google Maven repository
        mavenCentral() // jcenter() eltávolítva
        maven { url = uri("https://maven.fabric.io/public") }
        // Az új repository-k hozzáadása:
        maven { url = uri("https://dl.bintray.com/3d-robotics/maven") } // jcenter() eltávolítva

        maven {
            url = uri(getMavenRepoUrl())
            credentials {
                username = getMavenUsername()
                password = getMavenApiKey()
            }
        }

    }
}
