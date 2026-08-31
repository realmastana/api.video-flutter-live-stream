group = "video.api.flutter.livestream"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    compileSdk = 37
    namespace = "video.api.flutter.livestream"

    defaultConfig {
        minSdk = 21
    }

    sourceSets {
        named("main") {
            java.srcDirs("src/main/kotlin")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    val kotlinVersion = "2.0.0"
    val streamPackVersion = "2.9.0"

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("io.github.thibaultbee:streampack:$streamPackVersion")
    implementation("io.github.thibaultbee:streampack-extension-rtmp:$streamPackVersion")
}

repositories {
    google()
    mavenCentral()
}
