package ox.lib.functional

fun <X, R> makeFunctionCache(fn: (X) -> R): (X) -> R {
    val cache: MutableMap<X, R> = HashMap()
    return {
        cache.getOrPut(it) { fn(it) }
    }
}
