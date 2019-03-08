package com.apurebase.arkenv

import org.amshove.kluent.shouldEqualTo
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test
import org.yaml.snakeyaml.Yaml
import strikt.assertions.isEqualTo

class LoaderTest {

    private open class Ark : Arkenv() {
        val port: Int by argument("--port")
    }

    @Test fun `custom loader`() {
        val ark = Ark()
        ark.loaders.add {
            // load yaml or whatever
            it.dotEnv["PORT"] = "99"
        }

        ark.parse(arrayOf())

        ark.port shouldEqualTo 99
    }

    @Test fun `yaml example`() {
        val ark = object : Ark() {
            val name: String by argument("--name")
        }

        @Language("yaml")
        val yaml = """
          port: 99
          name: hello world
        """.trimIndent()

        ark.loaders.add {
            val map = (Yaml().load(yaml) as Map<String, Any>)
                .map { (key, value) -> key.toUpperCase() to value.toString() }
            it.dotEnv.putAll(map)
        }

        ark.parse(arrayOf()).expectThat {
            get { port }.isEqualTo(99)
            get { name }.isEqualTo("hello world")
        }
    }
}
