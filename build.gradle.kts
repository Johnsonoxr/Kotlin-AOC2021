plugins {
    kotlin("jvm") version "1.9.20"
}

sourceSets {
    main {
        kotlin.srcDir("src")
    }
}

tasks {
    wrapper {
        gradleVersion = "8.5"
    }
}

dependencies {
    //  coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.2")
}