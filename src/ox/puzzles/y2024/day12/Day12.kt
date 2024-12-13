package ox.puzzles.y2024.day12

import ox.lib.util.Grid2D
import ox.lib.util.GridIndex
import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import kotlin.collections.ArrayDeque

typealias Plot = Pair<Char, Boolean>
typealias Garden = Grid2D<Plot>
typealias Region = Set<GridIndex>
typealias Area = Int
typealias Perimeter = Int
typealias Corners = Int

fun parseLine(s: Scanner): List<Plot> {
    return s.nextLine().asIterable().map { it to false }
}

fun cornerWindow(pos: GridIndex) = listOf(
    listOf(pos.up(), pos.up().right(), pos.right()),
    listOf(pos.up(), pos.up().left(), pos.left()),
    listOf(pos.down(), pos.down().right(), pos.right()),
    listOf(pos.down(), pos.down().left(), pos.left()),
)

val cornerPredicate = listOf(
    listOf(false, false, false),
    listOf(false, true, false),
    listOf(true, false, true),
)

fun cornerCount(region: Region, pos: GridIndex, type: Char): Int {
    return cornerWindow(pos).count {
        cornerPredicate.indexOf(
            it.map { pt -> region.contains(pt) }
        ) >= 0
    }
}

fun getRegion(garden: Garden, pos: GridIndex): Triple<Area, Perimeter, Corners> {
    val type = garden[pos].first
    val queue = ArrayDeque<GridIndex>()
    val seen = mutableSetOf<GridIndex>()
    queue.add(pos)

    while (queue.isNotEmpty()) {
        val top = queue.removeFirst()
        if (seen.contains(top))
            continue

        if (!garden.checkBounds(top))
            continue

        if (garden[top].first != type)
            continue

        seen.add(top)
        queue.addAll(top.cardinal())
    }
    seen.forEach { garden[it] = type to true }

    val perimeter = seen.sumOf { index ->
        index.cardinal().count { garden.getOrNull(it.x, it.y)?.first != type }
    }

    val corners = seen.sumOf { cornerCount(seen, it, type) }

    return Triple(seen.count(), perimeter, corners)
}

fun getRegions(garden: Garden): List<Triple<Area, Perimeter, Corners>> {
    return garden.copy()
        .indices()
        .asSequence()
        .filterNot { garden[it].second }
        .map { getRegion(garden, it) }
        .toList()
}

class Day12 : Day {
    private val garden = Grid2D(
        ResourceIterable(getInputName(), ::parseLine)
    )
    private val regions = getRegions(garden)

    override fun part1i() = regions.sumOf { (a, p, c) -> a * p }
    override fun part2i() = regions.sumOf { (a, p, c) -> a * c }
}

fun main() {
    val d = Day12()
    println(d.part2())
}