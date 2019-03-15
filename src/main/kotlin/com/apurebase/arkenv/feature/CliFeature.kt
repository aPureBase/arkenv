package com.apurebase.arkenv.feature

import com.apurebase.arkenv.ArgumentDelegate
import com.apurebase.arkenv.Arkenv

class CliFeature : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        loadCliAssignments(arkenv)
    }

    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? = parseCli(delegate)

    private fun loadCliAssignments(arkenv: Arkenv) {
        val args = arkenv.argList
        val names = arkenv.delegates.flatMap { it.argument.names }.map { it.trimStart('-') }
        var i = 0
        while (i < args.size) {
            val value = args[i]
            val spl = value.split('=')
            if (spl.size == 2 && names.contains(spl[0])) {
                args.removeAt(i)
                arkenv.keyValue[spl.first().toUpperCase().replace('-', '_')] = spl.getOrNull(1) ?: ""
            } else i++
        }
    }

    private fun parseCli(delegate: ArgumentDelegate<*>): String? =
        delegate.index?.let {
            delegate.parsedArgs.getOrNull(it + 1)
        }
}
