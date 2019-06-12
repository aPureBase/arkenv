package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo
import java.io.InputStream
import java.net.URL
import java.security.Key
import java.security.KeyPair
import java.security.KeyPairGenerator
import javax.crypto.Cipher
import javax.xml.bind.DatatypeConverter

class HttpFeatureTest {

    private val appName = "Arkenv-Client"
    private val rootUrl = "http://localhost:8888"

    private inner class Ark(url: String, responseMap: Map<URL, String>) : Arkenv(appName, {
        install(ProfileFeature())
        install(HttpFeature(url, httpClient = MockClient(responseMap), cipher = decryptCipher))
    }) {
        val message: String by argument()
        val status: Int by argument()
        val nested: Int by argument("nested.item")
        val password: String by argument("spring.datasource.password")
    }

    @Test fun `simple http request`() {
        val profile = "test"
        Ark(rootUrl, mapOf(URL("$rootUrl/$appName/$profile") to response))
            .parse("ARKENV_PROFILE", profile)
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

    private fun Key.makeCipher(mode: Int): Cipher = Cipher.getInstance("RSA").also { it.init(mode, this) }

    private fun encrypt(input: String) = encryptCipher
        .doFinal(input.toByteArray())
        .let { DatatypeConverter.printHexBinary(it) }

    private val keyPair = generateKey()
    private val encryptCipher = keyPair.public.makeCipher(Cipher.ENCRYPT_MODE)
    private val decryptCipher = keyPair.private.makeCipher(Cipher.DECRYPT_MODE)
    private val password = "mysecret"
    private val response = """
                message: Hello world
                nested.item: 1
                status: 100
                spring.datasource.password: {cipher}${encrypt(password)}
            """.trimIndent()

    private inner class MockClient(private val responseMap: Map<URL, String>) : HttpClientImpl() {
        override fun get(url: URL): InputStream = responseMap.getValue(url).byteInputStream()
    }
}
