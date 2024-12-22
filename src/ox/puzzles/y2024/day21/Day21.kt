package ox.puzzles.y2024.day21

import ox.lib.util.CharacterGrid
import ox.lib.util.GridIndex
import ox.lib.util.SuperDijkstraSolver
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

val dirPad = CharacterGrid(" ^A<v>".chunked(3))
val numPad = CharacterGrid("789456123 0A".chunked(3))

data class State(val last: Char?, val end: GridIndex, val robot: GridIndex, var done: Boolean = false)

fun pushButton(move: Char, robot: GridIndex): Triple<Char, GridIndex, GridIndex?> {
    if (move != 'A')
        return Triple(move, robot.move(move), null)
    return Triple(move, robot, robot)
}

class Day21 : Day {
    private val inputs = ResourceIterable(getInputName(), Scanner::nextLine).toList()
    private val dp2 = HashMap<Pair<List<GridIndex>, GridIndex>, List<List<List<GridIndex>>>>()
    private val dp3 = HashMap<Pair<List<GridIndex>, Int>, Long>()

    private fun dijkstraSolver(start: GridIndex, it: GridIndex, g: CharacterGrid) = SuperDijkstraSolver(
        listOf(State(null, it, start)),
        { it.done },
        getNeighbours(g),
        0L,
        Long::compareTo,
        Long::plus
    )

    private fun getNeighbours(g: CharacterGrid) = { (last, end, robot, done): State ->
        if (done)
            emptyList()
        else if (last == 'A')
            listOf(State(null, end, robot, true) to 1L)
        else
            "^>v<A".asSequence()
                .map { pushButton(it, robot) }
                .filter { (it, new, c) -> g.getOrElse(new, ' ') != ' ' }
                .filter { (it, new, c) -> c == end || c == null }
                .map { (it, new, c) -> State(it, end, new) to 1L }
                .asIterable()
    }


    private fun getNext(input: List<GridIndex>, g: CharacterGrid = numPad): List<List<List<GridIndex>>> {
        val start = g.indexOf('A')!!

        if (dp2.contains(input to start))
            return dp2.getValue(input to start)

        val res = (listOf(start) + input)
            .zipWithNext { src, dest -> dijkstraSolver(src, dest, g).getBestPaths() }
            .map { part -> part.map { path -> path.mapNotNull { it.last?.let(dirPad::indexOf) } } }

        dp2[input to start] = res
        return res
    }

    private fun getShortestSize(input: List<GridIndex>, indirection: Int, grid: CharacterGrid = numPad): Long {
        if (dp3.contains(input to indirection))
            return dp3.getValue(input to indirection)

        if (indirection == 0)
            return input.size.toLong()

        val res = getNext(input, grid)
            .sumOf { part -> part.minOf { path -> getShortestSize(path, indirection - 1, dirPad) } }

        dp3[input to indirection] = res
        return res
    }

    private fun solve(iterations: Int) = inputs.sumOf {
        val short = getShortestSize(it.mapNotNull(numPad::indexOf), iterations)
        short * it.substring(0, it.length - 1).toLong()
    }

    override fun part1() = solve(3)

    override fun part2() = solve(26)
}

fun main() {
    val d = Day21()
    println(d.part1())
    println(d.part2())
}