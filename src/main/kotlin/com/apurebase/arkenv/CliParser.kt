package com.apurebase.arkenv

fun parseCli(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? =
    delegate.index?.let {
        delegate.parsedArgs.getOrNull(it + 1)
    }
