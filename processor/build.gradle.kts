plugins {
    kotlin("jvm")
}

val kspVersion: String by project

dependencies {
    implementation(kotlin("reflect"))
    implementation(kotlin("compiler"))
    implementation(project(":annotations"))

    implementation("com.google.devtools.ksp:symbol-processing:$kspVersion")
    implementation("com.google.devtools.ksp:symbol-processing-api:$kspVersion")

    implementation("com.squareup:kotlinpoet:2.0.0")
    implementation("com.squareup:kotlinpoet-ksp:2.0.0")
}