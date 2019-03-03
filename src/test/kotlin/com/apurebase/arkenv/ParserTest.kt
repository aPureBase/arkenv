package com.apurebase.arkenv

import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

class ParserTest {

    @Test fun `custom parser`() {
        val ark = object : Arkenv() {
            val port: Int by argument("--port")
        }

        ark.parsers.add { arkenv, delegate ->
            "9000"
        }
        ark.parse(arrayOf())

        ark.port shouldEqualTo 9000
    }

}
