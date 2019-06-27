package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Arkenv

interface ProcessorFeature {

    var arkenv: Arkenv

    fun process(key: String, value: String): String

}
