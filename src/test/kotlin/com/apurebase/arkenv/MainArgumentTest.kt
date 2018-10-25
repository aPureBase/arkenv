package com.apurebase.arkenv

import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldThrow
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

internal class MainArgumentTest {

    class Arkuments(args: Array<String>) : Arkenv(args) {
        val main: String by mainArgument { }
        val extra: String by argument("-e") { }
    }

    @Test fun `mixed main and normal`() {
        val args = Arkuments(arrayOf("-e", "import", "abc"))
        args.main shouldEqual "abc"
        args.extra shouldEqual "import"
    }

    @Test @Disabled // TODO
    fun `no main argument passed`() {
        { Arkuments(arrayOf()).main } shouldThrow Exception::class
        { Arkuments(arrayOf("-e", "import")).main } shouldThrow Exception::class
    }
}
