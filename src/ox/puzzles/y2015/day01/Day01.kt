package ox.puzzles.y2015.day01

import ox.puzzles.Day

fun charToVal(c: Char): Int {
    return when(c) {
        '(' -> 1
        ')' -> -1
        else -> 0
    }
}

class Day01 : Day {
    override fun part1i() : Int {
        val line = getBufferedReader(getInputName()).readText()
        return line.map(::charToVal).sum()
    }

    override fun part2i() : Int {
        val line = getBufferedReader(getInputName()).readText()
        val result = line.map(::charToVal).runningFold(0, Int::plus).withIndex().first { it.value < 0 }
        return result.index
    }
}

fun main() {
    println(Day01().part1())
    println(Day01().part2())
}