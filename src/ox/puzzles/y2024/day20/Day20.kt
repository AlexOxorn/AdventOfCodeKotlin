package ox.puzzles.y2024.day20

import ox.lib.itertools.cartesianProduct
import ox.lib.util.CharacterGrid
import ox.lib.util.DijkstraSolver
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

fun CharacterGrid.getNeighbours(position: GridIndex) = position
    .cardinal()
    .asSequence()
    .filter(this::checkBounds)
    .filter { this[it] != '#' }
    .map { it to 1L }.asIterable()

class Day20 : Day {
    private val grid = CharacterGrid(ResourceIterable(getInputName(), Scanner::nextLine))
    private val start = grid.withIndex().find { (i, c) -> c == 'S' }!!.first
    private val end = grid.withIndex().find { (i, c) -> c == 'E' }!!.first
    private val dijkstra = DijkstraSolver(
        listOf(start),
        { s -> s == end },
        grid::getNeighbours,
        0L,
        Long::compareTo,
        Long::plus
    ).trackPath()

    private val baseResult = dijkstra()
    private val baseResultPairs =
        baseResult.cartesianProduct(baseResult)
            .asSequence()
            .filter { (i, j) -> i.second < j.second }
            .map { (i, j) -> (i.first manhattan j.first) to (j.second - i.second) }
            .filter { (cheat, real) -> real - cheat >= 100 }
            .map { (cheat, real) -> cheat }
            .toList()

    override fun part1i(): Int {
        return baseResultPairs.count { it == 2 }
    }

    override fun part2i(): Int {
        return baseResultPairs.count { it <= 20 }
    }
}

fun main() {
    val d = Day20()
    println(d.part1())
    println(d.part2())
}