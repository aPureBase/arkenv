---
layout: default
title: Profiles
parent: Features
nav_order: 4
---

To enable Spring-like profile functionality, install the `ProfileFeature`. 

### Usage

```kotlin
class Ark : Arkenv("Example", configureArkenv { 
    install(ProfileFeature()) }
) { 
    val port: Int by argument()
    val name: String by argument()
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
$ export ARKENV_PROFILE=prod,dev
```

Arkenv will load the base profile `application.properties`, 
and extend it with the specified active profiles. 
Multiple active profiles can be set as a comma-separated list. 

### Customization

##### Profile prefix
If you do not like application.properties as the configuration file name, 
you can switch to another file name by specifying a custom prefix.  

* Argument: `--arkenv-profile-prefix`
* Env var: `ARKENV_PROFILE_PREFIX`
* Code: `ProfileFeature(prefix = "custom_prefix")`

##### Location
By default, Arkenv will look for profiles in the following relative locations 
on both the classpath and the file system: 
* `/` 
* `/config`
 
You can add additional locations by using the location argument, 
which accepts a comma-separated list of directory locations or file paths.

* Argument: `--arkenv-profile-location`
* Env var: `ARKENV_PROFILE_LOCATION`
* Code: `ProfileFeature(locations = listOf("some/dir"))`

##### Parsers

By default, the ProfileFeature supports property files, but can be 
extended to support other file formats. 
Specify additional parsers in the constructor. 

For example, yaml support can be added as follows:

```kotlin
class Ark : Arkenv("Example", configureArkenv {
    install(ProfileFeature(parsers = listOf(::YamlFeature)))
}) {
    ...
}

```

For further information see [YamlFeature]({{site.baseurl}}features/yaml).
