package com.apurebase.arkenv

import org.amshove.kluent.shouldEqualTo
import org.junit.jupiter.api.Test

class LoaderTest {

    @Test fun `custom loader`() {
        val ark = object : Arkenv() {
            val port: Int by argument("--port")
        }
        ark.loaders.add {
            // load yaml or whatever
            it.dotEnv["PORT"] = "99"
        }

        ark.parse(arrayOf())

        ark.port shouldEqualTo 99
    }

}
