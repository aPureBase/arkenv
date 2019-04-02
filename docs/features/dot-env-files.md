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
