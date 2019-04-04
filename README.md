[![Bintray](https://img.shields.io/bintray/v/apurebase/apurebase/arkenv.svg)](https://bintray.com/apurebase/apurebase/arkenv)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/pipeline.svg)](https://gitlab.com/apurebase/arkenv/pipelines)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/coverage.svg)](https://gitlab.com/apurebase/arkenv/commits/master)
[![Chat](https://img.shields.io/badge/chat-on%20slack-green.svg)](https://kotlinlang.slack.com/messages/CGF74HD19/)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

<img src="/docs/arkenv_logo.png?raw=true" width="200">

Type-safe Kotlin configuration parser `by` delegates. 

Supports the most common external configuration sources, including: 
* Command line
* Environment Variables
* Properties & Spring-like profiles
* Dot env (.env) files


### 📦 Installation
Add jcenter to your repositories. Then you can add Arkenv in Gradle:

```groovy
repositories { jcenter() }
compile "com.apurebase:arkenv:$arkenv_version"
```

### 🔨 Usage
Define your arguments by extending `Arkenv` and declaring props with the `argument` delegate.
```kotlin
class Arguments : Arkenv() {

    val country: String by argument()

    val bool: Boolean by argument {
        description = "A bool, which will be false by default"
    }

    val port: Int by argument("-p", "--this-can-be-set-via-env") {
        description = "An Int with a default value and custom names"
        defaultValue = { 5000 }
    }

    val nullInt: Int? by argument {
        description = "A nullable Int, which doesn't have to be declared"
    }

    val mapped: List<String> by argument("-m") {
        description = "Complex types can be achieved with a mapping"
        mapping = { it.split("|") }
    }
}
```
If you don't specify any names for the argument, it will use the property's name. 

In the case of `country`, you can parse it like this:
* From command line with `--country world`
* As an environment variable `COUNTRY=world`

By default, Arkenv supports parsing command line arguments and environment variables. 
Read more about other features and their configuration [here](https://apurebase.gitlab.io/arkenv/features/features/). 

You can also find more examples and guides in [the documentation](https://apurebase.gitlab.io/arkenv/guides/guides/) 


### 📃 Documentation
Please visit [https://apurebase.gitlab.io/arkenv/](https://apurebase.gitlab.io/arkenv/) for in-depth documentation.

### 🤝 Contributing [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com) 
##### Issues
This repository is hosted on Gitlab (https://gitlab.com/apurebase/arkenv). 
Please report issues and open pull requests there.

##### Slack
Find the Arkenv channel in the [official Kotlin Slack](https://kotlinlang.slack.com/messages/CGF74HD19/).

