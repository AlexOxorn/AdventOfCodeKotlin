package ox.lib.util

open class Grid2D<T> {
    protected var listData: MutableList<T> = ArrayList()
    protected var width: Int = 0

    constructor(ranges: Iterable<Collection<T>>) {
        for (rng in ranges) {
            listData.addAll(rng)
            if (width == 0)
                width = rng.size
        }
    }

    constructor(width: Int, range: Iterable<T>) {
        this.width = width
        listData = range.iterator().asSequence().toMutableList()
    }

    constructor(width: Int, height: Int = width, generator: (Int) -> T) {
        this.width = width
        this.listData = MutableList(width * height, generator)
    }

    operator fun get(x: Int, y: Int): T {
        return listData[y * width + x];
    }
    operator fun set(x: Int, y: Int, assign: T) {
        listData[y * width + x] = assign
    }
}