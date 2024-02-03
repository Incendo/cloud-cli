plugins {
    id("cloud-cli.base-conventions")
    id("cloud-cli.publishing-conventions")
}

dependencies {
    api(libs.cloud.core)
    api(libs.jansi)
}
