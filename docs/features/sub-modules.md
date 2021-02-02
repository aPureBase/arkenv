---
layout: default
title: Sub Modules
parent: Features
nav_order: 11
---

# Sub Modules

**Since v3.1.0**

Arkenv definitions can be organized by using sub modules.

A common scenario for sub modules is when you want your configuration 
split into multiple files.

For example, you may want to define your database configuration 
in a different class / file. 

```kotlin
class DatabaseConfig : Arkenv() {
    val port: Int by argument()
}

class Ark : Arkenv() {
    val name: String by argument()
    val database = module(DatabaseConfig())
}
```

The above example shows how to use the `module` function to register 
another Arkenv instance as a module, which will then be parsed together 
with the root module that it is defined in.   

The sub module's properties can then be accessed after parsing. 
```kotlin
fun main(args: Array<String>) {
    val ark = Ark().parse(args)
    println(ark.database.port) 
}
```

The sub modules will be parsed using the root module's features.

Recursive modules are not supported and will lead to a stack overflow.

## Plain classes

**Since v3.2.0**

For plain classes, the module function is used to provide a delegate.

```kotlin
object Module {
    val port: Int by argument()
}

object Configuration {
    val subModule by module(Module)
}
```