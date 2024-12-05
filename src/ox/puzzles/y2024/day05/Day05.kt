package ox.puzzles.y2024.day05

import ox.lib.itertools.isSorted
import ox.puzzles.Day
import ox.puzzles.ScanIterable
import java.util.*
import java.util.stream.Collectors
import kotlin.streams.asStream

fun parseRule(scan: Scanner): Pair<Int, Int> {
    val line = scan.nextLine()
    if (line.isBlank())
        throw NoSuchElementException()
    val pair = line.split("|").map(String::toInt)
    return pair[0] to pair[1]
}

fun parseUpdate(scan: Scanner) = scan.nextLine().split(",").map(String::toInt)

class Day05 : Day {
    private var rules: Map<Int, Set<Int>>
    private var valid: List<List<Int>>
    private var invalid: List<List<Int>>

    init {
        val scan = Scanner(getBufferedReader(getInputName()))
        rules = ScanIterable(scan, ::parseRule).asSequence().asStream().collect(
            Collectors.groupingBy(
                Pair<Int, Int>::first,
                Collectors.mapping(
                    Pair<Int, Int>::second, Collectors.toSet()
                )
            ))
        val (v, f) = ScanIterable(scan, ::parseUpdate).partition{ it.isSorted(this::lessThan) }
        valid = v
        invalid = f
    }

    private fun lessThan(l: Int, r: Int): Int {
        if (rules[l]?.contains(r) == true)
            return -1
        if (rules[r]?.contains(l) == true)
            return 1
        return 0
    }

    override fun part1(): Long {
        return valid.sumOf { it[it.size / 2] }.toLong()
    }


    override fun part2(): Long {
        return invalid
            .map { it.sortedWith(this::lessThan) }
            .sumOf { it[it.size / 2] }.toLong()
    }
}

fun main() {
    println(Day05().part1())
    println(Day05().part2())
}