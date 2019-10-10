---
layout: default
title: Features
nav_order: 4
has_children: true
---

# Features

Arkenv supports a feature model that allows for 
adding and removing functionality as required.

```kotlin
class Ark : Arkenv("Example", configureArkenv {
    install(PropertyFeature("custom-file"))
    uninstall(CliFeature())
}) {
    val mysqlPassword: String by argument()
    val port: Int by argument()
}
```

In the constructor of Arkenv, specify a configuration lambda and call 
`install` to add / replace a feature or
`uninstall` to remove a feature. 

In the above example, we first add support for reading arguments from
a property file, and then remove the command line support. 

The following features are installed by default:
* [CliFeature]({{site.baseurl}}features/command-line) 
* [ProfileFeature]({{site.baseurl}}features/profiles)
* [EnvironmentVariableFeature]({{site.baseurl}}features/environment-variables)

### Order
The parse order depends on the order in which features are installed. 
Features that are installed earlier overrule features that are installed later.  

Arkenv will start by installing the default features, and then proceed with your features
installed in the configuration. 

This means that by default command line arguments have the highest order and will surpass other configuration sources. 


### Custom features

To create a new feature from scratch, simply implement the `ArkenvFeature`
interface. 

It has 3 overridable methods:
* `onLoad` is used to read data from a source and store it for parsing. 
* `onParse` is used on each argument property when parsing to obtain a value.
* `finally` can be used for clean up. 

Your feature does not need to implement all of these. 

Here is an example of how to use them:

```kotlin
class CustomFeature : ArkenvFeature {
    override fun onLoad(arkenv: Arkenv) {
        // read line from config file, split by equals and put them in the keyValue map for later parsing
        File("config")    
            .readLines()
            .map { it.split("=") }
            .map { it[0] to it[1] }
            .let { arkenv.keyValue.putAll(it) }
    }
    
    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? {
        // load text from a file with the name of the property if exists
        return File(delegate.property.name).takeIf(File::exists)?.readText()
    }
    
    // release resources or the like
    override fun finally(arkenv: Arkenv) = map.clear() 
}
```


### Processor features

**Since v2.1.0**

Processor features describe transformations that are applied to all values
that are loaded through Arkenv, no matter what feature they originate from. 

By default, this is used to resolve [placeholders]({{site.baseurl}}features/placeholders).

There is also support for decrypting values using the [EncryptionFeature]({{site.baseurl}}features/encryption).
