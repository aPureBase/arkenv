package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.parse

/**
 * Feature for loading profile-based configuration.
 * A list of active profiles can be configured via a custom [name] or the *ARKENV_PROFILE* argument.
 * @param name overrides the default name of the profile argument, can be set via *ARKENV_PROFILE*
 * @param prefix the default prefix for any profile configuration files, can be set via *ARKENV_PROFILE_PREFIX*
 * @param locations defines the default list of locations in which to look for profile configuration files,
 * can be set via *ARKENV_PROFILE_LOCATION*
 */
class ProfileFeature(
    name: String = "--arkenv-profile",
    prefix: String = "application",
    locations: Collection<String> = listOf()
) : ArkenvFeature, Arkenv("ProfileFeature") {

    internal val profiles: List<String> by argument(name) {
        defaultValue = ::emptyList
        mapping = { it.split(',') }
    }

    private val prefix: String by argument("--arkenv-profile-prefix") {
        defaultValue = { prefix }
    }

    private val extension = "properties"

    private val location: Collection<String> by argument("--arkenv-profile-location") {
        mapping = { it.split(",").map(String::trim) }
        defaultValue = { locations }
    }

    override fun onLoad(arkenv: Arkenv) {
        parse(arkenv.argList.toTypedArray())
        load(arkenv, null)
        profiles.forEach { load(arkenv, it) }
    }

    private fun load(arkenv: Arkenv, file: String?) = PropertyFeature(makeFileName(file), location).onLoad(arkenv)

    private fun makeFileName(profile: String?) =
        if (profile != null) "$prefix-$profile.$extension"
        else "$prefix.$extension"
}
