package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import java.util.*
import javax.crypto.Cipher

/**
 * Supports decryption of encrypted values during the processing phase.
 * Values will be decrypted if they start with a certain prefix
 * The encryption prefix can be configured via the *ARKENV_ENCRYPTION_PREFIX* argument.
 * By default, the prefix is *{cipher}*
 *
 * @param cipher The cipher used to decrypt the values
 * @since 2.1.0
 */
class Encryption(private val cipher: Cipher) : ProcessorFeature {

    override lateinit var arkenv: Arkenv

    private val prefixKey = "ARKENV_ENCRYPTION_PREFIX"
    private val defaultPrefix = "{cipher}"

    override fun process(key: String, value: String): String {
        if (key == prefixKey) return value
        val prefix = arkenv.getOrNull(prefixKey) ?: defaultPrefix
        return when {
            value.startsWith(prefix) -> cipher.decrypt(value.removePrefix(prefix))
            else -> value
        }
    }

    private fun Cipher.decrypt(input: String): String = Base64.getDecoder()
        .decode(input)
        .let(::doFinal)
        .let(::String)
}
