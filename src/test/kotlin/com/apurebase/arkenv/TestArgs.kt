package com.apurebase.arkenv

class TestArgs(args: Array<String>) : Arkenv(args) {

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
        description = "This is a com.apurebase.arkenv.main arg, so no names"
    }

    val description: String? by argument("-d", "--description") {
        description = "Description"
        envVariable = "DESC"
    }
}

class MainArg(value: String) : Arkenv(arrayOf(value)) {
    val mainArg by mainArgument<String>()
}

class Nullable(args: Array<String>) : Arkenv(args) {

    val int: Int? by argument("-i", "--int")

    val str: String? by argument("-s", "--str")

}

class Arkuments(args: Array<String>) : Arkenv(args) {

    val configPath: String by argument("-c", "--config") {
        description = "The path to your config.yml"
    }

    val manualAuth: Boolean by argument("-ma", "--manual-auth")

    val doRefresh: Boolean by argument("-r", "--refresh") {
        description = "Refresh the Spotify access token"
    }

}

object ObjectArgs : Arkenv(arrayOf()) {

    val int by argument<Int>("-i")

    val optional: String? by argument(listOf("-o"))

}
