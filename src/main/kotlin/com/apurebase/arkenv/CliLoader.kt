package com.apurebase.arkenv

fun loadCliAssignments(arkenv: Arkenv) {
    val args = arkenv.argList
    var i = 0
    while (i < args.size) {
        val value = args[i]
        if (value.contains('=')) {
            args.removeAt(i)
            val split = value.split('=')
            arkenv.dotEnv[split.first().toUpperCase()] = split.getOrNull(1) ?: ""
        } else i++
    }
}
