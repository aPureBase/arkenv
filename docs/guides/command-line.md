---
layout: default
title: Command Line Guide
parent: Guides
nav_order: 1
---

# Command Line Guide

This guide introduces several features that are useful 
if your primary purpose is to support command line configuration 
with the args that are passed through via your main method

The code below defines an Arkenv instance with several arguments 
which showcase distinct functionality. 

```kotlin
class Arguments : Arkenv() {

    val country: String by argument {
        description = "A simple String (required)"
    }

    val bool: Boolean by argument("-b") {
        description = "A bool, which will be false by default"
    }

    val port: Int by argument {
        description = "An Int with a default value"
        defaultValue = { 5000 }
    }

    val nullInt: Int? by argument {
        description = "A nullable Int, which doesn't have to be declared"
    }

    val mapped: List<String> by argument("-m") {
        description = "Complex types can be achieved with a mapping"
        mapping = { it.split("|") }
    }

    val mainString: String by mainArgument {
        description = "This is a main arg, so no names"
    }
}
```

Each argument declares a `description`, which explains its significance. 
This description is included when calling `toString()` on the instance.

Here is a valid example of how to parse these arguments from the command line,
assuming your jar is called App.jar:
```bash
java -jar App.jar --country DK -m hello|world port=6060 "last argument means main!"
```
This results in the following:
* `country` will be assigned `DK`
* `bool` will remain `false`
* `port` will be `6060` instead of its default value
* `nullInt` will be `null`
* `mapped` will be `[hello, world]`
* `mainString` will take up the last argument: `last argument means main!`

