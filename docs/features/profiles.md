---
layout: default
title: Profiles
parent: Features
nav_order: 4
---

To enable Spring-like profile functionality, install the `ProfileFeature`. 

### Usage

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
val args = arrayOf("--arkenv-profile", "dev")
Ark().parse(args)
```

Or via env vars: 
```bash
$ export ARKENV_PROFILE=prod
```

Arkenv will load the base profile `application.properties`, 
and extend it with the active dev or production profile. 

### Customization

##### Profile name
If you do not like application.properties as the configuration file name, 
you can switch to another file name by specifying a custom prefix.  

* Argument: `--arkenv-profile-prefix`
* Env var: `ARKENV_PROFILE_PREFIX`
* Code: `ProfileFeature(prefix = "custom_prefix")`

##### Location
You can also refer to an explicit location by using the spring.config.location environment property
 (which is a comma-separated list of directory locations or file paths).
 
spring.config.name and spring.config.location are used very early to determine which files have
 to be loaded, so they must be defined as an environment property 
 (typically an OS environment variable, a system property, or a command-line argument).
