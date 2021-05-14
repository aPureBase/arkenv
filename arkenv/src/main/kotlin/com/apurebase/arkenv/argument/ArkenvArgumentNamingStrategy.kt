package com.apurebase.arkenv.argument

/**
 * Determines how argument names are resolved.
 */
enum class ArkenvArgumentNamingStrategy {
    /**
     * Always include the parameter name, even when custom names are specified.
     */
    ParameterNameAlwaysIncluded,

    /**
     * Only include the parameter name when no custom names are specified.
     */
    ParameterNameOnlyIfNotSpecified
}