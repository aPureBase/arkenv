---
layout: default
title: Profiles
parent: Features
nav_order: 4
---

To enable Spring-like profile functionality, install the `ProfileFeature`. 

```kotlin
class Ark : Arkenv() {
    init {
        install(ProfileFeature())
    }

    val port: Int by argument("--port")
    val name: String by argument("--name")
}
```

Given the following files in the resources folder:
* `application.properties`
* `application-dev.properties`
* `application-prod.properties`

The active profile can be set like this:
```kotlin
val args = arrayOf("--profile", "dev")
Ark().parse(args)
```

Or via env vars: 
```bash
$ export PROFILE=prod
```

Arkenv will load the base profile `application.properties`, 
and extend it with the active profile. 

