[![Maven Central](https://img.shields.io/maven-central/v/com.apurebase/arkenv.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.apurebase%22%20AND%20a:%22arkenv%22)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/pipeline.svg)](https://gitlab.com/apurebase/arkenv/pipelines)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/coverage.svg)](https://gitlab.com/apurebase/arkenv/commits/master)
[![Chat](https://img.shields.io/badge/chat-on%20slack-green.svg)](https://kotlinlang.slack.com/messages/CGF74HD19/)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)

<img src="/docs/arkenv_logo.png?raw=true" width="200">

Type-safe Kotlin configuration `by` delegates. 

Supports the most common external configuration sources, including: 
* [Command line](https://arkenv.io/features/command-line/)
* [Environment Variables](https://arkenv.io/features/environment-variables/)
* [Properties](https://arkenv.io/features/properties/), [Yaml](https://arkenv.io/features/yaml/), and Spring-like [profiles](https://arkenv.io/features/profiles/)
* [`.env` files](https://arkenv.io/features/dot-env-files/)


### 📦 Installation
Add [Maven Central](https://search.maven.org/search?q=arkenv) to your repositories and add Arkenv in Gradle:

```groovy
repositories { mavenCentral() }
implementation "com.apurebase:arkenv:$arkenv_version"
implementation "com.apurebase:arkenv-yaml:$arkenv_version" // for yaml support
```

### 🔨 Usage

#### 1. Define your arguments with the `argument` delegate.
```kotlin
object Arguments {
    val port: Int by argument()
}
```

or use constructor injection:
```kotlin
class Arguments(val port: Int)
```

#### 2. Parse your arguments.

```kotlin
fun main(args: Array<String>) {
    Arkenv.parse(Arguments, args) // object or existing instance
    Arkenv.parse<Arguments>(args) // constructor injection 
}
```

You can specify additional custom names for each `argument`.

The property's name is used as a fallback.

By default, Arkenv supports parsing command line arguments,
environment variables, and profiles.


In the case of `port`, you can parse it like this:
* From command line with `--port 443`
* As an environment variable `PORT=80`
* In a profile, like `application-dev.properties`, add `port=5000` 

 

To get started, we recommend reading about [the basics](https://arkenv.io/guides/the-basics) 
for a quick tour of what's included. 


### 📃 Documentation
Please visit [https://arkenv.io/](https://arkenv.io/) for in-depth documentation.

### 🤝 Contributing [![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg?style=flat-square)](http://makeapullrequest.com) 

##### Slack
Find the Arkenv channel in the [official Kotlin Slack](https://kotlinlang.slack.com/messages/CGF74HD19/).

