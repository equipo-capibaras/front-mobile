plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.ksp)
    alias(libs.plugins.jacoco)
}

android {
    namespace = "io.capibaras.abcall"
    compileSdk = 35

    defaultConfig {
        applicationId = "io.capibaras.abcall"
        minSdk = 26
        targetSdk = 35
        versionCode = 6
        versionName = "1.6.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField("String", "BASE_URL", "\"https://capibaras.io/api/v1/\"")
        }
        debug {
            enableUnitTestCoverage = true
            buildConfigField("String", "BASE_URL", "\"https://dev.capibaras.io/api/v1/\"")
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
        buildConfig = true
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

tasks.withType(Test::class) {
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
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
        exclude("**/App.class")
        exclude("**/data/database/ABCallDB*")
        exclude("**/di/**")
        exclude("**/navigation/**")
        exclude("**/ui/*MainActivity*")
        exclude("**/ui/components/**")
        exclude("**/ui/navigation/**")
        exclude("**/ui/theme/**")
        exclude("**/ui/views/**")
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
    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)
    implementation(libs.gson)
    implementation(libs.security.crypto)
    implementation(libs.core.splashscreen)
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.navigation.testing)
    testImplementation(project(":app"))

    ksp(libs.room.compiler)

    testImplementation(libs.junit)
    testImplementation(libs.roboelectric)
    testImplementation(libs.androidx.test.core.ktx)
    testImplementation(libs.koin.test)
    testImplementation(libs.koin.test.junit4)
    testImplementation(libs.mockk)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.kotlin.faker)

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
        property("sonar.projectVersion", "1.6.0")

        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.organization", "equipo-capibaras")
        property("sonar.projectKey", "equipo-capibaras_front-mobile")
        property("sonar.sources", "src/main/")
        property("sonar.gradle.skipCompile", "true")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "build/reports/jacoco/codeCoverageReportDebug/codeCoverageReportDebug.xml"
        )
        property(
            "sonar.coverage.exclusions",
            "**/App.kt, **/di/**, **/navigation/**, **/data/database/ABCallDB.kt, **/ui/components/**, **/ui/navigation/**, **/ui/theme/**, **/ui/views/**, **/ui/MainActivity.kt"
        )
    }
}
