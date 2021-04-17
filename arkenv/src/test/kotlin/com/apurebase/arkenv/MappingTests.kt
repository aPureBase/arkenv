package com.apurebase.arkenv

import com.apurebase.arkenv.test.expectThat
import com.apurebase.arkenv.test.parse
import com.apurebase.arkenv.util.argument
import com.apurebase.arkenv.util.parse
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File
import java.nio.file.Path

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class MappingTests {

    @Test fun `list should map to List of String`() {
        val input = "by,default"
        val output = listOf("by", "default")
        object: Arkenv() {
            val list: List<String> by argument()
            val collection: Collection<String> by argument()
        }.parse("LIST", input, "COLLECTION", input).expectThat {
            get { list } isEqualTo output
            get { collection } isEqualTo output
        }
    }

    @Test fun `char should map`() {
        object : Arkenv() {
            val char: Char by argument()
        }.parse("CHAR", "OnlyTheFirstChar").char.expectThat {
            isEqualTo('O')
        }
    }

    @Test fun `IntArray should map`() {
        object : Arkenv() {
            val array: IntArray by argument()
        }.parse(argName, numericInput).array.expectThat {
            isEqualTo(numericOutput.toIntArray())
        }
    }

    @Test fun `CharArray should map`() {
        object : Arkenv() {
            val array: CharArray by argument()
        }.parse(argName, "abc").array.expectThat {
            isEqualTo(listOf('a', 'b', 'c').toCharArray())
        }
    }

    @Test fun `ShortArray should map`() {
        object : Arkenv() {
            val array: ShortArray by argument()
        }.parse(argName, numericInput).array.expectThat {
            isEqualTo(numericOutput.map(Int::toShort).toShortArray())
        }
    }

    @Test fun `LongArray should map`() {
        object : Arkenv() {
            val array: LongArray by argument()
        }.parse(argName, numericInput).array.expectThat {
            isEqualTo(numericOutput.map(Int::toLong).toLongArray())
        }
    }

    @Test fun `FloatArray should map`() {
        object : Arkenv() {
            val array: FloatArray by argument()
        }.parse(argName, floatingPointInput).array.expectThat {
            isEqualTo(floatingPointOutput.map(Double::toFloat).toFloatArray())
        }
    }

    @Test fun `DoubleArray should map`() {
        object : Arkenv() {
            val array: DoubleArray by argument()
        }.parse(argName, floatingPointInput).array.expectThat {
            isEqualTo(floatingPointOutput.toDoubleArray())
        }
    }

    @Test fun `BooleanArray should map`() {
        object : Arkenv() {
            val array: BooleanArray by argument()
        }.parse(argName, "True,False,true").array.expectThat {
            isEqualTo(listOf(true, false, true).toBooleanArray())
        }
    }

    @Test fun `ByteArray should map`() {
        object : Arkenv() {
            val array: ByteArray by argument()
        }.parse("--$argName", "-1,2,3").array.expectThat {
            isEqualTo(listOf(-1, 2, 3).map(Int::toByte).toByteArray())
        }
    }

    @Test fun `Path should map`() {
        val expectedPath = "C:\\"
        val configuration = object {
            val path: Path by argument()
        }

        Arkenv.parse(configuration, arrayOf("--path", expectedPath))

        expectThat(configuration) {
            get { path.toString() } isEqualTo expectedPath
        }
    }

    @Test fun `File should map`() {
        val expectedPath = "C:\\"
        val configuration = object {
            val file: File by argument()
        }

        Arkenv.parse(configuration, arrayOf("--file", expectedPath))

        expectThat(configuration) {
            get { file.toString() } isEqualTo expectedPath
        }
    }

    private val floatingPointInput = "1.1,29.92,-387.9999"
    private val floatingPointOutput = listOf(1.1, 29.92, -387.9999)
    private val numericInput = "1,-29,387"
    private val numericOutput = listOf(1, -29, 387)
    private val argName = "ARRAY"
}
