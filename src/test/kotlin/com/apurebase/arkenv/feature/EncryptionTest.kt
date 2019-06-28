package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

internal class EncryptionTest {

    private inner class Ark : Arkenv("EncryptionTest", configureArkenv {
        install(Encryption(decryptCipher))
    }) {
        val string: String by argument()
        val int: Int by argument()
    }

    @Test fun test() {
        val prefix = "{cipher_test}"
        val stringValue = "input"
        val intValue = 56
        val encryptedString = prefix + encrypt(stringValue)
        val encryptedInt = prefix + encrypt(intValue.toString())

        Ark().parse("ARKENV_HTTP_ENCRYPTION_PREFIX", prefix, "--STRING", encryptedString, "--INT", encryptedInt)
            .expectThat {
                get { string }.isEqualTo(stringValue)
                get { int }.isEqualTo(intValue)
            }
    }

    companion object {
        private fun generateKey(): KeyPair = KeyPairGenerator
            .getInstance("RSA")
            .apply { initialize(512) }
            .genKeyPair()

        private fun Key.makeCipher(mode: Int): Cipher = Cipher.getInstance("RSA").also { it.init(mode, this) }
        private val keyPair = generateKey()
        private val encryptCipher = keyPair.public.makeCipher(Cipher.ENCRYPT_MODE)
        val decryptCipher = keyPair.private.makeCipher(Cipher.DECRYPT_MODE)

        fun encrypt(input: String): String = encryptCipher
            .doFinal(input.toByteArray())
            .let { DatatypeConverter.printHexBinary(it) }
    }
}
