---
layout: default
title: Manual Input
parent: Features
nav_order: 5
---

# Manual Input

An argument can be specified at runtime via the command line. 

```kotlin
val name: String by argument("-n") {
    acceptsManualInput = true
}
```

When set to true, Arkenv will ask for a value on parse, which can then be provided by the user.

If the argument is already defined then no input is requested.