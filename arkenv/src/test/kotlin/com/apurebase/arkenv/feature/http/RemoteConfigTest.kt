package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import io.ktor.client.engine.cio.CIO
import io.ktor.util.KtorExperimentalAPI
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

@KtorExperimentalAPI
internal class RemoteConfigTest {

    private inner class Ark : Arkenv("application", configureArkenv {
        install(HttpFeature(KtorHttpClient(CIO)))
    }) {
        val source: String by argument()
        val port: Int by argument()
        val description: String by argument()
    }

    @Test fun `fetch config from remote GitLab`() {
        val host = "gitlab"
        MockSystem(
            "arkenv-profile-prefix" to "remote-test-$host", // use a different profile scheme locally to config remote
            "arkenv-profile" to "prod"
        )

        validate(host)
    }

    @Test fun `fetch config from remote GitHub`() {
        val host = "github"
        MockSystem(
            "arkenv-profile-prefix" to "remote-test-$host", // use a different profile scheme locally to config remote
            "arkenv-profile" to "prod"
        )

        validate(host)
    }

    private fun validate(host: String) {
        Ark().parse().expectThat {
            get { source }.isEqualTo("production-remote")
            get { port }.isEqualTo(1111)
            get { description }.isEqualTo("This config comes from $host!")
        }
    }
}
