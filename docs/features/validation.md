---
layout: default
title: Validation
parent: Features
nav_order: 2
---

# Validation

There is support for custom validations of the input passed to arguments. 

```kotlin
val failingProp: Int by argument("-f") {
    validate("number should be positive") { it > 0 }
    validate("should be even") { it % 2 == 0 }
}
```

In this scenario, the validation will fail when the number is negative or uneven. 
For example `-f -2` or `-f 5` would not pass. 

When a validation fails, it throws an exception with the given message and the actual value. 