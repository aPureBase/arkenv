package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.TestFactory
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

internal class RemoteConfigTest {

    private inner class Ark : Arkenv(configuration = configureArkenv {
        install(GitFeature())
    }) {
        val source: String by argument()
        val port: Int by argument()
        val description: String by argument()
        val secondary: Boolean by argument()
    }

    @TestFactory fun `fetch config from remote`() =
        GitFeature.RemoteType.values().map { it.name.toLowerCase() }.map { host ->
            DynamicTest.dynamicTest(host) {
                MockSystem(
                    // use a different profile scheme locally to config remote
                    "arkenv-profile-prefix" to "remote-test-$host",
                    "arkenv-profile" to "prod,second" // load multiple profiles
                )

                validate(host)
            }
        }

    private fun validate(host: String) = Ark().parse().expectThat {
        get { source }.isEqualTo("production-remote")
        get { port }.isEqualTo(1111)
        get { description }.isEqualTo("This config comes from $host!")
        get { secondary }.isTrue()
    }
}
