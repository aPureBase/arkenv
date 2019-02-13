---
layout: default
title: Boolean Merge
parent: Features
nav_order: 4
---

# Boolean Merge

When multiple boolean arguments are declared, you can merge their arguments instead of defining them separately.

Given the following Arkenv: 
```kotlin
class Arguments : Arkenv() {
    val doRun: Boolean by argument("-d", "--do-run")
    val production: Boolean by argument("-p", "--production")
    val something: Boolean by argument("-s", "--something")
}
```
Either of the following will turn all arguments true: 
`-d -p -s` `-dps` `-spd` `-sp -d`