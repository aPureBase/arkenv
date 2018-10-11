package com.apurebase.arkenv

import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test

internal class MainArgumentTest {

    class Arkuments(args: Array<String>) : Arkenv(args) {
        val main: String by mainArgument { }
        val extra: String by argument("-e") {  }
    }

    @Test
    fun `mixed main and normal`() {
        val args = Arkuments(arrayOf("-e", "import", "abc"))
        args.main shouldEqual "abc"
        args.extra shouldEqual "import"
    }
}