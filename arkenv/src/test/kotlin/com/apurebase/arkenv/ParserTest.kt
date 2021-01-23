package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class ParserTest {

    private val customFeature = object : ArkenvFeature {
        override fun onParse(arkenv: Arkenv, delegate: ArkenvArgument<*>): String? = "9000"
    }

    @Test fun `custom parser`() {
        val ark = object : Arkenv("Test", configureArkenv {
            install(customFeature)
        }) {
            val port: Int by argument()
        }

        ark.parse().expectThat {
            get { port }.isEqualTo(9000)
        }
    }

}
