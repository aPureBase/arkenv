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
Provide additional names for the argument as shown below. 
Arkenv accepts a variety of different formats. 
 
 ```kotlin
val port: Int by argument("--additional-name", "additionalName", "ADDITIONAL_NAME")
``` 

### Customization

##### Env prefix
Define a prefix that applies to all environment variable names.

The previous port example will then become `SOME_PREFIX_PORT`.

* Argument: `--arkenv-env-prefix`
* Env var: `ARKENV_ENV_PREFIX`
* Code: `EnvironmentVariableFeature(envPrefix = "some_prefix")`

##### [Dot env files]({{site.baseurl}}features/dot-env-files)

##### [Docker Secrets]({{site.baseurl}}features/docker-secrets)
