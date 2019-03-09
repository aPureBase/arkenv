---
layout: default
title: Features
nav_order: 4
has_children: true
---

# Features

Arkenv supports a feature model that allows for 
adding and removing functionality as required.

```kotlin
class PropertiesArk(propertiesFile: String) : Arkenv() {
    init {
        install(PropertyFeature(propertiesFile))
        uninstall(CliFeature())
    }
    
    val mysqlPassword: String by argument("--mysql-password")
    
    val port: Int by argument("--database-port")
}
```

In the init block of Arkenv, call `install` to add a feature or
`uninstall` to remove a feature. 

In the above example, we first add support for reading arguments from
a property file, and then remove the command line support. 

By default, the `CliFeature` and `EnvironmentVariableFeature` are enabled.

To create a new feature from scratch, simply implement the `ArkenvFeature`
interface. 

It has 3 overridable methods:
* `onLoad` used to read arguments from a source and store them for parsing
* `onParse` used on each argument property when parsing to obtain a value
* `configure` can be used to further configure the declared arguments
