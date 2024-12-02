package ox.puzzles.y2024.day02

import ox.puzzles.Day
import ox.puzzles.ResourceIterable
import java.util.*
import kotlin.math.abs

fun parseText(s: Scanner): List<Int> {
    val line = s.nextLine().trimEnd()
    return line.split(' ').map(String::toInt)
}

enum class DIR {
    UNKNOWN, INCREASING, DECREASING;

    fun match(diff: Int): Boolean {
        return when (this) {
            UNKNOWN -> true
            INCREASING -> diff > 0
            DECREASING -> diff < 0
        }
    }

    fun from(diff: Int): DIR {
        return when {
            (this != UNKNOWN) -> this
            (diff > 0) -> INCREASING
            (diff < 0) -> DECREASING
            else -> UNKNOWN
        }
    }
}

fun isTolerant(
    report: List<Int>,
    first: Int,
    second: Int,
    except: Int? = null,
    dir: DIR = DIR.UNKNOWN,
): Boolean {
    if (second >= report.size) {
        return true
    }

    val diff = report[second] - report[first]
    val valid = abs(diff) in 1..3 && dir.match(diff)

    return (valid && isTolerant(report, second, second + 1, except, dir.from(diff))) ||
            (except != null && isTolerant(report, first, second + 1, second, dir))
}

fun isTolerant(report: List<Int>) =
    isTolerant(report, 0, 1) || isTolerant(report, 1, 2, 0)

fun isSafe(report: List<Int>) = isTolerant(report, 0, 1, report.size)

class Day02 : Day {
    override fun part1i(): Int {
        val reports = ResourceIterable(getInputName(), ::parseText)
        return reports.count(::isSafe)
    }

    override fun part2i(): Int {
        val reports = ResourceIterable(getInputName(), ::parseText)
        return reports.count(::isTolerant)
    }
}

fun main() {
    println(Day02().part1())
    println(Day02().part2())
}