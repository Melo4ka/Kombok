plugins {
    kotlin("jvm")
    id("com.google.devtools.ksp") version "2.1.0-1.0.29"
}

ksp {
    // arg("builderVarargFunctions", "true")
}

dependencies {
    implementation(project(":annotations"))
    ksp(project(":processor"))

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}