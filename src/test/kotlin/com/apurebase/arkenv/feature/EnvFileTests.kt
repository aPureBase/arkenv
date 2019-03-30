package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.FileNotFoundException

class EnvFileTests {

    private class EnvFileArk(dotEnvFilePath: String?) : Arkenv(configuration = {
        install(EnvironmentVariableFeature(dotEnvFilePath = dotEnvFilePath))
    }) {
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
        val path = getTestResourcePath(".env")
        EnvFileArk(path).parse(arrayOf()).expectThat {
            get { mysqlPassword }.isEqualTo("this_is_expected")
            get { port }.isEqualTo(5050)
        }
    }
}
