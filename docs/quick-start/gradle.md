---
layout: default
title: Gradle
parent: Quick Start
nav_order: 1
---

# Gradle Installation

### Gradle

```gradle
repositories {
    jcenter()
}

dependencies {
    implementation "com.apurebase:arkenv:$arkenv_version"
}
```

### Gradle Kotlin Script

```kotlin
repositories {
    jcenter()
}

dependencies {
    implementation("com.apurebase:arkenv:$arkenvVersion")
}
```