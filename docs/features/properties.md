---
layout: default
title: Properties
parent: Features
nav_order: 6
---

# Properties

Arkenv also supports the properties file format. Define a property file path with 
`propertiesFilePath` in the constructor.

```kotlin
class PropertiesArk : Arkenv(propertiesFilePath = "app.properties") {
    ...
}
```