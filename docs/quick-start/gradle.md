---
layout: default
title: Gradle
parent: Quick Start
nav_order: 1
---

# Gradle Installation
[![Maven Central](https://img.shields.io/maven-central/v/com.apurebase/arkenv.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.apurebase%22%20AND%20a:%22arkenv%22)

### Gradle

```gradle
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.apurebase:arkenv:$arkenv_version"
}
```

### Gradle Kotlin Script

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("com.apurebase:arkenv:$arkenvVersion")
}
```
