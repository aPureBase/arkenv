package com.apurebase.arkenv

import com.apurebase.arkenv.feature.HttpClient
import com.apurebase.arkenv.feature.HttpFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo
import java.io.InputStream
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

class HttpFeatureTest {

    private inner class Ark(url: String) : Arkenv("Arkenv-Client", {
        install(HttpFeature(url, httpClient = MockClient(), cipher = decryptCipher))
    }) {
        val message: String by argument()
        val status: Int by argument()
        val nested: Int by argument("nested.item")
        val password: String by argument("spring.datasource.password")
    }

    @Test fun test() {
        Ark("http://localhost:8888")
            .parse()
            .expectThat {
                get { message }.isEqualTo("Hello world")
                get { status }.isEqualTo(100)
                get { nested }.isEqualTo(1)
                get { password }.isEqualTo("mysecret")
            }
    }

    private fun generateKey(): KeyPair =
        KeyPairGenerator
            .getInstance("RSA")
            .apply { initialize(512) }
            .genKeyPair()

    private val keyPair = generateKey()
    private val encryptCipher = keyPair.public.makeCipher(Cipher.ENCRYPT_MODE)
    private val decryptCipher = keyPair.private.makeCipher(Cipher.DECRYPT_MODE)
    private val password = "mysecret"
    private val encryptedPassword = encryptCipher
        .doFinal(password.toByteArray())
        .let { DatatypeConverter.printHexBinary(it) }

    private fun Key.makeCipher(mode: Int): Cipher = Cipher.getInstance("RSA").also { it.init(mode, this) }

    private inner class MockClient : HttpClient {
        private val response = """
                message: Hello world
                nested.item: 1
                status: 100
                spring.datasource.password: {cipher}$encryptedPassword
            """.trimIndent()

        override fun get(url: String): InputStream = response.byteInputStream()
    }
}
