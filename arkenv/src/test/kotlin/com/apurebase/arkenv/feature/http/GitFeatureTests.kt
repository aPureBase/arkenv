package com.apurebase.arkenv.feature.http

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.parse
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import org.junit.jupiter.api.DynamicTest
import org.junit.jupiter.api.DynamicTest.dynamicTest
import org.junit.jupiter.api.TestFactory
import strikt.assertions.isEqualTo
import strikt.assertions.isTrue

internal class GitFeatureTests {

    private inner class Ark : Arkenv(configureArkenv {
        install(GitFeature())
    }) {
        val source: String by argument()
        val port: Int by argument()
        val description: String by argument()
        val secondary: Boolean by argument()
    }

    @TestFactory fun `fetch config from remote`(): List<DynamicTest> = remoteTypeNames.map {
        dynamicTest(it) { remoteTest(it) }
    }

    @TestFactory fun `different branch`(): List<DynamicTest> = remoteTypeNames.map {
        dynamicTest(it) {
            val branch = "alternate"
            remoteTest(
                GitFeature.RemoteType.Github.name.toLowerCase(),
                source = branch,
                args = arrayOf("--arkenv-remote-branch", branch)
            )
        }
    }

    private fun remoteTest(host: String, source: String = prodSource, args: Array<String> = arrayOf()) {
        MockSystem(
            // use a different profile scheme locally to config remote
            "arkenv-profile-prefix" to "remote-test-$host",
            "arkenv-profile" to "prod,second" // load multiple profiles
        )

        validate(host, source, args)
    }

    private fun validate(host: String, source: String, args: Array<String> = arrayOf()) =
        Ark().parse(args).expectThat {
            get { this.source }.isEqualTo(source)
            get { port }.isEqualTo(1111)
            get { description }.isEqualTo("This config comes from $host!")
            get { secondary }.isTrue()
        }

    private val prodSource = "production-remote"

    private val remoteTypeNames = GitFeature.RemoteType.values().map { it.name.toLowerCase() }
}
