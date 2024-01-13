// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.google.android.libraries.mapsplatform.secrets-gradle-plugin") version "2.0.1" apply false
    id("org.sonarqube") version "4.4.1.3373"
}
sonar {
    properties {
        property("sonar.projectKey", "FAYCAL-EL_dish-wish-frontend")
        property("sonar.organization", "faycal-el")
        property("sonar.host.url", "https://sonarcloud.io")
    }
}