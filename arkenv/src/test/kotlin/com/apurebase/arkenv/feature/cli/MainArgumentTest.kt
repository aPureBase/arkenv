package com.apurebase.arkenv.feature.cli

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.MissingArgumentException
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.mainArgument
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo

internal class MainArgumentTest {

    private class Ark : Arkenv() {
        val main: String by mainArgument()
        val extra: String by argument("-e", "--extra")
    }

    @Test fun `mixed main and normal`() {
        val args = Ark().parse("-e", "import", "abc")
        args.main shouldBeEqualTo "abc"
        args.extra shouldBeEqualTo "import"
    }

    @Test fun `mixed main and env`() {
        MockSystem("EXTRA" to "import")
        Ark()
            .parse("abc")
            .expectThat {
                get { main }.isEqualTo("abc")
                get { extra }.isEqualTo("import")
            }
    }

    @Test fun `env before main`() {
        val ark = object : Arkenv() {
            val extra: String by argument("-e", "--extra")
            val main: String by mainArgument {
                defaultValue = { "default" }
            }
        }
        MockSystem("EXTRA" to "import")
        ark
            .parse("abc")
            .expectThat {
                get { main }.isEqualTo("abc")
                get { extra }.isEqualTo("import")
            }
    }

    @Test fun `no main argument passed`() {
        assertThrows<MissingArgumentException> { Ark().main }
        assertThrows<MissingArgumentException> { Ark().parse("-e", "import") }
    }

    @Test fun `main should not eat unused args`() {
        val ark = object : Arkenv() {
            val main: Int by mainArgument()
        }
        ark.parse("-b", "99").expectThat {
            get { main }.isEqualTo(99)
        }
    }
}
