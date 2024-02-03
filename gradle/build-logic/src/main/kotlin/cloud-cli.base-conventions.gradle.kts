plugins {
    id("org.incendo.cloud-build-logic")
    id("org.incendo.cloud-build-logic.spotless")
}

indra {
    checkstyle().set(libs.versions.checkstyle)

    javaVersions {
        minimumToolchain(21)
        target(21)
        testWith().set(setOf(21))
    }
}

cloudSpotless {
    ktlintVersion = libs.versions.ktlint
}

spotless {
    java {
        importOrderFile(rootProject.file(".spotless/cloud-cli.importorder"))
    }
}

// Common dependencies.
dependencies {
    // external
    compileOnly(libs.immutables)
    annotationProcessor(libs.immutables)

    // test dependencies
    testImplementation(libs.jupiter.engine)
    testImplementation(libs.jupiter.params)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.truth)
}
