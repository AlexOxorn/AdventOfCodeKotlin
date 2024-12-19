package ox.puzzles.y2024.day16

import ox.lib.util.CharacterGrid
import ox.lib.util.DijkstraSolver
import ox.lib.util.GridDir
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

class SuperDijkstraSolver<Node, Cost>(
    aStart: List<Node>,
    aEnd: (Node) -> Boolean,
    aGetNeighbours: (Node) -> Iterable<Pair<Node, Cost>>,
    aNullCost: Cost,
    aCostCmp: (Cost, Cost) -> Int,
    aCostAdd: (Cost, Cost) -> Cost,
    aGetHeuristic: ((Node) -> Cost)? = null,
) : DijkstraSolver<Node, Cost>(aStart, aEnd, aGetNeighbours, aNullCost, aCostCmp, aCostAdd, aGetHeuristic) {
    private val cameFrom2: MutableMap<Node, MutableList<Node>> = mutableMapOf()

    private fun processNeighbours2(current: Node) {
        for ((neighbour, cost) in getNeighbours(current)) {
            val tentativeScore = costAdd(gScore.getValue(current), cost)
            val alreadyChecked = gScore.contains(neighbour)
            val cmpResult = if (alreadyChecked) costCmp(tentativeScore, gScore.getValue(neighbour)) else -1
            if (cmpResult > 0) {
                continue
            } else if (cmpResult == 0) {
                cameFrom2[neighbour]?.add(current)
            } else {
                cameFrom2[neighbour] = mutableListOf(current)
            }

            gScore[neighbour] = tentativeScore
            openSet.add(neighbour to costAdd(tentativeScore, getHeuristic(neighbour)))
        }
    }

    private fun allBestResults(from: Node, seen: MutableSet<Node> = mutableSetOf()): Set<Node> {
        if (seen.contains(from))
            return seen
        seen.add(from)
        cameFrom2[from]?.forEach { allBestResults(it, seen) }
        return seen
    }

    fun getBestPositions(): Set<Node> {
        reset()
        while (openSet.isNotEmpty()) {
            val (current, currentCost) = openSet.remove()
            debug(current, currentCost, openSet, gScore, cameFrom)
            processNeighbours2(current)
            if (sentinel(current)) {
                return allBestResults(current)
            }
        }
        throw Exception()
    }
}

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