package ox.puzzles.y2015

import ox.puzzles.Day
import java.io.File

fun charToVal(c: Char): Int {
    return when(c) {
        '(' -> 1
        ')' -> -1
        else -> 0
    }
}

class Day01(override val filename: String) : Day {
    override fun part1() : Int {
        val line = File(filename).readText()
        return line.map(::charToVal).sum()
    }

    override fun part2() : Int {
        val line = File(filename).readText()
        val result = line.map(::charToVal).runningFold(0, Int::plus).withIndex().first { it.value < 0 }
        return result.index
    }
}

fun main() {
    println(Day01("inputs/2015/day01_input.txt").part1())
    println(Day01("inputs/2015/day01_input.txt").part2())
}