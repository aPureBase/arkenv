plugins {
    base
    java
    kotlin("jvm") version "1.4.32"
    id("org.jetbrains.dokka") version "1.4.32"
    signing
}

val snakeyamlVersion: String by project
val jmockitVersion: String by project
val kluentVersion: String by project
val junitVersion: String by project
val striktVersion: String by project
val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    api(project(":arkenv"))
    implementation("org.yaml:snakeyaml:$snakeyamlVersion")

    testImplementation(testFixtures(project(":arkenv")))
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:$junitVersion")
}

tasks {
    compileKotlin { kotlinOptions { jvmTarget = "1.8" } }
    compileTestKotlin { kotlinOptions { jvmTarget = "1.8" } }

    test {
        useJUnitPlatform()
        doFirst {
            jvmArgs = listOf(
                "-javaagent:${classpath.find { it.name.contains("jmockit") }!!.absolutePath}"
            )
        }
    }
    dokkaHtml {
        outputDirectory.set(buildDir.resolve("javadoc"))
        dokkaSourceSets {
            configureEach {
                jdkVersion.set(8)
                reportUndocumented.set(true)
                platform.set(org.jetbrains.dokka.Platform.jvm)
            }
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
    from(tasks.dokkaHtml)
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
                description.set("Yaml feature for Arkenv")
                url.set("https://arkenv.io/features/yaml/")
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
