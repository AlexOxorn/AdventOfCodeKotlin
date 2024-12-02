package ox.lib.itertools

inline fun <T, R> Iterable<T>.inplaceFold(initial: R, operation: (R, T) -> Unit): R {
    for (element in this) operation(initial, element)
    return initial
}