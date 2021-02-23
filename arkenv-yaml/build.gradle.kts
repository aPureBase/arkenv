plugins {
    base
    java
    kotlin("jvm") version "1.3.72"
    id("org.jetbrains.dokka") version "0.10.1"
    signing
}

val snakeyamlVersion: String by project
val jmockitVersion: String by project
val kluentVersion: String by project
val junitVersion: String by project
val striktVersion: String by project
val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

//val sourceTests: SourceSetOutput =

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    compile(project(":arkenv"))
    compile("org.yaml:snakeyaml:$snakeyamlVersion")

    // Can't figure out how to extend via arkenv project test sourcesets
    testCompile(project(":arkenv", "testArchive"))
}

tasks {
    compileKotlin { kotlinOptions { jvmTarget = "1.8" } }
    compileTestKotlin { kotlinOptions { jvmTarget = "1.8" } }

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
                description.set("Yaml feature for Arkenv")
                url.set("https://apurebase.gitlab.io/arkenv/features/yaml/")
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
    sign(publishing.publications["maven"])
}
