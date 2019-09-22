---
layout: default
title: Validation
parent: Yaml
nav_order: 11
---

# Yaml

**Since v2.1.0**

Arkenv support the yaml file format. 

This functionality is located in a separate module `arkenv-yaml`.

Add it as a dependency in your `build.gradle`.
```groovy
compile "com.apurebase:arkenv-yaml:$arkenv_version"
```

Install the `YamlFeature` and specify the file to load from.

### Usage

```kotlin
class Ark : Arkenv(configuration = {
    install(YamlFeature("config"))
}) {
    val mysqlPassword: String by argument()
}
```

Arkenv will look for the file in the resources.
 
Like with environment variables, Arkenv will look for the
snake-case version of any double-hyphen names.

An example yaml file `config.yml` could have the following content: 

```yaml
mysql_password : JKE9ehnEA
```

Nested keys will be concatenated using an underscore `_`.

Arrays will be parsed as a comma-separated string.

### Profiles

The yaml feature can also be used together with the profile feature to 
load yaml source files. Specify additional parsers in the constructor.

```kotlin
class Ark : Arkenv("Example", configureArkenv {
    install(ProfileFeature(parsers = listOf(::YamlFeature)))
}) {
    ...
}

```

For further information see [Profiles]({{site.baseurl}}features/profiles).

### Customization

##### Locations
By default, Arkenv will look for profiles in the following relative locations 
on both the classpath and the file system: 
* `/` 
* `/config`  

You can add additional locations by using the location argument, 
which accepts a comma-separated list of directory locations or file paths.

* Argument: `--arkenv-property-location`
* Env var: `ARKENV_PROPERTY_LOCATION`
* Code: `PropertyFeature(locations = listOf("./path/"))`
