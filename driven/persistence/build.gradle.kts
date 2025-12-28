plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa") version "2.2.21"
}

dependencies {
    implementation(project(":domain"))

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")

    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.withType<Test> { useJUnitPlatform() }
