---
layout: default
title: Encryption
parent: Features
nav_order: 12
---

# Encryption

**Since v2.1.0**

* Encryption is a [processor feature]({{site.baseurl}}features/features)

Supports decryption of encrypted values during the processing phase.
Values will be decrypted if they start with a certain prefix.

The encryption prefix can be configured via the `ARKENV_ENCRYPTION_PREFIX` argument.
By default, the prefix is `{cipher}`.

### Usage

```kotlin
class Ark : Arkenv(configureArkenv {
    install(Encryption(decryptCipher))
}) {
    val string: String by argument()
}
```

Since this is a processor feature, any value that is loaded by
Arkenv will be considered for decryption.

