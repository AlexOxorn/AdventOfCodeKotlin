package ox.lib.util

open class InfiniteGrid2D<T> {
    protected var listData: MutableMap<Pair<Int, Int>, T> = HashMap()

    operator fun get(x: Int, y: Int): T {
        return listData.getValue(x to y)
    }
    operator fun set(x: Int, y: Int, assign: T) {
        listData[x to y] = assign
    }
    operator fun get(x: Pair<Int, Int>): T {
        return listData.getValue(x)
    }
    operator fun set(x: Pair<Int, Int>, assign: T) {
        listData[x] = assign
    }
}