package ox.lib.itertools

inline fun <T, R> Iterable<T>.inplaceFold(initial: R, operation: (R, T) -> Unit): R {
    val accumulator = initial
    for (element in this) operation(accumulator, element)
    return accumulator
}