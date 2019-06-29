package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

/**
 * Supports decryption of encrypted values during the processing phase.
 * Values will be decrypted if they start with a certain prefix
 * The encryption prefix can be configured via the *ARKENV_HTTP_ENCRYPTION_PREFIX* argument.
 * By default, the prefix is *{cipher}*
 *
 * @param cipher The cipher used to decrypt the values
 */
class Encryption(private val cipher: Cipher) : ProcessorFeature {

    override lateinit var arkenv: Arkenv

    private val prefixKey = "ARKENV_HTTP_ENCRYPTION_PREFIX"

    override fun process(key: String, value: String): String {
        if (key == prefixKey) return value
        val prefix = arkenv.getOrNull(prefixKey) ?: "{cipher}"
        return when {
            value.startsWith(prefix) -> cipher.decrypt(value.removePrefix(prefix))
            else -> value
        }
    }

    private fun Cipher.decrypt(input: String): String = DatatypeConverter
        .parseHexBinary(input)
        .let(::doFinal)
        .let { String(it) }
}
