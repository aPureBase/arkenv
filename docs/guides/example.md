---
layout: default
title: Example
parent: Guides
nav_order: 1
---

# Example

```kotlin
class Arguments : Arkenv() {
    val port: Int by argument("-p") {
        description = "An Int with a default value"
        defaultValue = { 5000 }
    }
}

fun main(args: Array<String>) {
    // Prints out either 5000 or defined from cli
    println("Port: " + Arguments().parse(args).port) 
}
```

```bash
java -jar app.jar -p 80 # Prints "Port: 80"
```