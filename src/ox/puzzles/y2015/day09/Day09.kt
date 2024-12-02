package ox.puzzles.y2015.day09

import ox.puzzles.Day
import java.util.regex.Pattern


class Day09 : Day {
    companion object {
        private val cityMap = HashMap<String, HashMap<String, Long>>()
        fun addLine(s: String) {
            val r: Pattern = Pattern.compile("(\\w+) to (\\w+) = (\\d+)")
            val m = r.matcher(s)
            m.find()
            if (!cityMap.contains(m.group(1)))
                cityMap[m.group(1)] = HashMap()
            if (!cityMap.contains(m.group(2)))
                cityMap[m.group(2)] = HashMap()
            cityMap[m.group(1)]!![m.group(2)] = m.group(3).toLong()
            cityMap[m.group(2)]!![m.group(1)] = m.group(3).toLong()
        }
    }

    private val answerCache = HashMap<Pair<String, List<String>>, Long?>()

    private fun solvePath(
        current: String,
        remaining: List<String>,
        call: (List<Pair<Long, Long?>>, (Pair<Long, Long?>) -> Long) -> Long?): Long? {

        if (remaining.isEmpty())
            return 0

        if (answerCache.contains(current to remaining)) {
            return answerCache[current to remaining]!!
        }

        val waysOut = cityMap[current]!!
        val destinations = remaining
            .filter { waysOut.contains(it) }
            .map { waysOut[it]!! to solvePath(it, remaining.filter { v: String -> v != it }, call) }
            .filter { it.second != null }

        val min = call(destinations) { it.first + it.second!! }
        answerCache[current to remaining] = min
        return min
    }

    private fun solveShortestPath(current: String, remaining: List<String>): Long? {
        return solvePath(current, remaining, List<Pair<Long, Long?>>::minOfOrNull)
    }

    private fun solveLongestPath(current: String, remaining: List<String>): Long? {
        return solvePath(current, remaining, List<Pair<Long, Long?>>::maxOfOrNull)
    }

    override fun part1(): Long {
        answerCache.clear()
        getBufferedReader(getInputName()).lines().forEach(Day09::addLine)
        val cities = cityMap.keys.toList()
        return cities.mapNotNull { solveShortestPath(it, cities.filterNot { v: String -> v == it }) }.min()
    }

    override fun part2(): Long {
        answerCache.clear()
        getBufferedReader(getInputName()).lines().forEach(Day09::addLine)
        val cities = cityMap.keys.toList()
        return cities.mapNotNull { solveLongestPath(it, cities.filterNot { v: String -> v == it }) }.max()
    }
}

operator fun <T: Comparable<T>> Iterable<T>.compareTo(other: Iterable<T>): Int {
    val first = iterator()
    val second = other.iterator()

    while (first.hasNext() && second.hasNext()) {
        val cmp = first.next().compareTo(second.next())
        if (cmp != 0) return cmp
    }
    if (!first.hasNext() && !second.hasNext())
        return 0
    if (!first.hasNext())
        return -1
    return 1
}


fun main() {
    val d = Day09()
    println(d.part1())
    println(d.part2())
}