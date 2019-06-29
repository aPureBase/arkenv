package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.feature.Encryption
import com.apurebase.arkenv.feature.EncryptionTest
import com.apurebase.arkenv.feature.ProfileFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo
import java.io.InputStream
import java.net.URL

class HttpFeatureTest {

    private val appName = "Arkenv-Client"
    private val rootUrl = "http://localhost:8888"

    private inner class Ark(url: String, responseMap: Map<URL, String>) : Arkenv(appName, configureArkenv {
        install(ProfileFeature())
        install(HttpFeature(url, MockClient(responseMap)))
        install(Encryption(EncryptionTest.decryptCipher))
    }) {
        val message: String by argument()
        val status: Int by argument()
        val nested: Int by argument("nested.item")
        val password: String by argument("spring.datasource.password")
    }

    @Test fun `simple http request`() {
        val profile = "production"
        val label = "myLabel"
        val responseMap = mapOf(URL("$rootUrl/$appName/$profile/$label") to response)
        Ark(rootUrl, responseMap)
            .parse("ARKENV_PROFILE", profile, "ARKENV_LABEL", label)
            .expectThat {
                get { message }.isEqualTo("Hello world")
                get { status }.isEqualTo(100)
                get { nested }.isEqualTo(1)
                get { password }.isEqualTo("mysecret")
            }
    }

    private val password = "mysecret"
    private val response = """
                message: Hello world
                nested.item: 1
                status: 100
                spring.datasource.password: {cipher}${EncryptionTest.encrypt(password)}
            """.trimIndent()

    private inner class MockClient(private val responseMap: Map<URL, String>) : HttpClientImpl() {
        override fun get(url: URL): InputStream = responseMap.getValue(url).byteInputStream()
    }
}
