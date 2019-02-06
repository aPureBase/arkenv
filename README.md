[![Bintray](https://img.shields.io/bintray/v/apurebase/apurebase/arkenv.svg)](https://bintray.com/apurebase/apurebase/arkenv)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/coverage.svg)](https://gitlab.com/apurebase/arkenv/commits/master)
[![Pipelines](https://gitlab.com/apurebase/arkenv/badges/master/pipeline.svg)](https://gitlab.com/apurebase/arkenv/pipelines)
[![Awesome Kotlin Badge](https://kotlin.link/awesome-kotlin.svg)](https://github.com/KotlinBy/awesome-kotlin)


![Arkenv Logo](/docs/arkenv_logo.png?raw=true "Arkenv Logo")

Kotlin Cli & Env argument parser using delegates. 

This repository is hosted on Gitlab (https://gitlab.com/apurebase/arkenv). 

Please report issues and open pull requests there.

- [Usage](#usage)
- [Installation](#installation)
  - [Gradle](#gradle)
  - [Maven](#maven)
- [Features](#features)
  - [Environment Variables](#environment-variables)
  - [Boolean Merge](#boolean-merge)
  - [Validation](#validation)
  - [Manual Input](#manual-input)
  - [Docker Secrets](#docker-secrets)
  - [Dot Env Files](#dot-env-files)
  - [Properties](#properties)
  

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

Then you can pass your args to the `parse` function.
```kotlin
fun main(args: Array<String>) {
    Arguments().parse(args)
}
```


## Installation

#### Gradle

```gradle
repositories {
    jcenter()
}

dependencies {
    compile "com.apurebase:arkenv:$arkenv_version"
}
```

#### Maven
```xml
<dependency>
    <groupId>com.apurebase</groupId>
    <artifactId>arkenv</artifactId>
    <version>${arkenv_version}</version>
</dependency>

<repositories>
    <repository>
        <id>jcenter</id>
        <url>https://jcenter.bintray.com/</url>
    </repository>
</repositories>
```

## Features

#### Environment Variables

Arkenv allows parsing environment variables by default. Just put double hyphen `--` in front of your argument name like this:
```kotlin
val port: Int by argument("-p", "--port") { ... }
```
Then the env variable will be called `PORT`. 

When passing a hyphen-separated name like `--host-url` it will be parsed as `HOST_URL`.

Another option is to explicitly set the name of the environment variable. 
```kotlin
val description: String by argument("--description") {
    envVariable = "DESC"
}
```
You can now use either `DESCRIPTION` or `DESC` to set the argument via environment variables.


#### Boolean Merge

When multiple boolean arguments are declared, you can merge their arguments instead of defining them separately.

Given the following Arkenv: 
```kotlin
class Arguments : Arkenv() {
    val doRun: Boolean by argument("-d", "--do-run")
    val production: Boolean by argument("-p", "--production")
    val something: Boolean by argument("-s", "--something")
}
```
Either of the following will turn all arguments true: 
`-d -p -s` `-dps` `-spd` `-sp -d`

#### Validation

There is support for custom validations of the input passed to arguments. 

```kotlin
val failingProp: Int by argument("-f") {
    validate("number should be positive") { it > 0 }
    validate("should be even") { it % 2 == 0 }
}
```

In this scenario, the validation will fail when the number is negative or uneven. 
For example `-f -2` or `-f 5` would not pass. 

When a validation fails, it throws an exception with the given message. 

#### Manual Input

An argument can be specified at runtime via the command line. 

```kotlin
val name: String by argument("-n") {
    acceptsManualInput = true
}
```

When set to true, Arkenv will ask for a value on parse, which can then be provided by the user.

If the argument is already defined then no input is requested.

#### Docker Secrets

In some cases we don't want to directly expose the arguments to the program. 

An alternative is to use secret files, which can be loaded via environment variables. 

```kotlin
val ark = object : Arkenv(enableEnvSecrets = true) {
    val apiKey: String by argument("--api-key")
}
```

Normally, the `apiKey` could be declared by defining an env var `API_KEY`, 
but when the `enableEnvSecrets` flag is set to true in the constructor, 
it is also possible to use `API_KEY_FILE` with the path to the file containing the secret value. 

```bash
$ export API_KEY_FILE=file_including_api_key.txt
```

Arkenv loads the file and reads the entire content. 

#### Dot Env Files
A lot of tools integrate with the dot env (.env) file format, 
which allows to define environment variables in a plain file. 

In order to seamlessly integrate your application, Arkenv supports parsing these files.

An example of such a file, named `.env` could look like this:
```bash
MYSQL_PASSWORD=this_is_expected
DATABASE_PORT=5050
```

Use `dotEnvFilePath` to specify the location of the dot env file. 
```kotlin
class EnvFileArk : Arkenv(dotEnvFilePath = ".env") {
    val mysqlPassword: String by argument("--mysql-password")
    val port: Int by argument("--database-port")
}
```
The parsing rules are the same as for [Environment Variables](#environment-variables), 
meaning it accepts the snake case version of double-hyphen arguments. 

#### Properties
Arkenv also supports the properties file format. Define a property file path with 
`propertiesFilePath` in the constructor.

```kotlin
class PropertiesArk : Arkenv(propertiesFilePath = "app.properties") {
    ...
}
```
