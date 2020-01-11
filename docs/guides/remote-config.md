---
layout: default
title: Remote configuration
parent: Guides
nav_order: 3
---

# Remote configuration

Arkenv can be set up to load configuration from a git repository.

Here is an [Arkenv Remote Example repository](https://github.com/AndreasVolkmann/arkenv-remote-example)
which contains properties files that can be used remotely.

Let's start with a basic Arkenv implementation:
```kotlin
class Ark : Arkenv("application", configureArkenv {
    install(HttpFeature(KtorHttpClient(CIO)))
}) {
    val source: String by argument()
    val port: Int by argument()
    val description: String by argument()
}
```

Install the http feature and provide a client implementation.
In this case, Ktor is used, which by itself supports many different client engines.

Define arguments that are going to be loaded from the repository.

We need to provide some configuration which can be done by
using a local profile:
```properties
# for github, this is the owner/repository combination
ARKENV_REMOTE_PROJECT_ID : AndreasVolkmann/arkenv-remote-example
# this points to the sub directory in the repository
ARKENV_REMOTE_DIRECTORY  : remote-test
# github is the default remote type
ARKENV_REMOTE_TYPE       : github
# host, which can differ in self-hosted scenarios
ARKENV_HTTP_URL          : https://github.com
```

Like the profile feature, Arkenv will look for a base profile and any active profiles.
