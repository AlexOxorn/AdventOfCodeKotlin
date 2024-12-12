package ox.puzzles.y2024.day08

import ox.lib.itertools.cartesianProduct
import ox.lib.util.CharacterGrid
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*

typealias Coord = Pair<Int, Int>
typealias AntennaMap = Map<Char, List<Coord>>

operator fun Coord.minus(other: Coord): Coord {
    return (first - other.first) to (second - other.second )
}
operator fun Coord.plus(other: Coord): Coord {
    return (first + other.first) to (second + other.second )
}
operator fun Coord.unaryMinus(): Coord {
    return -first to -second
}

class Day08 : Day {
    var grid: CharacterGrid
    var antennaMap: AntennaMap

    init {
        val input = ResourceIterable(getInputName(), Scanner::nextLine)
        grid = CharacterGrid(input)
        antennaMap = grid.withIndex().filterNot{ it.second == '.' }.groupBy({it.second}, {it.first.x to it.first.y})
    }

    private fun getLinearCombination(init: Coord, dir: Coord): List<Coord> {
        val toReturn = mutableListOf<Coord>()
        var curr = init
        while (grid.checkBounds(curr.first, curr.second)) {
            toReturn.add(curr)
            curr += dir
        }
        return toReturn
    }

    private fun findAntinodeOf(antenna: Char, part2: Boolean): Set<Coord> {
        val antennas = antennaMap.getValue(antenna)
        return antennas.cartesianProduct(antennas)
            .asSequence()
            .filterNot{(a, b) -> a == b}
            .flatMap { (a, b) ->
                val diff = b - a
                if (part2)
                    getLinearCombination(a, -diff) + getLinearCombination(b, diff)
                else
                    listOf(a - diff, b + diff)
            }
            .filter { (a, b) -> grid.checkBounds(a, b) }
            .toSet()
    }

    private fun solve(part2: Boolean = false): Int {
        val antinodes =
            antennaMap.keys
                .asSequence()
                .map{ findAntinodeOf(it, part2)}
                .fold(HashSet<Coord>(), Set<Coord>::union)

        return antinodes.count()
    }

    override fun part1i(): Int {
        return solve(false)
    }

    override fun part2i(): Int {
        return solve(true)
    }

}

fun main() {
    val d = Day08()
    println(d.part1())
    println(d.part2())
}