package com.apurebase.arkenv

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.File
import java.io.FileNotFoundException

class EnvFileTests {

    class EnvFileArk(override val dotEnvFilePath: String?) : Arkenv() {
        val mysqlPassword: String by argument("--mysql-password")
        val port: Int by argument("--database-port")
    }

    @Test fun `should throw when dot env file can not be found`() {
        val ark = EnvFileArk("does_not_exit.env")
        assertThrows<FileNotFoundException> {
            ark.parse(arrayOf())
        }
    }

    @Test fun `should load values from dot env file`() {
        val path = File("src/test/resources/.env").absolutePath
        EnvFileArk(path).parse(arrayOf()).expectThat {
            get { mysqlPassword }.isEqualTo("this_is_expected")
            get { port }.isEqualTo(5050)
        }
    }
}
