import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("signing")
}

android {
    namespace = "io.github.evitwilly.nicestarrating"
    compileSdk = 33

    defaultConfig {
        minSdk = 19
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
}

fun fetchLocalProperties(): Properties {
    val localProperties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use {
            localProperties.load(it)
        }
    }
    return localProperties
}

val localProperties = fetchLocalProperties()

afterEvaluate {
    publishing {
        publications {
            register<MavenPublication>("release") {
                val publishArtifactId = "nicestarratingview"

                from(components["release"])

                groupId = "io.github.evitwilly.nicestarratingview"
                artifactId = publishArtifactId
                version = "1.0.2"

                repositories {
                    maven {
                        val releasesUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                        val snapshotsUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots/")
                        url = if (version.toString().endsWith("SNAPSHOT")) snapshotsUrl else releasesUrl
                        credentials {
                            username = localProperties.getProperty("ossrhUsername")
                            password = localProperties.getProperty("ossrhPassword")
                        }
                    }
                }

                pom {
                    name.set(publishArtifactId)
                    description.set("A simple view to display the rating with stars")
                    url.set("https://github.com/evitwilly/NiceStarRatingView")

                    licenses {
                        license {
                            name.set("MIT License")
                            url.set("https://github.com/evitwilly/NiceStarRatingView/blob/develop/LICENSE.txt")
                        }
                    }

                    developers {
                        developer {
                            id.set("dmitry_tsyvtsyn")
                            name.set("Dmitry Tsyvtsyn")
                            email.set("dmitry.kind.2@gmail.com")
                        }
                    }

                    scm {
                        connection.set("scm:github.com/evitwilly/NiceStarRatingView.git")
                        developerConnection.set("scm:git:ssh://github.com/evitwilly/NiceStarRatingView.git")
                        url.set("https://github.com/evitwilly/NiceStarRatingView")
                    }
                }
            }
        }
    }
}

signing {
    useInMemoryPgpKeys(
        localProperties.getProperty("signing.keyId"),
        localProperties.getProperty("signing.key"),
        localProperties.getProperty("signing.password")
    )
    sign(publishing.publications)
}