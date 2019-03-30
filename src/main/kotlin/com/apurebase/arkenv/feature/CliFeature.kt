package com.apurebase.arkenv.feature

import com.apurebase.arkenv.ArgumentDelegate
import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.mapRelaxed
import com.apurebase.arkenv.toSnakeCase
import kotlin.collections.set

class CliFeature : ArkenvFeature {

    override fun onLoad(arkenv: Arkenv) {
        arkenv.argList.replaceAll(String::mapRelaxed)
        loadCliAssignments(arkenv)
    }

    override fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? = parseCli(delegate)

    private fun loadCliAssignments(arkenv: Arkenv) {
        val names = arkenv.delegates.flatMap { it.argument.names }.map { it.trimStart('-') }
        var i = 0
        while (i < arkenv.argList.size) {
            val value = arkenv.argList[i]
            val spl = value.split('=')
            val key = spl.first().toSnakeCase()
            if (spl.size == 2 && names.contains(key)) {
                arkenv.argList.removeAt(i)
                arkenv.keyValue[key] = spl.getOrNull(1) ?: ""
            } else i++
        }
    }

    private fun parseCli(delegate: ArgumentDelegate<*>): String? =
        delegate.index?.let {
            delegate.parsedArgs.getOrNull(it + 1)
        }
}
