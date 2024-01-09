pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        jcenter()

    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "JournalMemos"
include(":app")
include(":mylibrary")
