package ox.lib.util

import ox.lib.itertools.cartesianProduct

open class Grid2D<T> {
    var listData: MutableList<T> = ArrayList()
        protected set
    var width: Int = 0
        protected set
    val height: Int
        get() = listData.size / width

    fun <R> copy(proj: (T) -> R): Grid2D<R> {
        return Grid2D(width, listData.map(proj))
    }

    fun copy(): Grid2D<T> {
        return Grid2D(width, listData)
    }

    fun rows(): Iterable<Iterable<T>> {
        return listData.asSequence().chunked(width).asIterable()
    }

    fun withIndex(): List<Pair<Pair<Int, Int>, T>> {
        return this.indices() zip listData
    }

    constructor(ranges: Iterable<Collection<T>>) {
        for (rng in ranges) {
            listData.addAll(rng)
            if (width == 0)
                width = rng.size
        }
    }

    constructor(width: Int, range: Iterable<T>) {
        this.width = width
        listData = range.toMutableList()
    }

    constructor(width: Int, height: Int = width, generator: (Int) -> T) {
        this.width = width
        this.listData = MutableList(width * height, generator)
    }

    private fun assertBounds(x: Int, y: Int) {
        if (!checkBounds(x, y))
            throw IndexOutOfBoundsException()
    }

    fun checkBounds(x: Int, y: Int): Boolean {
        if (x !in 0..<width)
            return false
        if (y !in 0..<height)
            return false
        return true
    }

    operator fun get(x: Int, y: Int): T {
        assertBounds(x, y)
        return listData[y * width + x]
    }

    operator fun set(x: Int, y: Int, assign: T) {
        assertBounds(x, y)
        listData[y * width + x] = assign
    }

    fun getOrNull(x: Int, y: Int): T? {
        if (!checkBounds(x, y)) {
            return null
        }
        return listData.getOrNull(y * width + x)
    }

    fun indices() = (0..<height).cartesianProduct(0..<width).map { (j, i) -> i to j }
}

class CharacterGrid(lines: Iterable<String>) : Grid2D<Char>(
    lines.asSequence().map { it.toList() }.asIterable()
)