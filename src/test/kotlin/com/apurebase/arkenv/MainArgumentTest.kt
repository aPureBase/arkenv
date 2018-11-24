package com.apurebase.arkenv

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test

internal class MainArgumentTest {

    class Arkuments : Arkenv() {
        val main: String by mainArgument { }
        val extra: String by argument("-e") { }
    }

    @Test fun `mixed main and normal`() {
        val args = Arkuments().parse(arrayOf("-e", "import", "abc"))
        args.main shouldEqual "abc"
        args.extra shouldEqual "import"
    }

    @Test fun `no main argument passed`() {
        { Arkuments().main } shouldThrow IllegalArgumentException::class
        { Arkuments().parse(arrayOf("-e", "import")) } shouldThrow IllegalArgumentException::class
    }
}
