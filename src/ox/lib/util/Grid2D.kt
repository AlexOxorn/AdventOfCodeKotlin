package ox.lib.util

import ox.lib.itertools.cartesianProduct

enum class GridDir {
    UP, DOWN, LEFT, RIGHT;

    fun opposite() = when (this) {
        UP -> DOWN
        DOWN -> UP
        LEFT -> RIGHT
        RIGHT -> LEFT
    }

    fun right() = when (this) {
        UP -> RIGHT
        DOWN -> LEFT
        LEFT -> UP
        RIGHT -> DOWN
    }

    fun left() = opposite().right()
}

data class GridIndex(val x: Int = 0, val y: Int = 0) {
    fun up(distance: Int = 1) = GridIndex(x, y - distance)
    fun down(distance: Int = 1) = GridIndex(x, y + distance)
    fun left(distance: Int = 1) = GridIndex(x - distance, y)
    fun right(distance: Int = 1) = GridIndex(x + distance, y)

    fun move(dir: GridDir, distance: Int = 1) = when (dir) {
        GridDir.UP -> up(distance)
        GridDir.DOWN -> down(distance)
        GridDir.LEFT -> left(distance)
        GridDir.RIGHT -> right(distance)
    }

    companion object {
        val dirs = listOf(
            GridDir.UP, GridDir.RIGHT, GridDir.DOWN, GridDir.LEFT
        )
    }

    fun cardinal() = listOf(up(), right(), down(), left())
    fun cardinalWithDir() = cardinal() zip dirs
}

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

    open fun copy(): Grid2D<T> {
        return Grid2D(width, listData)
    }

    fun rows(): Iterable<Iterable<T>> {
        return listData.asSequence().chunked(width).asIterable()
    }

    fun withIndex(): List<Pair<GridIndex, T>> {
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

    fun checkBounds(i: GridIndex): Boolean {
        return checkBounds(i.x, i.y)
    }

    operator fun get(x: Int, y: Int): T {
        assertBounds(x, y)
        return listData[y * width + x]
    }

    operator fun get(i: GridIndex): T {
        return get(i.x, i.y)
    }

    operator fun set(x: Int, y: Int, assign: T) {
        assertBounds(x, y)
        listData[y * width + x] = assign
    }

    operator fun set(i: GridIndex, assign: T) {
        return set(i.x, i.y, assign)
    }

    fun getOrNull(x: Int, y: Int): T? {
        if (!checkBounds(x, y)) {
            return null
        }
        return listData.getOrNull(y * width + x)
    }

    fun indices() = (0..<height).cartesianProduct(0..<width).map { (j, i) -> GridIndex(i, j) }

    fun indexOf(v: T) = indices().firstOrNull { this[it] == v }
}

class CharacterGrid : Grid2D<Char> {
    constructor(lines: Iterable<String>) : super(lines.asSequence().map { it.toList() }.asIterable())
    constructor(width: Int, height: Int = width, generator: (Int) -> Char) : super(width, height, generator)
    constructor(width: Int, range: Iterable<Char>) : super(width, range)
    override fun copy(): CharacterGrid {
        return CharacterGrid(width, listData)
    }

    override fun toString() = buildString {
        for (r in rows()) {
            r.forEach { if (it == '.') append('.') else append(it) }
            append('\n')
        }
    }
}

class DigitGrid(lines: Iterable<String>) : Grid2D<Int>(
    lines.asSequence().map { it.asIterable().map { char -> char - '0' } }.asIterable()
)