package com.apurebase.arkenv

fun String.toSnakeCase() = this
    .replace("([a-z])([A-Z]+)".toRegex(), "$1_$2")
    .replace(".", "_").replace("-", "_")
    .toUpperCase()
    .removePrefixes("_")

fun String.removePrefixes(prefix: CharSequence): String = this
    .removePrefix(prefix)
    .let {
        if (it.startsWith(prefix)) it.removePrefixes(prefix)
        else it
    }

fun <T> Sequence<T>.takeUntil(pred: (T) -> Boolean): Sequence<T> {
    var shouldContinue = true
    return takeWhile {
        val result = shouldContinue
        shouldContinue = pred(it)
        result
    }
}

fun String.endsWith(list: Iterable<String>): Boolean {
    list.forEach {
        if (this.endsWith(it)) return true
    }
    return false

}

fun String.startsWith(list: Iterable<String>): Boolean {
    list.forEach {
        if (this.startsWith(it)) return true
    }
    return false
}

fun String.contains(list: Iterable<String>): Boolean {
    list.forEach {
        if (this.contains(it)) return true
    }
    return false
}

fun String.removeSurrounding(list: Iterable<String>): String {
    var result = this
    list.forEach {
        result = result.removeSurrounding(it)
    }
    return result
}
