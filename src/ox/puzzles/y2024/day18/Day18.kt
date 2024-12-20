package ox.puzzles.y2024.day18

import ox.lib.util.CharacterGrid
import ox.lib.util.DijkstraSolver
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

class Day18 : Day {
    private val size = 70
    private val filename = getInputName()
    private val first = 1024
    private val bytes =
        ResourceIterable(filename, Scanner::nextLine)
            .map { it.split(',') }
            .map { it.map(String::toInt) }
    private val grid = CharacterGrid(size + 1, size + 1) { '.' }
    private lateinit var gridCopy: CharacterGrid
    private val solver = DijkstraSolver(
        listOf(GridIndex(0, 0)),
        { it == GridIndex(size, size) },
        { index ->
            index.cardinal()
                .filter { gridCopy.checkBounds(it) }
                .filter { gridCopy[it] != '#' }
                .map { it to 1 }
        },
        0,
        Int::compareTo,
        Int::plus
    )

    init {
        bytes
            .take(first)
            .forEach { (i, j) -> grid[i, j] = '#' }
    }

    override fun part1i(): Int {
        gridCopy = grid.copy()
        return solver().last().second
    }

    override fun part2s(): String {
        gridCopy = grid.copy()
        val (i, v) = bytes
            .subList(1024, bytes.size)
            .withIndex()
            .find { (i, v) ->
                gridCopy[v[0], v[1]] = '#'
                solver().isEmpty()
            }!!
        return "${v[0]},${v[1]}"
    }
}

fun main() {
    val d = Day18()
    println(d.part1())
    println(d.part2s())
}