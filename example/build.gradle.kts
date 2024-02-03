plugins {
    application
    id("cloud-cli.base-conventions")
    alias(libs.plugins.shadow)
    alias(libs.plugins.graal.native.buildtools)
}

dependencies {
    implementation(projects.cloudCli)
}

application {
    mainClass = "org.incendo.cloud.cli.example.ExampleApplication"
}

tasks {
    shadow {
    }

    build {
        dependsOn(shadowJar)
    }
}

graalvmNative {
    binaries.all {
        resources.autodetect()
    }
    binaries {
        getByName("main") {
            sharedLibrary = false
            mainClass = "org.incendo.cloud.cli.example.ExampleApplication"
            useFatJar = true
            javaLauncher =
                javaToolchains.launcherFor {
                    languageVersion = JavaLanguageVersion.of(21)
                    vendor = JvmVendorSpec.matching("GraalVM Community")
                }
        }
    }
    testSupport = false
    toolchainDetection = false
}

spotless {
    java {
        targetExclude("build/generated/**")
    }
}
