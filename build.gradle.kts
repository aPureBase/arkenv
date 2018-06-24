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

group = "com.apurebase"
version = "1.0-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.2.50"
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
}
