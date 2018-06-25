import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.maven
import org.gradle.kotlin.dsl.*
import org.gradle.kotlin.dsl.repositories
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.extra
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.kotlin
import org.gradle.kotlin.dsl.repositories
import org.jetbrains.kotlin.gradle.dsl.Coroutines
import org.gradle.api.tasks.bundling.Jar

group = "com.apurebase"
version = "1.0.0"

plugins {
    kotlin("jvm") version "1.2.50"
    `maven-publish`
}

val junitVersion = "5.2.0"

repositories {
    maven { setUrl("http://dl.bintray.com/kotlin/ktor") }
    maven { setUrl("https://dl.bintray.com/kotlin/kotlinx") }
    maven { setUrl("http://dl.bintray.com/kotlin/exposed") }
    mavenCentral()
    jcenter()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
    compile("org.jetbrains.kotlin", "kotlin-reflect", "1.2.50")

    testCompile("org.junit.jupiter", "junit-jupiter-api", junitVersion)
    testCompile("org.amshove.kluent", "kluent", "1.38")
    testCompile("org.jmockit:jmockit:1.39")
    testRuntime("org.junit.jupiter", "junit-jupiter-engine", junitVersion)
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(java.sourceSets["main"].allSource)
}


val privateMavenWriteUrl: String by project
val privateMavenPassword: String by project
publishing {
    repositories {
        maven {
            url = uri(privateMavenWriteUrl)
            credentials {
                username = "myMavenRepo"
                password = privateMavenPassword
            }
        }
    }
    (publications) {
        "mavenJava"(MavenPublication::class) {
            from(components["java"])
            artifact(sourcesJar)
        }
    }
}