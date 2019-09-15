package com.apurebase.arkenv

import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.test.MockSystem
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldBeNull
import org.amshove.kluent.shouldEqual
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.contains
import strikt.assertions.isNotNull

class LookupTests {

    private class Ark : Arkenv("Test", configureArkenv {
        clearInputAfterParse = false
        install(PropertyFeature())
    })

    @Test fun `should resolve unused properties`() {
        val ark = Ark().parse()
        ark["PORT"] shouldBeEqualTo "80"
        ark["name"] shouldBeEqualTo "profile-test"
    }

    @Test fun `should resolve unused cli args`() {
        val ark = Ark().parse("--left-over", "expected")
        ark["left-over"] shouldBeEqualTo "expected"
    }

    @Test fun `should resolve environment variables`() {
        val key = "key"
        val value = "value"
        MockSystem(key to value)
        val ark = Ark().parse()
        ark[key] shouldBeEqualTo value
    }

    @Test fun `should throw when argument does not exist`() {
        val ark = Ark().parse("--other", "name")
        val key = "left-over"
        assertThrows<IllegalArgumentException> { ark[key] }
            .expectThat { get { message }.isNotNull().contains(key) }
    }

    @Nested
    inner class Nullable {

        @Test fun `should resolve unused properties`() {
            val ark = Ark().parse()
            ark.getOrNull("PORT") shouldEqual "80"
            ark.getOrNull("name") shouldEqual "profile-test"
        }

        @Test fun `should return null when argument does not exist`() {
            val ark = Ark().parse()
            ark.getOrNull("left-over").shouldBeNull()
        }
    }
}