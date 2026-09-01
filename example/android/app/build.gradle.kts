import java.util.Properties
import com.android.build.api.dsl.ApplicationExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.android.application")
    id("dev.flutter.flutter-gradle-plugin")
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { stream ->
        localProperties.load(stream)
    }
}

val flutterVersionCode = localProperties.getProperty("flutter.versionCode", "1")
val flutterVersionName = localProperties.getProperty("flutter.versionName", "1.0")

configure<ApplicationExtension> {
    compileSdk = 37
    namespace = "video.api.flutter.livestream.example"

    sourceSets {
        named("main") {
            java.directories.add("src/main/kotlin")
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
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                // Checked-in copy of AGP's default `proguard-android-optimize.txt`.
                // Using `getDefaultProguardFile(...)` here fails on AGP 9.1 when the
                // Flutter template redirects the build directory (`layout.buildDirectory`
                // set in the root build.gradle.kts): the proguard file is generated under
                // the redirected build dir while R8 reads the non-redirected path.
                "proguard-android-optimize.txt",
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_17
    }
}

// Workaround for AGP 9.1: with the Flutter template's build-directory
// redirection (see the root build.gradle.kts), `extractProguardFiles` writes
// the default proguard files under the redirected build directory
// (`../build/app/...`) while the R8 task reads them from the project build
// directory (`app/build/...`), failing with
// "Supplied proguard configuration does not exist".
tasks.matching { it.name == "minifyReleaseWithR8" }.configureEach {
    dependsOn("extractProguardFiles")
    doFirst {
        val sourceDir = layout.buildDirectory
            .dir("intermediates/default_proguard_files/global").get().asFile
        val targetDir = project.layout.projectDirectory
            .dir("build/intermediates/default_proguard_files/global").asFile
        sourceDir.listFiles()?.forEach { sourceFile ->
            val targetFile = File(targetDir, sourceFile.name)
            if (!targetFile.exists()) {
                targetDir.mkdirs()
                sourceFile.copyTo(targetFile)
            }
        }
    }
}

flutter {
    source = "../.."
}

dependencies {
}
