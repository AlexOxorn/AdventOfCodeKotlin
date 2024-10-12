package ox.lib.itertools

fun <T, R> Iterable<T>.cartesianProduct(other: Iterable<R>) = this.flatMap { a -> other.map { b -> a to b } }