[![Bintray](https://img.shields.io/bintray/v/apurebase/apurebase/arkenv.svg)](https://bintray.com/apurebase/apurebase/arkenv)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/coverage.svg)](https://gitlab.com/apurebase/arkenv/commits/master)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/pipeline.svg)](https://gitlab.com/apurebase/arkenv/pipelines)


# Arkenv
Kotlin Cli & Env argument parser. 

- [Usage](#usage)
- [Installation](#installation)
  - [Gradle](#gradle)

### Usage
```kotlin
class Arguments(args: Array<String>) : Arkenv(args) {

    val country: String by argument("-c") {
        description = "A simple String (required)"
    }

    val bool: Boolean by argument("-b") {
        description = "A bool, which will be false by default"
    }

    val port: Int by argument("-p") {
        description = "An Int with a default value"
        defaultValue = 5000
    }

    val nullInt: Int? by argument("-ni") {
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

#### Environment Variables

For environment variables to work you should have double `--` infront of your argument like this:
```kotlin
val port: Int by argument("-p", "--port") { ... }
```
Then the env variable will be called `PORT` and if giving a name like `--host-url` it will be `HOST_URL`

Another option is to explicitly set the name of the environment variable. 
```kotlin
val description: String by argument("--description") {
    envVariable = "DESC"
}
```
You can now use either `DESCRIPTION` or `DESC` to set the argument via environment variables.


## Installation

#### Gradle

```gradle
repositories {
    jcenter()
    ...
}

dependencies {
    compile "com.apurebase:arkenv:$arkenv_version"
    ...
}
```
