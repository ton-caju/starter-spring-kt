plugins {
	kotlin("jvm") version "2.2.21" apply false
	kotlin("plugin.spring") version "2.2.21" apply false
	kotlin("plugin.jpa") version "2.2.21" apply false
	id("org.springframework.boot") version "4.0.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("com.diffplug.spotless") version "8.1.0" apply false
	id("org.sonarqube") version "7.2.1.6560"
	id("jacoco")
}

extra["kotestVersion"] = "5.9.1"
extra["mockkVersion"] = "1.13.13"
extra["springmockkVersion"] = "4.0.2"
extra["testcontainersVersion"] = "1.19.8"
extra["springdocVersion"] = "2.8.14"

allprojects {
	group = "br.com.caju"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "com.diffplug.spotless")
	apply(plugin = "jacoco")

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
	}

	configure<JacocoPluginExtension> {
		toolVersion = "0.8.12"
	}

	configure<com.diffplug.gradle.spotless.SpotlessExtension> {
		kotlin {
			target("**/*.kt")
			targetExclude("build/**/*.kt")
			ktfmt().kotlinlangStyle()
		}
		kotlinGradle {
			target("*.gradle.kts")
			ktfmt().kotlinlangStyle()
		}
		java {
			target("**/*.java")
			targetExclude("build/**/*.java")
			googleJavaFormat()
		}
	}

	tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
		compilerOptions {
			freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
		finalizedBy("jacocoTestReport")
	}

	tasks.named<JacocoReport>("jacocoTestReport") {
		reports {
			xml.required.set(true)
			html.required.set(true)
			csv.required.set(false)
		}
	}

	tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
		violationRules {
			rule {
				limit {
					minimum = "0.70".toBigDecimal()
				}
			}
		}
	}

	dependencies {
		val implementation by configurations
		val testImplementation by configurations

		implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.1"))
		testImplementation(platform("org.testcontainers:testcontainers-bom:${rootProject.extra["testcontainersVersion"]}"))
		testImplementation(platform("io.kotest:kotest-bom:${rootProject.extra["kotestVersion"]}"))

		implementation("org.jetbrains.kotlin:kotlin-reflect")

		testImplementation("org.junit.platform:junit-platform-launcher")
	}
}

sonar {
	properties {
		property("sonar.projectKey", "caju_starter")
		property("sonar.projectName", "Starter - Hexagonal Architecture")
		property("sonar.projectVersion", version.toString())
		property("sonar.sourceEncoding", "UTF-8")
		property("sonar.kotlin.detekt.reportPaths", "**/build/reports/detekt/detekt.xml")
		property("sonar.coverage.jacoco.xmlReportPaths", "**/build/reports/jacoco/test/jacocoTestReport.xml")
		property("sonar.java.binaries", "**/build/classes")

		// Exclusões de análise
		property("sonar.exclusions", "**/*Application.kt,**/*Config.kt,**/build/**,**/generated/**")
		property("sonar.coverage.exclusions", "**/*Application.kt,**/*Config.kt,**/dto/**,**/entity/**,**/model/**")

		// Configurações de qualidade
		property("sonar.qualitygate.wait", "false")
		property("sonar.cpd.exclusions", "**/dto/**,**/entity/**,**/model/**")

		// Análise de duplicação
		property("sonar.cpd.kotlin.minimumTokens", "100")
	}
}
