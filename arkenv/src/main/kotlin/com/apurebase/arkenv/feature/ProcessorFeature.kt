package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv

/**
 * A feature that defines processing logic that is used to transform parsed configuration data.
 * @since 2.1.0
 */
interface ProcessorFeature {

    var arkenv: Arkenv

    /**
     * Maps the [key] and [value] combination to a new value.
     */
    fun process(key: String, value: String): String

}
