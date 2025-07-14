plugins {
  kotlin("multiplatform") version "2.0.21"
  id("com.android.application") version "8.10.1"
  id("org.jetbrains.compose") version "1.8.1"
  id("org.jetbrains.kotlin.plugin.compose") version "2.0.21"
  id("com.diffplug.spotless") version "6.25.0"
}

kotlin {
  androidTarget {
    compilerOptions {
      jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
  }

  sourceSets {
    val commonMain by getting {
      dependencies {
        implementation(compose.runtime)
        implementation(compose.foundation)
        implementation(compose.material3)
        implementation(compose.ui)
      }
    }

    val commonTest by getting {
      dependencies {
        implementation(kotlin("test"))
      }
    }

    val androidMain by getting {
      dependencies {
        implementation("androidx.compose.ui:ui-tooling-preview:1.7.5")
        implementation("androidx.activity:activity-compose:1.9.3")
        implementation("com.google.android.material:material:1.12.0")
      }
    }
  }
}

android {
  namespace = "app.pmsoft.fatrobin"
  compileSdk = 35

  sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
  sourceSets["main"].res.srcDirs("src/androidMain/res")
  sourceSets["main"].resources.srcDirs("src/commonMain/resources")

  defaultConfig {
    applicationId = "app.pmsoft.fatrobin"
    minSdk = 24
    targetSdk = 35
    versionCode = 1
    versionName = "1.0"
  }

  packaging {
    resources {
      excludes += "/META-INF/{AL2.0,LGPL2.1}"
    }
  }

  buildTypes {
    getByName("release") {
      isMinifyEnabled = false
    }
  }

  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }

  buildFeatures {
    compose = true
  }

  composeOptions {
    kotlinCompilerExtensionVersion = "1.5.15"
  }
}

spotless {
  kotlin {
    target("**/*.kt")
    targetExclude("**/build/**/*.kt")
    ktlint("1.0.1").editorConfigOverride(
      mapOf(
        "indent_size" to "2",
        "continuation_indent_size" to "2",
        "ktlint_standard_function-naming" to "disabled",
      ),
    )
    trimTrailingWhitespace()
    endWithNewline()
  }
  kotlinGradle {
    target("*.gradle.kts")
    ktlint("1.0.1")
    trimTrailingWhitespace()
    endWithNewline()
  }
}
