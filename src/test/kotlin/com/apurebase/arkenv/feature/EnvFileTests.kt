package com.apurebase.arkenv.feature

import com.apurebase.arkenv.*
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.getTestResourcePath
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.FileNotFoundException

class EnvFileTests {

    private class EnvFileArk(dotEnvFilePath: String?) : Arkenv("Test", configureArkenv {
        install(EnvironmentVariableFeature(dotEnvFilePath = dotEnvFilePath))
    }) {
        val mysqlPassword: String by argument("--mysql-password")
        val port: Int by argument("--database-port")
    }

    @Test fun `should throw when dot env file can not be found`() {
        val ark = EnvFileArk("does_not_exit.env")
        assertThrows<FileNotFoundException> {
            ark.parse()
        }
    }

    @Test fun `should load values from dot env file`() {
        val path = getTestResourcePath(".env")
        EnvFileArk(path).parse().expectThat {
            get { mysqlPassword }.isEqualTo("this_is_expected")
            get { port }.isEqualTo(5050)
        }
    }
}
