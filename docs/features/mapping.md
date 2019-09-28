---
layout: default
title: Mapping
parent: Features
nav_order: 13
---

# Mapping

The following mappings are supported by default:

```kotlin
object Ark : Arkenv() {
    val int:                Int by argument()
    val long:               Long by argument()
    val string:             String by argument()
    val char:               Char by argument()
    val intArray:           IntArray by argument()
    val shortArray:         ShortArray by argument()
    val charArray:          CharArray by argument()
    val longArray:          LongArray by argument()
    val floatArray:         FloatArray by argument()
    val doubleArray:        DoubleArray by argument()
    val booleanArray:       BooleanArray by argument()
    val byteArray:          ByteArray by argument()
    
    val stringList:         List<String> by argument()
    val stringCollection:   Collection<String> by argument() 
}
```

All primitive array types can be defined as a comma-separated string. 
The only exception being `CharArray`, which simply takes each input 
character as a single array item.

For `List` and `Collection` the type parameter is assumed to be `String`
and will only work in this case.

