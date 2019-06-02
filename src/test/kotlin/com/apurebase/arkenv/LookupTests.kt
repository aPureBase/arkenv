package com.apurebase.arkenv

import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.contains
import strikt.assertions.isNotNull

class LookupTests {

    private class Ark : Arkenv("Test", {
        clearInputAfterParse = false
        install(PropertyFeature())
    })

    @Test fun `remaining arguments should be available`() {
        val ark = Ark().parse()
        ark["PORT"] shouldBeEqualTo "80"
        ark["name"] shouldBeEqualTo "profile-test"
    }

    @Test fun `should resolve unused cli args`() {
        val ark = Ark().parse("--left-over", "expected")
        ark["left-over"] shouldBeEqualTo "expected"
    }

    @Test fun `should throw when argument does not exist`() {
        val ark = Ark().parse("--other", "name")
        val key = "left-over"
        assertThrows<IllegalArgumentException> { ark[key] }
            .expectThat { get { message }.isNotNull().contains(key) }
    }
}
