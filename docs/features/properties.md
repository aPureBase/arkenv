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
    ...
}
```

Arkenv will look for the file in the resources. 