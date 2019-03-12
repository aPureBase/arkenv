package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.parse

open class ProfileFeature(name: String = "--profile", prefix: String = "application") : ArkenvFeature, Arkenv() {

    protected open val profile: String? by argument(name)

    protected open val prefix: String by argument("--arkenv-config-name") {
        defaultValue = { prefix }
    }

    protected open val extension = "properties"

    override fun onLoad(arkenv: Arkenv) {
        parse(arkenv.argList.toTypedArray())
        val defaultName = makeFileName(null)
        PropertyFeature(defaultName).onLoad(arkenv)
        profile?.let { makeFileName(it) }?.let { PropertyFeature(it).onLoad(arkenv) }
    }

    protected open fun makeFileName(profile: String?) =
        if (profile != null) "$prefix-$profile.$extension"
        else "$prefix.$extension"
}
