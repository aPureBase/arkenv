---
layout: default
title: .env Files
parent: Features
nav_order: 5
---

# .env Files

A lot of tools integrate with the dot env (.env) file format, 
which allows to define environment variables in a plain file. 

In order to seamlessly integrate your application, Arkenv supports parsing these files.

An example of such a file, named `.env` could look like this:
```bash
MYSQL_PASSWORD=this_is_expected
PORT=5050
```

Use `dotEnvFilePath` to specify the location of the dot env file. 
```kotlin
class Ark : Arkenv("Example", configureArkenv {
    install(EnvironmentVariableFeature(dotEnvFilePath = ".env"))
}) {
    val mysqlPassword: String by argument()
    val port: Int by argument()
}
```
The parsing rules are the same as for [Environment Variables](#environment-variables), 
meaning it accepts the snake case version of double-hyphen arguments. 

### Customization

To enable the use and specify a dot env file to load, use one of the 
following.

* Argument: `--arkenv-dot-env-file`
* Env var: `ARKENV_DOT_ENV_FILE`
* Code: `EnvironmentVariableFeature(dotEnvFilePath = ".env")`
