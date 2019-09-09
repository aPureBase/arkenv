package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.getTestResourcePath
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.assertions.isEqualTo
import java.io.FileNotFoundException

class EnvFileTests {

    private class EnvFileArk(dotEnvFilePath: String? = null) : Arkenv("Test", configureArkenv {
        uninstall(EnvironmentVariableFeature())
        install(EnvironmentVariableFeature(dotEnvFilePath = dotEnvFilePath))
    }) {
        val mysqlPassword: String by argument()
        val port: Int by argument("--database-port")
        val connectionString: String by argument()
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
            get { connectionString }.isEqualTo("localhost:5050;database=testdb;user=testuser;")
        }
    }

    @Test fun `dot env file can be specified via argument`() {
        val path = getTestResourcePath(".env-alt")
        EnvFileArk()
            .parse("ARKENV_DOT_ENV_FILE", path)
            .expectThat {
                get { mysqlPassword }.isEqualTo("alternative")
                get { port }.isEqualTo(8080)
                get { connectionString }.isEqualTo("localhost")
            }
    }
}
