package com.apurebase.arkenv

/**
 * Parses available data for argument delegates
 */
interface ArkenvParser {
    fun parse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String?
}


