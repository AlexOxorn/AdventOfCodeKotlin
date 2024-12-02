package ox.lib.itertools

inline fun <T, R> Iterable<T>.zipWithNext(n: Int, transform: (a: T, b: T) -> R): List<R> {
    val iterator = iterator()
    if (!iterator.hasNext()) return emptyList()

    val offQueue = ArrayDeque<T>()

    val result = mutableListOf<R>()
    offQueue.addLast(iterator.next())
    for (i in 1..<n) {
        if (iterator.hasNext())
            offQueue.addLast(iterator.next())
        else
            return emptyList()
    }
    while (iterator.hasNext()) {
        offQueue.addLast(iterator.next())
        result.add(transform(offQueue.first(), offQueue.last()))
        offQueue.removeFirst()
    }
    return result
}

fun <T> Iterable<T>.zipWithNext(n: Int): List<Pair<T, T>> {
    return zipWithNext(n) { a, b -> a to b }
}