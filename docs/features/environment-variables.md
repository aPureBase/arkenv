---
layout: default
title: Environment Variables
parent: Features
nav_order: 2
---

# Environment Variables

Arkenv allows parsing arguments with environment variables. 

The `EnvironmentVariableFeature` is installed by default.

### Usage
Put a double hyphen (`--`) in front of your argument name like this:
```kotlin
val port: Int by argument("-p", "--port")
```
The corresponding environment variable will then be called `PORT`. 

When passing a hyphen-separated name like `--host-url` it will be parsed as `HOST_URL`.

Another option is to explicitly set the name of the environment variable. 
```kotlin
val description: String by argument("--description") {
    envVariable = "DESC"
}
```
You can now use either `DESCRIPTION` or `DESC` to set the argument via environment variables.

### Customization

##### Env prefix
Define a prefix that applies to all environment variable names.

The previous port example will then become `SOME_PREFIX_PORT`.

* Code: `EnvironmentVariableFeature(envPrefix = "some_prefix")`

##### [Dot env files]({{ site.baseurl }}features/dot-env-files)

##### [Docker Secrets]({{ site.baseurl }}features/docker-secrets)
