package com.apurebase.arkenv

import com.apurebase.arkenv.feature.ArkenvFeature
import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

class ParserTest {

    private val customFeature = object : ArkenvFeature {
        override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? {
            return "9000"
        }
    }

    @Test fun `custom parser`() {
        val ark = object : Arkenv(configuration = {
            install(customFeature)
        }) {
            val port: Int by argument("--port")
        }

        ark.parse(arrayOf())

        ark.port shouldEqualTo 9000
    }

}
