---
layout: default
title: Git Remote Configuration
parent: Features
nav_order: 13
---

# Git Remote Configuration

Arkenv can be set up to load configuration from a git repository.

Here is an [Arkenv Remote Example repository](https://github.com/AndreasVolkmann/arkenv-remote-example)
which contains properties files that can be used remotely.

Let's start with a basic Arkenv implementation:
```kotlin
class Ark : Arkenv(configureArkenv {
    install(GitFeature())
}) {
    val source: String by argument()
    val port: Int by argument()
    val description: String by argument()
}
```

Install the http feature and optionally provide a client implementation.

Define arguments that are going to be loaded from the repository.

Some configuration has to be provided, which can be done by
using a local profile:
```properties
# For github, this is the owner/repository combination
ARKENV_REMOTE_PROJECT_ID : AndreasVolkmann/arkenv-remote-example
# Optional, points to the sub directory in the repository
ARKENV_REMOTE_DIRECTORY  : remote-test
```

Like the profile feature, Arkenv will look for a base profile and any
active profiles in the remote.
