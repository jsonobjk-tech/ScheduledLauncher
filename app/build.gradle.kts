plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.example.scheduledlauncher"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.scheduledlauncher"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
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

    // 禁用 lint 报错阻止构建
    lint {
        abortOnError = false
        checkReleaseBuilds = false
    }

    // 打包配置：排除重复的元文件
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    // Compose Bill of Materials — 统一管理所有 Compose 库版本
    val composeBom = platform("androidx.compose:compose-bom:2023.10.01")
    implementation(composeBom)

    // Compose UI 核心
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // Activity + Compose 集成
    implementation("androidx.activity:activity-compose:1.8.1")

    // ViewModel + Compose 集成
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.2")

    // AndroidX 核心
    implementation("androidx.core:core-ktx:1.12.0")

    // 调试工具
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
}
