package com.apurebase.arkenv

internal class CliParser : ArkenvParser {
    override fun parse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? =
        delegate.index?.let {
            delegate.parsedArgs.getOrNull(it + 1)
        }
}
