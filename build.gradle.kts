plugins {
    id("java")
    id("org.jetbrains.intellij.platform") version "2.20.1"
}

group = "de.mehtrick"
version = providers.gradleProperty("pluginVersion").orElse("1.0.4").get()

val platformVersion = providers.gradleProperty("platformVersion").orElse("2026.1.5.1")
val platformJavaVersion = platformVersion.map { if (it.startsWith("2026.1")) 21 else 25 }

java {
    toolchain {
        languageVersion.set(platformJavaVersion.map(JavaLanguageVersion::of))
    }
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
    options.encoding = "UTF-8"
}

repositories {
    mavenCentral()
    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        intellijIdea(platformVersion)
        bundledPlugin("org.jetbrains.plugins.yaml")
        testFramework(org.jetbrains.intellij.platform.gradle.TestFrameworkType.Platform)
        pluginVerifier()
        zipSigner()
    }
    testImplementation("junit:junit:4.13.2")
    testRuntimeOnly("org.opentest4j:opentest4j:1.3.0")
}

tasks.test {
    useJUnit()
    failOnNoDiscoveredTests.set(true)
    testLogging {
        events("failed", "skipped")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
    }
}

intellijPlatform {
    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = "263.*"
        }
        changeNotes = """
            1.0.4 - Support IntelliJ IDEA 2026, update YAML integration and build tooling,
            and enable automated editor regression tests.
        """.trimIndent()
    }
    publishing {
        token = providers.environmentVariable("PUBLISH_TOKEN")
    }
    pluginVerification {
        ides {
            create("IU", "2026.1.5.1")
            create("IU", "2026.2.5")
            create("IU", "2026.3")
        }
    }
}
