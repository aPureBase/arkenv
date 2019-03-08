---
layout: default
title: Environment Variables
parent: Features
nav_order: 2
---

# Environment Variables

Arkenv allows parsing environment variables by default. Just put double hyphen `--` in front of your argument name like this:
```kotlin
val port: Int by argument("-p", "--port") { ... }
```
Then the env variable will be called `PORT`. 

When passing a hyphen-separated name like `--host-url` it will be parsed as `HOST_URL`.

Another option is to explicitly set the name of the environment variable. 
```kotlin
val description: String by argument("--description") {
    envVariable = "DESC"
}
```
You can now use either `DESCRIPTION` or `DESC` to set the argument via environment variables.
