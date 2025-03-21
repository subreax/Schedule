import org.gradle.kotlin.dsl.ksp
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties

fun loadKeystoreProperties(path: String): Properties? {
    return try {
        val propsFile = rootProject.file(path)
        val props = Properties()
        props.load(FileInputStream(propsFile))
        props
    } catch (ex: Exception) {
        null
    }
}

fun getLatestGitHash(): String {
    val out = ByteArrayOutputStream()
    exec {
        commandLine("git", "rev-parse", "--short", "HEAD")
        standardOutput = out
    }
    return out.toString().trim()
}

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin)
    alias(libs.plugins.jetbrains.compose.compiler)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
    alias(libs.plugins.google.services)
    alias(libs.plugins.google.firebase.crashlytics)
}

room {
    schemaDirectory("$projectDir/schemas")
}

android {
    namespace = "com.subreax.schedule"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.subreax.schedule"
        minSdk = 26
        targetSdk = 35
        versionCode = 21
        versionName = "1.3.0"

        buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")
        buildConfigField("String", "GIT_HASH", "\"${getLatestGitHash()}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            loadKeystoreProperties("keystore.properties")?.also { props ->
                storeFile = file(props.getProperty("storeFile"))
                storePassword = props.getProperty("storePassword")
                keyAlias = props.getProperty("keyAlias")
                keyPassword = props.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        debug {
            resValue("string", "app_name", "Расписание ТулГУ (debug)")
        }
        release {
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
                "retrofit2.pro"
            )

            signingConfig = signingConfigs.getByName("release")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    sourceSets {
        // Adds exported schema location as test app assets
        getByName("androidTest").assets.srcDir("$projectDir/schemas")
    }

    flavorDimensions.add("appSource")
    productFlavors {
        create("default") {
            dimension = "appSource"
        }
        create("github") {
            dimension = "appSource"
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core.splashscreen)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.datastore.preferences)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.reorderable)

    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    implementation(libs.retrofit2)
    implementation(libs.retrofit2.gson)

    implementation(libs.koin.core)
    implementation(libs.koin.android)
    implementation(libs.koin.compose.viewmodel)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    implementation(libs.androidx.room.testing)

    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)

    debugImplementation(libs.leakcanary)
}
