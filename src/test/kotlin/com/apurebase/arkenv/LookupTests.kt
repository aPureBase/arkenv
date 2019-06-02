package com.apurebase.arkenv

import com.apurebase.arkenv.feature.PropertyFeature
import com.apurebase.arkenv.test.parse
import org.amshove.kluent.shouldBeEqualTo
import org.junit.jupiter.api.Test


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


}
