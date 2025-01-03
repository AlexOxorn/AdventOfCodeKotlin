package ox.lib.itertools

fun <T, R> Iterable<T>.cartesianProduct(other: Iterable<R>) = this.flatMap { a -> other.map { b -> a to b } }
infix fun <T, R> Iterable<T>.cross(other: Iterable<R>) = this.cartesianProduct(other)

fun <T> cartesianProduct(vararg iterables: Iterable<T>): List<List<T>> {
    return iterables.fold(listOf(emptyList())) { acc, iterable ->
        acc.flatMap { list ->
            iterable.map { element -> list + element }
        }
    }
}