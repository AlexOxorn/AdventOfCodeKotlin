package ox.puzzles

import java.io.File
import java.util.*

class ScanIterable<T>(filename: String, private val parseFunc: (Scanner) -> T) : Iterable<T>,
    Iterator<T> {
    private var cached: T? = null
    var scan: Scanner = Scanner(File(filename))
        private set

    private fun cache() {
        cached = try {
            parseFunc(scan);
        } catch (e: InputMismatchException) {
            null
        } catch (e: NoSuchElementException) {
            null
        }
    }

    override fun iterator(): Iterator<T> {
        return this
    }

    override fun hasNext(): Boolean {
        if (cached != null)
            return true
        cache()
        return cached != null
    }

    override fun next(): T {
        val cachedCopy = cached
        if (cachedCopy != null) {
            cached = null
            return cachedCopy
        }
        return parseFunc(scan);
    }
}