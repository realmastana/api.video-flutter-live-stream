import com.android.build.api.dsl.LibraryExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "video.api.flutter.livestream"
version = "1.0-SNAPSHOT"

plugins {
    id("com.android.library")
    kotlin("android")
}

configure<LibraryExtension> {
    compileSdk = 37
    namespace = "video.api.flutter.livestream"

    defaultConfig {
        minSdk = 21
    }

    sourceSets {
        named("main") {
            java.directories.add("src/main/kotlin")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

dependencies {
    val kotlinVersion = "2.0.0"
    val streamPackVersion = "3.2.0"

    implementation("org.jetbrains.kotlin:kotlin-stdlib:$kotlinVersion")
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