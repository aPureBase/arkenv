package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.configureArkenv
import com.apurebase.arkenv.test.dotEnvPath
import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.getTestResourcePath
import com.apurebase.arkenv.test.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import strikt.api.Assertion
import strikt.assertions.isEqualTo
import java.io.FileNotFoundException

class EnvFileTests {

    private inner class EnvFileArk(dotEnvFilePath: String? = null) : Arkenv(configureArkenv {
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
        EnvFileArk(dotEnvPath).parse().expectThat { verify() }
    }

    @Test fun `dot env file can be specified via argument`() {
        EnvFileArk()
            .parse("ARKENV_DOT_ENV_FILE", altPath)
            .expectThat { verifyAlt() }
    }

    @Test fun `dot env file can be specified via profile`() {
        EnvFileArk()
            .parse("--arkenv-profile", "placeholder")
            .expectThat { verify() }
    }

    private fun Assertion.Builder<EnvFileArk>.verify() {
        get { mysqlPassword }.isEqualTo("this_is_expected")
        get { port }.isEqualTo(5050)
        get { connectionString }.isEqualTo("localhost:5050;database=testdb;user=testuser;")
    }

    private fun Assertion.Builder<EnvFileArk>.verifyAlt() {
        get { mysqlPassword }.isEqualTo("alternative")
        get { port }.isEqualTo(8080)
        get { connectionString }.isEqualTo("localhost")
    }

    private val altPath = getTestResourcePath(".env-alt")
}
