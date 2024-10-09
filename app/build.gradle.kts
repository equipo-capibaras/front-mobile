plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.sonarqube)
}

android {
    namespace = "io.capibaras.abcall"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.capibaras.abcall"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        debug {
            enableUnitTestCoverage = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    kotlinOptions {
        jvmTarget = "21"
    }
    buildFeatures {
        compose = true
    }

    packaging {
        resources.excludes.addAll(
            listOf(
                "META-INF/LICENSE.md",
                "META-INF/LICENSE-notice.md"
            )
        )
    }
}

task<JacocoReport>("codeCoverageReportDebug") {
    group = "Verification"
    description = "Generate Jacoco coverage report for the debug build."

    reports {
        html.required.set(true)
        xml.required.set(true)
        csv.required.set(false)
    }

    sourceDirectories.setFrom("${project.projectDir}/src/main/java")
    classDirectories.setFrom(fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude("**/ui/**")         // Excluir todas las clases relacionadas con la UI
        exclude("**/di/**")         // Excluir inyección de dependencias
        exclude("**/navigation/**") // Excluir las clases de navegación
        include("**/viewmodels/**") // Incluir solo los ViewModel
    })
    executionData.setFrom("${project.layout.buildDirectory.get()}/outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    testImplementation(libs.junit)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockk)


    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    androidTestImplementation(libs.mockk.android)


    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    debugImplementation(libs.mockk.agent)
}

sonar {
    properties {
        property("sonar.projectName", "front-mobile")
        property("sonar.projectVersion", "1.0")

        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "equipo-capibaras")
        property("sonar.projectKey", "equipo-capibaras_front-mobile")
        property("sonar.gradle.skipCompile", "equipo-capibaras_front-mobile")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacoco/codeCoverageReportDebug/codeCoverageReportDebug.xml"
        )
        property("sonar.coverage.exclusions", "**/di/**, **/navigation/**, **/ui/**")
    }
}
