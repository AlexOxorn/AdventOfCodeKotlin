package ox.puzzles

import java.io.BufferedReader
import java.io.File
import java.util.*

open class ScanIterable<T>(scan: Scanner, val parseFunc: (Scanner) -> T) : Iterable<T>,
    Iterator<T> {

    private var cached: T? = null
    var scan: Scanner = scan
        private set

    private fun cache() {
        cached = try {
            parseFunc(scan)
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
        return parseFunc(scan)
    }

    fun asSequence(): Sequence<T> {
        return iterator().asSequence()
    }

    fun getBufferedReader(filename: String): BufferedReader {
        return ScanIterable::class.java.getResourceAsStream(filename)!!.bufferedReader()
    }
}

class FileIterable<T>(filename: String, parseFunc: (Scanner) -> T) :
    ScanIterable<T>(Scanner(File(filename)), parseFunc)

class ResourceIterable<T>(filename: String, parseFunc: (Scanner) -> T) :
    ScanIterable<T>(
        Scanner(ScanIterable::class.java.getResourceAsStream(filename)!!.bufferedReader()),
        parseFunc
    )