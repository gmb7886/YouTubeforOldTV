pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        // Necessário para obter as versões do GeckoView
        maven {
            url = uri("https://maven.mozilla.org/maven2/")
        }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Repositório do Mozilla para GeckoView (Stable, Beta, Nightly)
        maven { url = uri("https://maven.mozilla.org/maven2/") }
    }
}

rootProject.name = "YouTube"
include(":app")