package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.ArkenvBuilder
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import strikt.assertions.isEqualTo

interface FileBasedTests {

    class PropertiesArk(config: ArkenvBuilder) : Arkenv("Test", config) {
        val mysqlPassword: String by argument("--mysql-password")
        val port: Int by argument("--database-port")
        val multiLine: String by argument("--multi-string")
    }

    val defaultPort get() = 5050

    fun configure(propertiesFile: String, locations: List<String>): ArkenvBuilder

    fun verify(
        path: String,
        port: Int = defaultPort,
        pw: String = "this_is_expected",
        locations: List<String> = listOf(),
        vararg args: String = arrayOf()
    ) = PropertiesArk(configure(path, locations)).parse(*args).apply {
        expectThat {
            get { this.mysqlPassword }.isEqualTo(pw)
            get { this.port }.isEqualTo(port)
            get { this.multiLine.trim() }.isEqualTo("this stretches lines")
        }
    }
}
