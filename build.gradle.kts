import de.marcphilipp.gradle.nexus.NexusPublishPlugin
import java.time.Duration

val GROUP: String by project
val VERSION_NAME: String by project
val ossrhUsername: String? by project
val ossrhPassword: String? by project

plugins {
    id("io.codearte.nexus-staging") version "0.21.2"
    id("de.marcphilipp.nexus-publish") version "0.4.0"
}


allprojects {
    repositories {
        jcenter()
        mavenCentral()
    }
}

subprojects {
    group = GROUP
    version = VERSION_NAME

    apply<NexusPublishPlugin>()

    nexusPublishing {
        repositories {
            sonatype()
        }
        clientTimeout.set(Duration.parse("PT10M")) // 10 minutes
    }
}

nexusStaging {
    packageGroup = GROUP
    username = ossrhUsername
    password = ossrhPassword
    numberOfRetries = 360 // 1 hour if 10 seconds delay
    delayBetweenRetriesInMillis = 10000 // 10 seconds
}

tasks.wrapper {
    distributionType = Wrapper.DistributionType.ALL
}

tasks.closeRepository {
    mustRunAfter(subprojects.map { it.tasks.getByName("publishToSonatype") }.toTypedArray())
}
tasks.closeAndReleaseRepository {
    mustRunAfter(subprojects.map { it.tasks.getByName("publishToSonatype") }.toTypedArray())
}

//dependencies {
//    // Make the root project archives configuration depend on every subproject
//    subprojects.forEach {
//        archives(it)
//    }
//}
