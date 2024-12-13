package ox.lib.itertools

fun <T: Comparable<T>> Iterable<T>.minMax(): Pair<T, T> {
    val iterator = iterator()
    if (!iterator.hasNext()) throw NoSuchElementException()
    var minValue = iterator.next()
    var maxValue = minValue
    while (iterator.hasNext()) {
        val v = iterator.next()
        minValue = minOf(minValue, v)
        maxValue = maxOf(maxValue, v)
    }
    return minValue to maxValue
}