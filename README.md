[![Bintray](https://img.shields.io/bintray/v/apurebase/apurebase/arkenv.svg)](https://bintray.com/apurebase/apurebase/arkenv)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/coverage.svg)](https://gitlab.com/apurebase/arkenv/commits/master)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/pipeline.svg)](https://gitlab.com/apurebase/arkenv/pipelines)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)


![Arkenv Logo](/docs/arkenv_logo.png?raw=true "Arkenv Logo")

Type-safe Kotlin configuration parser `by` delegates. 

Supports the most common external configuration sources, including: 
* Command line
* Environment Variables
* Properties & Spring-like profiles
* Dot env (.env) files

_This repository is hosted on Gitlab (https://gitlab.com/apurebase/arkenv). Please report issues and open pull requests there._

### Usage
Define your arguments by extending `Arkenv` and declaring props with the `argument` delegate.
```kotlin
class Arguments : Arkenv() {

    val country: String by argument("-c") {
        description = "A simple String (required)"
    }

    val bool: Boolean by argument("-b") {
        description = "A bool, which will be false by default"
    }

    val port: Int by argument("-p", "--this-can-be-set-via-env") {
        description = "An Int with a default value"
        defaultValue = { 5000 }
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

### Documentation
Please visit [https://apurebase.gitlab.io/arkenv/](https://apurebase.gitlab.io/arkenv/) for in-depth documentation
