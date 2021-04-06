plugins {
    base
    kotlin("jvm") version "1.4.21"
    id("org.jetbrains.dokka") version "0.10.1"
    id("java-test-fixtures")
    signing
}

val snakeyamlVersion: String by project
val junitVersion: String by project
val jmockitVersion: String by project
val kluentVersion: String by project
val striktVersion: String by project

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")


dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(kotlin("reflect"))


    testFixturesApi("org.yaml:snakeyaml:$snakeyamlVersion")
    testFixturesApi("org.jmockit:jmockit:$jmockitVersion")
    testFixturesApi("org.amshove.kluent:kluent:$kluentVersion")
    testFixturesApi("org.junit.jupiter:junit-jupiter-api:$junitVersion")
    testFixturesApi("io.strikt:strikt-core:$striktVersion")

    testImplementation(testFixtures(project(":arkenv")))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    compileKotlin { kotlinOptions { jvmTarget = "1.8" } }
    compileTestKotlin { kotlinOptions { jvmTarget = "1.8" } }
    compileTestFixturesKotlin { kotlinOptions { jvmTarget = "1.8" } }

    test {
        useJUnitPlatform()
        doFirst {
            jvmArgs = listOf(
                "-javaagent:${classpath.find { it.name.contains("jmockit") }!!.absolutePath}"
            )
        }
    }
    dokka {
        outputFormat = "html"
        outputDirectory = "$buildDir/javadoc"
        impliedPlatforms = mutableListOf("JVM")
        configuration {
            jdkVersion = 8
            reportUndocumented = true
        }
    }
}

val sourcesJar by tasks.creating(Jar::class) {
    classifier = "sources"
    from(sourceSets.main.get().allSource)
}

val dokkaJar by tasks.creating(Jar::class) {
    group = JavaBasePlugin.DOCUMENTATION_GROUP
    classifier = "javadoc"
    from(tasks.dokka)
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = project.name
            from(components["java"])
            artifact(sourcesJar)
            artifact(dokkaJar)
            pom {
                name.set("Arkenv")
                description.set("Type-safe Kotlin configuration parser by delegates")
                url.set("https://apurebase.gitlab.io/arkenv/")
                organization {
                    name.set("aPureBase")
                    url.set("http://apurebase.com/")
                }
                licenses {
                    license {
                        name.set("The Apache License, Version 2.0")
                        url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                    }
                }
                developers {
                    developer {
                        id.set("avolkmann")
                        name.set("Andreas Volkmann")
                        email.set("avolkmann@me.com")
                    }
                    developer {
                        id.set("jeggy")
                        name.set("Jógvan Olsen")
                        email.set("jol@apurebase.com")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/aPureBase/arkenv.git")
                    developerConnection.set("scm:git:https://github.com/aPureBase/arkenv.git")
                    url.set("https://github.com/aPureBase/arkenv/")
                    tag.set("HEAD")
                }
            }
        }
    }
}

signing {
    isRequired = isReleaseVersion
    useInMemoryPgpKeys(
        System.getenv("ORG_GRADLE_PROJECT_signingKey"),
        System.getenv("ORG_GRADLE_PROJECT_signingPassword")
    )
    sign(publishing.publications["maven"])
}
