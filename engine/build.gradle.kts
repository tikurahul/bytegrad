plugins {
    alias(libs.plugins.kotlinMultiplatform)
}

kotlin {
    jvm()
    sourceSets {
        commonMain.dependencies {

        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
        jvmMain.dependencies {
            // Library that can build GraphViz like graphs
            implementation("org.graphper:graph-support-core:1.5.2")
        }
    }
}
