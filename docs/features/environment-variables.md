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
```kotlin
val port: Int by argument()
```
The corresponding environment variable will then be called `PORT`.

Either rely on the default name that is derived from the property name,
or specify names in the argument call. 

#### Additional names
Put a double hyphen (`--`) in front of your argument name.
Arkenv distinguishes between single-hyphen names for command line
 and double-hyphen names for both cli and environment variables.
 
 ```kotlin
val port: Int by argument("--additional-name")
``` 

When passing a hyphen-separated name like `--host-url` it will be parsed as `HOST_URL`.

##### Explicit env variable name
⚠️*Deprecated*

Another option is to explicitly set the name of the environment variable.
 
```kotlin
val description: String by argument {
    envVariable = "DESC"
}
```
You can now use either `DESCRIPTION` or `DESC` to set the argument via environment variables.

### Customization

##### Env prefix
Define a prefix that applies to all environment variable names.

The previous port example will then become `SOME_PREFIX_PORT`.

* Argument: `--arkenv-env-prefix`
* Env var: `ARKENV_ENV_PREFIX`
* Code: `EnvironmentVariableFeature(envPrefix = "some_prefix")`

##### [Dot env files]({{site.baseurl}}features/dot-env-files)

##### [Docker Secrets]({{site.baseurl}}features/docker-secrets)
