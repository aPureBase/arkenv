[![Bintray](https://api.bintray.com/packages/apurebase/apurebase/arkenv/images/download.svg)](https://bintray.com/apurebase/apurebase/arkenv)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/pipeline.svg)](https://gitlab.com/apurebase/arkenv/pipelines)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/coverage.svg)](https://gitlab.com/apurebase/arkenv/commits/master)
[![Chat](https://img.shields.io/badge/chat-on%20slack-green.svg)](https://kotlinlang.slack.com/messages/CGF74HD19/)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

<img src="/docs/arkenv_logo.png?raw=true" width="200">

Type-safe Kotlin configuration parser `by` delegates. 

Supports the most common external configuration sources, including: 
* Command line
* Environment Variables
* Properties, Yaml & Spring-like profiles
* `.env` files


### 📦 Installation
Add jcenter to your repositories and add Arkenv in Gradle:

```groovy
repositories { jcenter() }
compile "com.apurebase:arkenv:$arkenv_version"
```

### 🔨 Usage
Define your arguments with the `argument` delegate.
```kotlin
object Arguments {

    val country: String by argument()

    val bool: Boolean by argument("-b")

    val port: Int by argument()
}
```

Parse your arguments.

```kotlin
fun main(args: Array<String>) {
    Arkenv.parse(Arguments, args)
}
```

You can specify additional custom names for each argument. 
The property's name is used as a fallback.

By default, Arkenv supports parsing command line arguments,
environment variables, and profiles.


In the case of `port`, you can parse it like this:
* From command line with `--port 443`
* As an environment variable `PORT=80`
* In a profile, like `application-dev.properties`, add `port=5000` 

 

To get started, we recommend reading about [the basics](https://apurebase.gitlab.io/arkenv/guides/the-basics) 
for a quick tour of what's included. 


### 📃 Documentation
Please visit [https://apurebase.gitlab.io/arkenv/](https://apurebase.gitlab.io/arkenv/) for in-depth documentation.

### 🤝 Contributing [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com) 

##### Slack
Find the Arkenv channel in the [official Kotlin Slack](https://kotlinlang.slack.com/messages/CGF74HD19/).

