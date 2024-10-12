package ox.puzzles

import java.util.*

interface Parsable<T> {
    fun parse(s: Scanner): T
}