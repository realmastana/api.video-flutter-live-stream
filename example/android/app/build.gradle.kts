plugins {
    id("com.android.application")
    id("kotlin-android")
    id("dev.flutter.flutter-gradle-plugin")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

var flutterVersionCode = localProperties.getProperty("flutter.versionCode", "1")
var flutterVersionName = localProperties.getProperty("flutter.versionName", "1.0")

android {
    compileSdk = 37
    namespace = "video.api.flutter.livestream.example"

    sourceSets {
        named("main") {
            java.directories.add(file("src/main/kotlin"))
        }
    }

    defaultConfig {
        applicationId = "video.api.flutter.livestream.example"
        minSdk = flutter.minSdkVersion
        targetSdk = 37
        versionCode = flutterVersionCode.toInt()
        versionName = flutterVersionName
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

flutter {
    source = "../.."
}

dependencies {
}
