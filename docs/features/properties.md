---
layout: default
title: Properties
parent: Features
nav_order: 6
---

# Properties

Arkenv also supports the properties file format. Define a property file with 
`propertiesFile` in the constructor.

```kotlin
class PropertiesArk : Arkenv(propertiesFile = "app.properties") {
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
