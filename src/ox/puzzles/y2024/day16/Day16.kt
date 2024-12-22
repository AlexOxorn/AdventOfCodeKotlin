package ox.puzzles.y2024.day16

import ox.lib.util.*
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

class State(private val grid: CharacterGrid, val index: GridIndex, val dir: GridDir) {
    fun forward() = if (grid[index.move(dir)] != '#') State(grid, index.move(dir), dir) else null
    fun turnRight() = State(grid, index, dir.right())
    fun turnLeft() = State(grid, index, dir.left())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is State) return false
        return index == other.index && dir == other.dir
    }
    override fun hashCode() = Objects.hash(index, dir)
    override fun toString() = "($index|$dir)"
}

fun getNeighbours(s: State): List<Pair<State, Long>> {
    val temp = mutableListOf(
        s.turnLeft() to 1000L,
        s.turnRight() to 1000L
    )
    s.forward()?.let { temp.add(it to 1L) }
    return temp
}

class Day16 : Day {
    private val grid = CharacterGrid(ResourceIterable(getInputName(), Scanner::nextLine))
    private val start = grid.withIndex().find { (i, c) -> c == 'S' }!!.first
    private val end = grid.withIndex().find { (i, c) -> c == 'E' }!!.first
    private val dijkstra = SuperDijkstraSolver(
        listOf(State(grid, start, GridDir.RIGHT)),
        { s: State -> s.index == end },
        ::getNeighbours,
        0L,
        Long::compareTo,
        Long::plus,
        { _: State -> 0L }
    )

    private fun printPath(result: Collection<GridIndex>) {
        for ((y, r) in grid.rows().withIndex()) {
            for ((x, c) in r.withIndex()) {
                if (c == '#')
                    print("▉")
                else if (c == '.' && result.any { it == GridIndex(x, y) })
                    print("${27.toChar()}[31m▉${27.toChar()}[0m")
                else if (c == '.')
                    print(" ")
                else
                    print("${27.toChar()}[31m$c${27.toChar()}[0m")
            }
            println()
        }
    }

    override fun part1(): Long {
        dijkstra.trackPath()
        val result = dijkstra()
        printPath(result.map { it.first.index })
        return result.last().second
    }

    override fun part2(): Long {
        val paths = dijkstra
            .getBestPositions()
            .asSequence()
            .map { it.index }
            .toSet()
        printPath(paths)
        return paths.size.toLong()
    }

}

fun main() {
    val d = Day16()
    println(d.part1())
    println(d.part2())
}