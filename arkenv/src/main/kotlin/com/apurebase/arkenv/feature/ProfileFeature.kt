package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.getFeature
import com.apurebase.arkenv.split

/**
 * Feature for loading profile-based configuration.
 * A list of active profiles can be configured via the *ARKENV_PROFILE* argument.
 * @param prefix the default prefix for any profile configuration files, can be set via *ARKENV_PROFILE_PREFIX*
 * @param locations defines the default list of locations in which to look for profile configuration files,
 * can be set via *ARKENV_PROFILE_LOCATION*
 * @param parsers additional providers for profile file parsing. By default supports the property format.
 */
class ProfileFeature(
    private var prefix: String = "application",
    private var locations: Collection<String> = listOf(),
    parsers: Collection<PropertyParser> = listOf()
) : ArkenvFeature {

    private val parsers: MutableList<PropertyParser> = mutableListOf(::PropertyFeature)

    init {
        this.parsers.addAll(parsers)
    }

    var active: List<String> = listOf()
        private set

    override fun onLoad(arkenv: Arkenv) {
        active = arkenv.getOrNull("--arkenv-profile")?.split() ?: emptyList()
        prefix = arkenv.getOrNull("--arkenv-profile-prefix") ?: prefix
        locations = arkenv.getOrNull("--arkenv-profile-location")?.split() ?: locations
        load(arkenv, null)
        active.forEach { load(arkenv, it) }
    }

    private fun load(arkenv: Arkenv, file: String?) = parsers
        .map { it(makeFileName(file), locations) }
        .forEach { it.onLoad(arkenv) }

    private fun makeFileName(profile: String?) =
        if (profile != null) "$prefix-$profile"
        else prefix
}

typealias PropertyParser = (String, Collection<String>) -> PropertyFeature

val Arkenv.profiles get() = getFeature<ProfileFeature>()
