package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.parse

class ProfileFeature(name: String = "--profile") : ArkenvFeature, Arkenv() {

    private val profile: String? by argument(name)

    override fun onLoad(arkenv: Arkenv) {
        parse(arkenv.argList.toTypedArray())
        val defaultName = makeFileName(null)
        PropertyFeature(defaultName).onLoad(arkenv)
        profile?.let { makeFileName(it) }?.let { PropertyFeature(it).onLoad(arkenv) }
    }

    private fun makeFileName(profile: String?) =
        if (profile != null) "application-$profile.properties"
        else "application.properties"
}
