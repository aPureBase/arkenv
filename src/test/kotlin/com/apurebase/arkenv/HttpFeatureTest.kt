package com.apurebase.arkenv

import com.apurebase.arkenv.feature.HttpClient
import com.apurebase.arkenv.feature.HttpFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo
import java.io.InputStream

class HttpFeatureTest {

    private class Ark(url: String, response: String) : Arkenv("Arkenv-Client", {
        install(HttpFeature(url, null, MockClient(response)))
    }) {
        val message: String by argument()

        val status: Int by argument()

        val nested: Int by argument("nested.item")
    }

    @Test fun test() {
        val ark = Ark(
            "http://localhost:8888",
            """ message: Hello world
                nested.item: 1
                status: 100
            """.trimIndent()
        ).parse()

        ark.expectThat {
            get { message }.isEqualTo("Hello world")
            get { status }.isEqualTo(100)
            get { nested }.isEqualTo(1)
        }
    }

    private class MockClient(private val response: String) : HttpClient {
        override fun get(url: String): InputStream = response.byteInputStream()
    }
}
