package com.apurebase.arkenv.test

import com.apurebase.arkenv.Arkenv
import com.apurebase.arkenv.argument
import com.apurebase.arkenv.mainArgument

class TestArgs : Arkenv() {

    val country: String by argument("-c", "--country") {
        description = "Country to extract"
    }
    val bool: Boolean by argument("-b", "--execute") {
        description = "Whether to execute"
    }

    val nullInt: Int? by argument("-ni", "--null-int") {
        description = "A nullable Int, which doesn't have to be declared"
    }

    val mainString: String by mainArgument {
        description = "This is a main arg, so no names"
    }

    val description: String? by argument("-d", "--description") {
        description = "Description"
        envVariable = "DESC"
    }
}

class MainArg : Arkenv() {
    val mainArg by mainArgument<String>()
}

class Nullable : Arkenv() {

    val int: Int? by argument("-i", "--int")

    val str: String? by argument("-s", "--str")

}

class Arkuments : Arkenv() {

    val configPath: String by argument("-c", "--config") {
        description = "The path to your config.yml"
    }

    val manualAuth: Boolean by argument("-ma", "--manual-auth")

    val doRefresh: Boolean by argument("-r", "--refresh") {
        description = "Refresh the Spotify access token"
    }

}

object ObjectArgs : Arkenv() {

    val int by argument<Int>("-i")

    val optional: String? by argument(listOf("-o"))

}

class Mixed : Arkenv() {
    val someArg: Int by argument("-sa", "--some-arg")

    val other = "val"

    fun someFun() {

    }

    val getter get() = 1

}

class CustomEnv : Arkenv() {
    val arg: String by argument("-a", "--arg") {
        envVariable = "TEST"
    }
}
