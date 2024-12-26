package ox.puzzles.y2024.day25

import ox.lib.itertools.cross
import ox.lib.util.CharacterGrid
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import ox.puzzles.ScanIterable
import java.util.Scanner

fun parseGrid(s: Scanner): CharacterGrid {
    val grid = ScanIterable(s, Scanner::nextLine).takeWhile { it.isNotEmpty(); }
    if (grid.isEmpty())
        throw NoSuchElementException()
    return CharacterGrid(grid)
}

fun gridToSet(g: CharacterGrid) = g.withIndex()
        .filter { (i, c) -> c == '#' }
        .map { (i, c) -> i }
        .toSet()

infix fun <T> Set<T>.xor(that: Set<T>): Set<T> = (this - that) + (that - this)

class Day25 : Day {
    val tmp = ResourceIterable(getInputName(), ::parseGrid)
    val keys = tmp.map(::gridToSet)

    override fun part1(): Long {
        return (keys cross keys).count { (i, j) -> (i intersect j).isEmpty() }.toLong() / 2L
    }
}

fun main() {
    val d = Day25()
    println(d.part1())
}