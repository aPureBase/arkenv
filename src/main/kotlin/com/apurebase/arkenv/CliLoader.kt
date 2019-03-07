package com.apurebase.arkenv

fun loadCliAssignments(arkenv: Arkenv) {
    val args = arkenv.argList
    val names = arkenv.delegates.flatMap { it.argument.names }.map { it.trimStart('-') }
    var i = 0
    while (i < args.size) {
        val value = args[i]
        val spl = value.split('=')
        if (spl.size == 2 && names.contains(spl[0])) {
            args.removeAt(i)
            arkenv.dotEnv[spl.first().toUpperCase()] = spl.getOrNull(1) ?: ""
        } else i++
    }
}
