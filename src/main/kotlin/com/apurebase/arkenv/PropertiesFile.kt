package com.apurebase.arkenv

class PropertiesFile(
    val name: String,
    val classLoader: ClassLoader = Arkenv::class.java.classLoader
)
