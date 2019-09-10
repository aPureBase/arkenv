package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Argument
import com.apurebase.arkenv.ArgumentDelegate
import com.apurebase.arkenv.Arkenv

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
     * Used to assign a value to the [Argument] represented by [delegate].
     */
    fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? {
        return null
    }

    /**
     * Applies configuration to every [argument].
     */
    fun configure(argument: Argument<*>) {
    }

    /**
     * Executes after processing and parsing.
     * Can be used for cleaning up resources.
     */
    fun finally(arkenv: Arkenv) {
    }
}
