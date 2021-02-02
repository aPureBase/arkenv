package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.util.putAll
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.collections.component1
import kotlin.collections.component2

class LoaderTest {

    private open class Ark(feature: ArkenvFeature) : Arkenv("Test", configureArkenv { +feature }) {
        val port: Int by argument()
    }

    @Test fun `custom loader`() {
        val feature = object : ArkenvFeature {
            override fun onLoad(arkenv: Arkenv) {
                arkenv["PORT"] = "99"
            }
        }

        val ark = Ark(feature)
        ark.parse()
        expectThat(ark) { get { port } isEqualTo 99 }
    }

    @Test fun `yaml example`() {
        class YamlFeature(private val yaml: String) : ArkenvFeature {
            override fun onLoad(arkenv: Arkenv) {
                val map = (Yaml().load(yaml) as Map<String, Any>)
                    .map { (key, value) -> key.toUpperCase() to value.toString() }
                    .toMap()
                arkenv.putAll(map)
            }
        }

        @Language("yaml")
        val yaml = """
          port: 99
          name: hello world
        """.trimIndent()

        class YamlArk(yaml: String) : Ark(YamlFeature(yaml)) {
            val name: String by argument()
        }

        YamlArk(yaml)
            .parse()
            .expectThat {
                get { port } isEqualTo 99
                get { name } isEqualTo "hello world"
            }
    }
}
