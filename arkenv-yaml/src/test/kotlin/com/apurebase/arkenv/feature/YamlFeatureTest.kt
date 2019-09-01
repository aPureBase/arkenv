package com.apurebase.arkenv.feature

import com.apurebase.arkenv.ArkenvBuilder
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.get
import com.apurebase.arkenv.test.expectThat
import org.junit.jupiter.api.Test
import strikt.assertions.isEqualTo

internal class YamlFeatureTest : FileBasedTests {

    override fun configure(propertiesFile: String, locations: List<String>): ArkenvBuilder = configureArkenv {
        clearInputAfterParse = false
        install(YamlFeature(propertiesFile, locations))
    }

    @Test fun `should load properties file`() {
        verify("app.yml").expectThat {
            get { this["this.is.a.nested"] }.isEqualTo("value")
            get { this["this.is.an.array"] }.isEqualTo("a,b,c")
        }
    }

}
