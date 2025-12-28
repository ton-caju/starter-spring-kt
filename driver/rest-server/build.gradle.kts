plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":driven:persistence"))
    implementation(project(":driven:event-producer"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation(
        "org.springdoc:springdoc-openapi-starter-webmvc-ui:${rootProject.extra["springdocVersion"]}"
    )

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("io.kotest:kotest-runner-junit5")
    testImplementation("io.kotest:kotest-assertions-core")
    testImplementation("io.mockk:mockk:${rootProject.extra["mockkVersion"]}")
    testImplementation("com.ninja-squad:springmockk:${rootProject.extra["springmockkVersion"]}")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.postgresql:postgresql")
}

tasks.withType<Test> { useJUnitPlatform() }
