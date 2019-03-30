package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Test
import strikt.assertions.contains
import strikt.assertions.isEqualTo

internal class MainArgumentTest {

    private class Ark : Arkenv() {
        val main: String by mainArgument { }
        val extra: String by argument("-e", "--extra") { }
    }

    @Test fun `mixed main and normal`() {
        val args = Ark().parse(arrayOf("-e", "import", "abc"))
        args.main shouldEqual "abc"
        args.extra shouldEqual "import"
    }

    @Test fun `mixed main and env`() {
        MockSystem("EXTRA" to "import")
        Ark()
            .parse(arrayOf("abc"))
            .expectThat {
                get { main }.isEqualTo("abc")
                get { extra }.isEqualTo("import")
            }
    }

    @Test fun `env before main`() {
        val ark = object : Arkenv() {
            val extra: String by argument("-e", "--extra") { }
            val main: String by mainArgument {
                defaultValue = { "default" }
            }
        }
        MockSystem("EXTRA" to "import")
        ark
            .parse(arrayOf("abc"))
            .expectThat {
                get { main }.isEqualTo("abc")
                get { extra }.isEqualTo("import")
            }
    }

    @Test fun `no main argument passed`() {
        { Ark().main } shouldThrow IllegalArgumentException::class
        { Ark().parse(arrayOf("-e", "import")) } shouldThrow IllegalArgumentException::class
    }

    @Test fun `main should not eat unused args`() {
        val ark = object : Arkenv() {
            val main: Int by mainArgument()
        }
        ark.parse(arrayOf("-b", "99")).expectThat {
            get { main }.isEqualTo(99)
        }
    }
}
