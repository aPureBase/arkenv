package com.apurebase.arkenv

import com.apurebase.arkenv.util.split
import java.io.File
import java.net.URI
import java.net.URL
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.reflect.KClass

/**
 * Maps raw values to their types.
 */
internal object ArkenvMapper {
    /**
     * Maps the input [value] to an instance of [T] using [clazz] as a reference.
     * @throws IllegalArgumentException if the mapping is not supported or didn't succeed
     */
    @Suppress("UNCHECKED_CAST", "TooGenericExceptionCaught")
    internal fun <T> mapDefault(key: String, value: String, clazz: KClass<*>): T = try {
        map(key, value, clazz) as T
    } catch (ex: RuntimeException) {
        throw MappingException(key, value, clazz, ex)
    }

    internal fun map(key: String, value: String, clazz: KClass<*>): Any? = value.map(key, clazz)

    @Suppress("ComplexMethod")
    private fun String.map(key: String, clazz: KClass<*>): Any? = when (clazz) {
        Int::class -> toIntOrNull()
        Long::class -> toLongOrNull()
        String::class -> this
        Char::class -> firstOrNull()
        Boolean::class -> toBoolean() || this == "1"
        List::class, Collection::class -> split()
        IntArray::class -> split().map(String::toInt).toIntArray()
        ShortArray::class -> split().map(String::toShort).toShortArray()
        CharArray::class -> toCharArray()
        LongArray::class -> split().map(String::toLong).toLongArray()
        FloatArray::class -> split().map(String::toFloat).toFloatArray()
        DoubleArray::class -> split().map(String::toDouble).toDoubleArray()
        BooleanArray::class -> split().map(String::toBoolean).toBooleanArray()
        ByteArray::class -> split().map(String::toByte).toByteArray()
        Path::class -> Paths.get(this)
        File::class -> File(this)
        URL::class -> URL(this)
        URI::class -> URI(this)
        IntRange::class -> split("..").let { IntRange(it[0].toInt(), it[1].toInt()) }
        LongRange::class -> split("..").let { LongRange(it[0].toLong(), it[1].toLong()) }
        CharRange::class -> CharRange(this[0], this[3])
        else -> throw UnsupportedMappingException(key, clazz)
    }
}

