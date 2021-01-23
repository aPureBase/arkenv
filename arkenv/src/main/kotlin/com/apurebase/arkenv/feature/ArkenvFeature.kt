package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Argument
import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.ArkenvArgument

/**
 * Defines functionality for use in [Arkenv] instances.
 */
interface ArkenvFeature {

    /**
     * Loads configuration for use in the application.
     * Is called before processing and parsing.
     */
    fun onLoad(arkenv: Arkenv) {
    }

    /**
     * Executed after all features have been loaded.
     * Can be used for reactive loading, where one feature is enabled by another one.
     */
    fun postLoad(arkenv: Arkenv) {
    }

    /**
     * Used to assign a value to the [Argument] represented by [delegate].
     */
    fun onParse(arkenv: Arkenv, delegate: ArkenvArgument<*>): String? {
        return null
    }

    /**
     * Applies configuration to every [argument].
     */
    @Deprecated("Will be removed in future major version")
    fun configure(argument: Argument<*>) {
    }

    /**
     * Executes after processing and parsing.
     * Can be used for cleaning up resources.
     */
    fun finally(arkenv: Arkenv) {
    }
}
