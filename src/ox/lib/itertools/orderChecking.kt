package ox.lib.itertools

fun <T> Iterable<T>.isSorted(cmp: Comparator<T>): Boolean {
    return zipWithNext().all {(i, j) -> cmp.compare(i, j) != 1}
}

fun <T: Comparable<T>> Iterable<T>.isSorted(): Boolean {
    return isSorted {i, j -> i.compareTo(j)}
}