plugins {
	kotlin("jvm") version "2.2.21" apply false
	kotlin("plugin.spring") version "2.2.21" apply false
	kotlin("plugin.jpa") version "2.2.21" apply false
	id("org.springframework.boot") version "4.0.1" apply false
	id("io.spring.dependency-management") version "1.1.7" apply false
	id("com.diffplug.spotless") version "8.1.0" apply false
}

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

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(21)
		}
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

	dependencies {
		val implementation by configurations
		val testImplementation by configurations

		implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.1"))
	}
}
