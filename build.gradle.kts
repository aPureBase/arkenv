import de.marcphilipp.gradle.nexus.NexusPublishPlugin
import java.time.Duration

val group: String by project
val version: String by project
val sonatypeUsername: String? = project.findProperty("sonatypeUsername") as String? ?: System.getenv("sonatypeUsername")
val sonatypePassword: String? = project.findProperty("sonatypeUsername") as String? ?: System.getenv("sonatypePassword")

plugins {
    id("io.codearte.nexus-staging") version "0.21.2"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
    id("com.github.ben-manes.versions") version "0.38.0"
    jacoco
}


allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {
    group = group
    version = version

    apply<NexusPublishPlugin>()

    nexusPublishing {
        repositories {
            sonatype()
        }
        clientTimeout.set(Duration.parse("PT10M")) // 10 minutes
    }
}

nexusStaging {
    packageGroup = group
    username = sonatypeUsername
    password = sonatypePassword
    numberOfRetries = 360 // 1 hour if 10 seconds delay
    delayBetweenRetriesInMillis = 10000 // 10 seconds
}

tasks {
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
    closeRepository {
        mustRunAfter(subprojects.map { it.tasks.getByName("publishToSonatype") }.toTypedArray())
    }
    closeAndReleaseRepository {
        mustRunAfter(subprojects.map { it.tasks.getByName("publishToSonatype") }.toTypedArray())
    }
}
