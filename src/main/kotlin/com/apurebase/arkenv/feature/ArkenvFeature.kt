package com.apurebase.arkenv.feature

import com.apurebase.arkenv.Argument
import com.apurebase.arkenv.ArgumentDelegate
import com.apurebase.arkenv.Arkenv

interface ArkenvFeature {

    fun onLoad(arkenv: Arkenv) {

    }

    fun onParse(arkenv: Arkenv, delegate: ArgumentDelegate<*>): String? {
        return null
    }

    fun configure(argument: Argument<*>) {

    }
}