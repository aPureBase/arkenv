package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.parse

open class ProfileFeature(
    name: String = "--arkenv-profile",
    prefix: String = "application",
    locations: Collection<String> = listOf()
) : ArkenvFeature, Arkenv("ProfileFeature") {

    protected open val profile: String? by argument(name)

    protected open val prefix: String by argument("--arkenv-profile-prefix") {
        defaultValue = { prefix }
    }

    protected open val extension = "properties"

    protected open val location: Collection<String> by argument("--arkenv-profile-location") {
        mapping = { it.split(",").map(String::trim) }
        defaultValue = { locations }
    }

    override fun onLoad(arkenv: Arkenv) {
        parse(arkenv.argList.toTypedArray())
        load(arkenv, null)
        profile?.let { load(arkenv, it) }
    }

    private fun load(
        arkenv: Arkenv,
        file: String?
    ) = PropertyFeature(makeFileName(file), location).onLoad(arkenv)

    protected open fun makeFileName(profile: String?) =
        if (profile != null) "$prefix-$profile.$extension"
        else "$prefix.$extension"
}
