---
layout: default
title: Properties
parent: Features
nav_order: 3
---

# Properties

Arkenv supports the properties file format.
 
Install the `PropertyFeature` and specify the file to load from. 

### Usage 

```kotlin
class Ark : Arkenv(configuration = {
    +PropertyFeature("application")
}) { 
    val mysqlPassword: String by argument()
}
```

Arkenv will look for the file in the resources. 

Like with environment variables, Arkenv will look for the
snake-case version of any double-hyphen names.

An example properties file `application.properties` could have the following content: 

```properties
mysql_password = JKE9ehnEA
```

If you want to use profiles like in Spring Boot, see [Profiles]({{site.baseurl}}features/profiles).

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
