pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven(url = "https://artifactory.tools.tapresearch.io/artifactory/tapresearch-android-sdk/")
    }
}

rootProject.name = "TapResearchTakeHome"
include(":app")
