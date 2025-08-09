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
        maven("https://jitpack.io")
        maven("https://maven.google.com")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()

        maven("https://jitpack.io")
        maven("https://maven.google.com")
        maven("https://oss.jfrog.org/artifactory/oss-snapshot-local")
    }
}

rootProject.name = "Iksica I Studomat"
include(":app")
 