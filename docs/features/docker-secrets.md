---
layout: default
title: Docker Secrets
parent: Features
nav_order: 8
---

# Docker Secrets

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
