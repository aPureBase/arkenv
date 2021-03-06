package com.apurebase.arkenv

import com.apurebase.arkenv.argument.ArkenvArgument
import com.apurebase.arkenv.feature.ArkenvFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.argument
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

class ParserTest {

    private val customFeature = object : ArkenvFeature {
        override fun onParse(arkenv: Arkenv, delegate: ArkenvArgument<*>): String? = "9000"
    }

    @Test fun `custom parser`() {
        val ark = object : Arkenv("Test", configureArkenv { +customFeature }) {
            val port: Int by argument()
        }

        ark.parse().expectThat {
            get { port } isEqualTo 9000
        }
    }

}
