---
layout: default
title: Properties
parent: Features
nav_order: 3
---

# Properties

Arkenv supports the properties file format.
 
Install the `PropertyFeature` and specify the file to load from. 

```kotlin
class PropertiesArk(propertiesFile: String) : Arkenv() {
    init {
        install(PropertyFeature(propertiesFile))
    }
    val mysqlPassword: String by argument("--mysql-password")
}
```

Arkenv will look for the file in the resources. 
Like with environment variables, property Arkenv will look for the
snake-case version of any double-hyphen names.

An example properties file could have the following content: 

```properties
mysql_password : JKE9ehnEA
```

If you want to use profiles like in Spring Boot, see [Profiles]({{site.baseurl}}features/profiles).
