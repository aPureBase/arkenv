package com.apurebase.arkenv

import com.apurebase.arkenv.feature.Encryption
import com.apurebase.arkenv.feature.EncryptionTest
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class FeatureOrderTest {

    private inner class Ark : Arkenv("FeatureOrderTest", configureArkenv {
        install(Encryption(EncryptionTest.decryptCipher))
    }) {
        val string: String by argument()
    }

    @Test fun `decryption should happen before placeholder parsing`() {
        val prefix = "{cipher_test}"
        val stringValue = "input: \${int}"
        val intValue = 56
        val expectedValue = "input: $intValue"
        val encryptedString = prefix + EncryptionTest.encrypt(stringValue)
        val encryptedInt = prefix + EncryptionTest.encrypt(intValue.toString())

        Ark().parse("ARKENV_ENCRYPTION_PREFIX", prefix, "--STRING", encryptedString, "--INT", encryptedInt)
            .expectThat { get { string }.isEqualTo(expectedValue) }
    }
}
