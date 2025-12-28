plugins { kotlin("jvm") }

dependencies {
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.kotest:kotest-property")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
}

tasks.withType<Test> { useJUnitPlatform() }
