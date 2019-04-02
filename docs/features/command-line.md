---
layout: default
title: Command Line
parent: Features
nav_order: 1
---

# Command Line

Arkenv supports parsing arguments from the command line. 
The following exemplifies how to pass arguments correctly. 

```kotlin
class Ark : Arkenv() {
    val int: Int by argument("-i", "--int")
    
    val bool: Boolean by argument("-b", "--bool")
}
```

The above example defines two properties, `int` and `bool`. 

In order to set `int` equal to 5 and `bool` to true, one can call the program like this:

`java -jar app.jar -i 5 -b` 

`java -jar app.jar --int 5 --bool`

Any of the names defined in the argument are valid. 

### Assignment

Another way to set arguments is to use assignments. 

`java -jar app.jar int=5 bool=false`

This is especially useful when a boolean needs to be true by default 
and false when passed as a parameter. 
