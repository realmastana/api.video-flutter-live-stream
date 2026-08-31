pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_SETTINGS) // or FAIL_ON_PROJECT_REPOS
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "apivideo_live_stream"