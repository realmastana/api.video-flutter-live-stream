import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

group = "video.api.flutter.livestream"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
}

configure<LibraryExtension> {
    compileSdk = 37
    namespace = "video.api.flutter.livestream"

    defaultConfig {
        minSdk = 21
    }

    sourceSets {
        named("main") {
            java.srcDir("src/main/kotlin")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        jniLibs {
            useLegacyPackaging = false
        }
    }
}

// Configure the Kotlin compiler through the `kotlin` extension so that this
// plugin module builds both with the classic Kotlin Gradle Plugin (AGP < 9,
// or `android.builtInKotlin=false`) and with AGP 9 built-in Kotlin support.
project.extensions.configure(KotlinAndroidProjectExtension::class.java) {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

dependencies {
    val streamPackVersion = "3.2.0"

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")
    implementation("androidx.appcompat:appcompat:1.7.0")

    implementation("io.github.thibaultbee.streampack:streampack-core:$streamPackVersion")
    implementation("io.github.thibaultbee.streampack:streampack-rtmp:$streamPackVersion")
}

repositories {
    google()
    mavenCentral()
}
