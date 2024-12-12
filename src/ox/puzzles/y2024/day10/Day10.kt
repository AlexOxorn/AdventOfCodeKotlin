package ox.puzzles.y2024.day10
import ox.lib.util.DigitGrid
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*


class Day10 : Day {
    val grid = DigitGrid(ResourceIterable(getInputName(), Scanner::nextLine))
    private val dp = HashMap<GridIndex, Set<GridIndex>>()
    private val dp2 = HashMap<GridIndex, Set<List<GridIndex>>>()

    private fun getTrailTails(pos: GridIndex): Set<GridIndex> {
        if (dp.contains(pos))
            return dp[pos]!!

        if (grid[pos] == 9)
            return setOf(pos)

        val res = pos.cardinal()
            .asSequence()
            .filter(grid::checkBounds)
            .filter { grid[it] - grid[pos] == 1 }
            .map(this::getTrailTails)
            .fold(HashSet<GridIndex>(), Set<GridIndex>::union)

        dp[pos] = res
        return res
    }

    private fun getTrailPaths(pos: GridIndex): Set<List<GridIndex>> {
        if (dp2.contains(pos))
            return dp2[pos]!!

        if (grid[pos] == 9)
            return setOf(listOf(pos))

        val res = pos.cardinal()
            .asSequence()
            .filter(grid::checkBounds)
            .filter { grid[it] - grid[pos] == 1 }
            .map {
                getTrailPaths(it).map { sub -> listOf(pos) + sub }.toSet()
            }
            .fold(HashSet<List<GridIndex>>(), Set<List<GridIndex>>::union)

        dp2[pos] = res
        return res
    }

    override fun part1i(): Int {
        return grid.withIndex()
            .filter { (i, c) -> c == 0 }
            .map { (i, c) -> getTrailTails(i) }
            .sumOf { it.count() }
    }

    override fun part2i(): Int {
        return grid.withIndex()
            .filter { (i, c) -> c == 0 }
            .map { (i, c) -> getTrailPaths(i) }
            .sumOf { it.count() }
    }

}

fun main() {
    val d = Day10()
    println(d.part1())
    println(d.part2())
}