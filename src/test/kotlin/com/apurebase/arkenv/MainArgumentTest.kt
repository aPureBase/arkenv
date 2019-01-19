package com.apurebase.arkenv

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test
import strikt.assertions.contains
import strikt.assertions.isEqualTo

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

    @Test fun `main should not eat unused args`() {
        val ark = object : Arkenv() {
            val main: Int by mainArgument()
        }
        ark.parse(arrayOf("-b", "99")).expectThat {
            get { main }.isEqualTo(99)
            get { argList }.contains("-b")
            get { argList }.not().contains("99")
        }
    }
}
